package com.keysersoze.yumyard.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keysersoze.yumyard.data.repository.RecipeRepository
import com.keysersoze.yumyard.domain.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeViewModel: ViewModel() {

    private val repository = RecipeRepository()

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        //loadDummyRecipes()
        loadRecipes()
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

    fun loadRecipes(query: String = "chicken") {
        viewModelScope.launch {
            _loading.value = true
            try {
                _recipes.value = repository.search(query)
            } catch (e: Exception) {
                Log.e("@@@RecipeVM", "API Failed", e)
            }
        }
    }
}