package com.keysersoze.yumyard.data.repository

import com.keysersoze.yumyard.data.remote.response.toRecipe
import com.keysersoze.yumyard.di.RecipeApiClient
import com.keysersoze.yumyard.di.RecipeApiClient.api
import com.keysersoze.yumyard.domain.model.Recipe

class RecipeRepository {
    suspend fun search(query: String): List<Recipe> {
        val response = RecipeApiClient.api.searchRecipes(query)
        return response.meals?.map { it.toRecipe() } ?: emptyList()
    }

    suspend fun getRandomRecipes(): List<Recipe> {
        return List(5) {
            RecipeApiClient.api.getRandomRecipe().meals?.firstOrNull()?.toRecipe()
        }.filterNotNull()
    }
}