package com.keysersoze.yumyard.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keysersoze.yumyard.domain.model.Recipe
import com.keysersoze.yumyard.domain.usecase.recipe.GetRandomRecipesUseCase
import com.keysersoze.yumyard.domain.usecase.recipe.GetUserRecipesUseCase
import com.keysersoze.yumyard.domain.usecase.recipe.SearchRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@OptIn(FlowPreview::class)
@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val searchRecipesUseCase: SearchRecipesUseCase,
    private val getRandomRecipesUseCase: GetRandomRecipesUseCase,
    private val getUserRecipesUseCase: GetUserRecipesUseCase
) : ViewModel() {

    private val pageSize = 20

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _loadingMore = MutableStateFlow(false)
    val loadingMore: StateFlow<Boolean> = _loadingMore.asStateFlow()

    private val _notice = MutableStateFlow<String?>(null)
    val notice: StateFlow<String?> = _notice.asStateFlow()

    private var cursor: Double = 0.0
    private var endReached = false
    private var searchMode = false
    private val seenIds = mutableSetOf<String>()

    init {
        observeQuery()
    }

    private fun observeQuery() {
        viewModelScope.launch {
            _query
                .debounce { if (it.isBlank()) 0L else 500L }
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) loadRandomFirstPage() else loadSearch(query)
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun refresh() {
        viewModelScope.launch {
            val current = _query.value
            if (current.isBlank()) loadRandomFirstPage() else loadSearch(current)
        }
    }

    fun loadMore() {
        if (searchMode || endReached || _loadingMore.value || _loading.value) return
        viewModelScope.launch {
            _loadingMore.value = true
            try {
                val page = getRandomRecipesUseCase(cursor, pageSize, false)
                val fresh = page.recipes.filter { seenIds.add(it.id) }
                if (fresh.isNotEmpty()) _recipes.value = _recipes.value + fresh
                cursor = page.nextCursor ?: cursor
                endReached = page.nextCursor == null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                endReached = true
            } finally {
                _loadingMore.value = false
            }
        }
    }

    private suspend fun loadRandomFirstPage() {
        searchMode = false
        endReached = false
        seenIds.clear()
        _loading.value = true
        _notice.value = null
        try {
            val pivot = Random.nextDouble()
            var page = getRandomRecipesUseCase(pivot, pageSize, true)
            if (page.recipes.isEmpty() && !page.fromCache) {
                page = getRandomRecipesUseCase(0.0, pageSize, true)
            }
            val userRecipes = try {
                getUserRecipesUseCase.getAllUserRecipes()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                emptyList()
            }
            _recipes.value = (page.recipes + userRecipes).filter { seenIds.add(it.id) }
            cursor = page.nextCursor ?: 0.0
            endReached = page.nextCursor == null
            _notice.value = when {
                !page.fromCache -> null
                page.limitReached -> "Daily recipe limit reached — showing saved recipes"
                else -> "You're offline — showing saved recipes"
            }
        } finally {
            _loading.value = false
        }
    }

    private suspend fun loadSearch(query: String) {
        searchMode = true
        endReached = true
        _loading.value = true
        _notice.value = null
        try {
            val trimmed = query.trim()
            coroutineScope {
                val apiDeferred = async {
                    try {
                        searchRecipesUseCase(trimmed)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
                val userDeferred = async {
                    try {
                        getUserRecipesUseCase.searchRecipesByTitle(trimmed.lowercase())
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
                _recipes.value = apiDeferred.await() + userDeferred.await()
            }
        } finally {
            _loading.value = false
        }
    }

}
