package com.keysersoze.yumyard.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keysersoze.yumyard.data.local.entities.FavoriteEntity
import com.keysersoze.yumyard.data.repository.FavoriteRepository
import com.keysersoze.yumyard.domain.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: FavoriteRepository
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteEntity>> =
        repository.getAllFavorites()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addToFavorites(recipe: FavoriteEntity) {
        viewModelScope.launch {
            repository.addToFavorites(recipe)
        }
    }

    fun removeFromFavorites(recipe: FavoriteEntity) {
        viewModelScope.launch {
            repository.deleteFromFavorites(recipe)
        }
    }

    suspend fun isFavorite(id: String): Boolean {
        return repository.isFavorite(id)
    }

    suspend fun fetchFullRecipeById(id: String): Recipe {
        return repository.fetchFullRecipeById(id)
    }
}
