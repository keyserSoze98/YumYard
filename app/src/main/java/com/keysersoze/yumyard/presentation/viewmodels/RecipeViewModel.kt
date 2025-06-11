package com.keysersoze.yumyard.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keysersoze.yumyard.data.repository.RecipeRepository
import com.keysersoze.yumyard.domain.model.Recipe
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class RecipeViewModel: ViewModel() {

    private val repository = RecipeRepository()

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
                    loadRecipes(query)
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    suspend fun loadRecipes(query: String) {
        _loading.value = true
        try {
            _recipes.value = repository.search(query)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _loading.value = false
        }
    }

    private fun loadRandomRecipes() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _recipes.value = repository.getRandomRecipes()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }

    private fun loadDummyRecipes() {
        _recipes.value = listOf(
            Recipe(
                id = "1",
                title = "Spaghetti Carbonara",
                description = "A classic Roman pasta dish with eggs, cheese, pancetta, and pepper.",
                imageUrl = "https://source.unsplash.com/featured/?spaghetti",
                ingredients = listOf("Spaghetti", "Eggs", "Pancetta", "Parmesan", "Black Pepper"),
                steps = listOf("Boil pasta", "Fry pancetta", "Mix with eggs & cheese", "Combine and serve")
            ),
            Recipe(
                id = "2",
                title = "Veggie Stir Fry",
                description = "Quick and colorful, packed with fresh veggies and a savory sauce.",
                imageUrl = "https://source.unsplash.com/featured/?stirfry",
                ingredients = listOf("Broccoli", "Carrots", "Bell Peppers", "Soy Sauce", "Ginger"),
                steps = listOf("Chop veggies", "Stir fry in oil", "Add sauce", "Serve hot")
            ),
            Recipe(
                id = "3",
                title = "Mango Smoothie",
                description = "A tropical burst of flavor to brighten your day.",
                imageUrl = "https://source.unsplash.com/featured/?mango,smoothie",
                ingredients = listOf("Mango", "Yogurt", "Honey", "Ice Cubes"),
                steps = listOf("Peel mango", "Blend all ingredients", "Serve chilled")
            )
        )
    }
}