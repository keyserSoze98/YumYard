package com.keysersoze.yumyard.data.repository.recipe

import com.keysersoze.yumyard.data.remote.response.RecipeApiService
import com.keysersoze.yumyard.data.remote.response.toRecipe
import com.keysersoze.yumyard.domain.model.Recipe
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val api: RecipeApiService
) : RecipeRepository {
    override suspend fun search(query: String): List<Recipe> {
        val response = api.searchRecipes(query)
        return response.meals?.map { it.toRecipe() } ?: emptyList()
    }

    override suspend fun getRandomRecipes(): List<Recipe> {
        return List(20) {
            api.getRandomRecipe().meals?.firstOrNull()?.toRecipe()
        }.filterNotNull()
    }
}