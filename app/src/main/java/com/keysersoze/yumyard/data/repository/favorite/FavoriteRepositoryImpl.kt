package com.keysersoze.yumyard.data.repository.favorite

import com.keysersoze.yumyard.data.local.dao.FavoriteDao
import com.keysersoze.yumyard.data.local.entities.toDomain
import com.keysersoze.yumyard.data.remote.response.RecipeApiService
import com.keysersoze.yumyard.data.remote.response.toRecipe
import com.keysersoze.yumyard.domain.model.Favorite
import com.keysersoze.yumyard.domain.model.Recipe
import com.keysersoze.yumyard.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val dao: FavoriteDao,
    private val api: RecipeApiService
) : FavoriteRepository {
    override fun getAllFavorites(): Flow<List<Favorite>> {
        return dao.getAllFavorites().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun addToFavorites(favorite: Favorite) {
        return dao.addToFavorites(favorite.toEntity())
    }

    override suspend fun deleteFromFavorites(favorite: Favorite) {
        return dao.deleteFromFavorites(favorite.toEntity())
    }

    override suspend fun isFavorite(id: String): Boolean = dao.isFavorite(id)

    override suspend fun fetchFullRecipeById(id: String): Recipe {
        val response = api.getRecipeById(id)
        return response.meals?.firstOrNull()?.toRecipe()
            ?: throw Exception("Recipe not found")
    }

    override suspend fun clearAllFavorites() = dao.clearAllFavorites()
}