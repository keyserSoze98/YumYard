package com.keysersoze.yumyard.data.repository

import com.keysersoze.yumyard.data.remote.response.RecipeApiService
import com.keysersoze.yumyard.data.remote.response.toRecipe
import com.keysersoze.yumyard.domain.model.Recipe
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val api: RecipeApiService
) {
    suspend fun search(query: String): List<Recipe> {
        val response = api.searchRecipes(query)
        return response.meals?.map { it.toRecipe() } ?: emptyList()
    }

    suspend fun getRandomRecipes(): List<Recipe> {
        return List(5) {
            api.getRandomRecipe().meals?.firstOrNull()?.toRecipe()
        }.filterNotNull()
    }
}