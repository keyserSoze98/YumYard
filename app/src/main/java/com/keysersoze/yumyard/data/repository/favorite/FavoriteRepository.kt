package com.keysersoze.yumyard.data.repository.favorite

import com.keysersoze.yumyard.domain.model.Favorite
import com.keysersoze.yumyard.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAllFavorites(): Flow<List<Favorite>>
    suspend fun addToFavorites(recipe: Favorite)
    suspend fun deleteFromFavorites(recipe: Favorite)
    suspend fun isFavorite(id: String): Boolean
    suspend fun fetchFullRecipeById(id: String): Recipe
}