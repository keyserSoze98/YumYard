package com.keysersoze.yumyard.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.keysersoze.yumyard.domain.model.Recipe
import com.keysersoze.yumyard.domain.usecase.recipe.GetUserRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRecipesViewModel @Inject constructor(
    private val getUserRecipesUseCase: GetUserRecipesUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            val uid = auth.currentUser?.uid
            _recipes.value = if (uid == null) {
                emptyList()
            } else {
                try {
                    getUserRecipesUseCase.getRecipesByAuthor(uid)
                } catch (e: Exception) {
                    emptyList()
                }
            }
            _loading.value = false
        }
    }

    fun delete(recipe: Recipe) {
        viewModelScope.launch {
            try {
                getUserRecipesUseCase.deleteRecipe(recipe.id)
                _recipes.value = _recipes.value.filterNot { it.id == recipe.id }
            } catch (e: Exception) {
            }
        }
    }
}
