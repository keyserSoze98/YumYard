package com.keysersoze.yumyard.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keysersoze.yumyard.domain.model.Favorite
import com.keysersoze.yumyard.domain.model.Recipe
import com.keysersoze.yumyard.domain.usecase.favorite.FavoriteUseCases
import com.keysersoze.yumyard.domain.usecase.recipe.GetFullUserRecipeByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteUseCases: FavoriteUseCases,
    private val getFullUserRecipeByIdUseCase: GetFullUserRecipeByIdUseCase
) : ViewModel() {

    val favorites: StateFlow<List<Favorite>> = favoriteUseCases.getAllFavoritesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addToFavorites(favorite: Favorite) {
        viewModelScope.launch {
            favoriteUseCases.addToFavoritesUseCase(favorite)
        }
    }

    fun removeFromFavorites(favorite: Favorite) {
        viewModelScope.launch {
            favoriteUseCases.deleteFromFavoritesUseCase(favorite)
        }
    }

    suspend fun isFavorite(id: String): Boolean {
        return favoriteUseCases.isFavoriteUseCase(id)
    }

    suspend fun fetchFullRecipeById(id: String): Recipe {
        return favoriteUseCases.fetchFullRecipeByIdUseCase(id)
    }

    suspend fun fetchFullUserRecipeById(id: String): Recipe {
        return getFullUserRecipeByIdUseCase.execute(id)
    }

    fun clearAllFavorites() {
        viewModelScope.launch {
            favoriteUseCases.clearAllFavoritesUseCase()
        }
    }
}