package com.keysersoze.yumyard.data.repository

import com.keysersoze.yumyard.data.local.dao.FavoriteDao
import com.keysersoze.yumyard.data.local.entities.FavoriteEntity
import com.keysersoze.yumyard.data.remote.response.toRecipe
import com.keysersoze.yumyard.di.RecipeApiClient.api
import com.keysersoze.yumyard.domain.model.Recipe
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteRepository @Inject constructor(
    private val dao: FavoriteDao
) {
    fun getAllFavorites(): Flow<List<FavoriteEntity>> = dao.getAllFavorites()

    suspend fun addToFavorites(recipe: FavoriteEntity) = dao.addToFavorites(recipe)

    suspend fun deleteFromFavorites(recipe: FavoriteEntity) = dao.deleteFromFavorites(recipe)

    suspend fun isFavorite(id: String): Boolean = dao.isFavorite(id)

    suspend fun fetchFullRecipeById(id: String): Recipe {
        val response = api.getRecipeById(id)
        return response.meals?.firstOrNull()?.toRecipe()
            ?: throw Exception("Recipe not found")
    }
}