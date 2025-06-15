package com.keysersoze.yumyard.data.repository.recipe

import com.keysersoze.yumyard.domain.model.Recipe

interface RecipeRepository {
    suspend fun search(query: String): List<Recipe>
    suspend fun getRandomRecipes(): List<Recipe>
}