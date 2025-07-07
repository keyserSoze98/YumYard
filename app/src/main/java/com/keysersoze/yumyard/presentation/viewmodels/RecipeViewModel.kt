package com.keysersoze.yumyard.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keysersoze.yumyard.domain.model.Recipe
import com.keysersoze.yumyard.domain.usecase.recipe.GetRandomRecipesUseCase
import com.keysersoze.yumyard.domain.usecase.recipe.GetUserRecipesUseCase
import com.keysersoze.yumyard.domain.usecase.recipe.SearchRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val searchRecipesUseCase: SearchRecipesUseCase,
    private val getRandomRecipesUseCase: GetRandomRecipesUseCase,
    private val getUserRecipesUseCase: GetUserRecipesUseCase
): ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        loadRandomRecipes()
        observeQuery()
    }

    private fun observeQuery() {
        viewModelScope.launch {
            _query
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isNotBlank()) {
                        loadRecipes(query)
                    } else {
                        loadRandomRecipes()
                    }
                }

        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    private suspend fun loadRecipes(query: String) {
        _loading.value = true
        try {
            val apiResults = searchRecipesUseCase(query)
            val userResults = getUserRecipesUseCase.searchRecipesByTitle(query)
            _recipes.value = apiResults + userResults
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _loading.value = false
        }
    }

    private fun loadRandomRecipes() {
        viewModelScope.launch {
            _loading.value = true

            val apiDeferred = async {
                try { getRandomRecipesUseCase() } catch (e: Exception) {
                    Log.e("@@@RandomRecipesApiFailure", e.toString())
                    emptyList()
                }
            }

            val userDeferred = async {
                try { getUserRecipesUseCase.getAllUserRecipes() } catch (e: Exception) {
                    Log.e("@@@RandomRecipesUserFailure", e.toString())
                    emptyList()
                }
            }

            _recipes.value = apiDeferred.await() + userDeferred.await()
            _loading.value = false
        }
    }
}