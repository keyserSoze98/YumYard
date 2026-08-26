package com.keysersoze.yumyard.data.repository.recipe

import com.keysersoze.yumyard.domain.model.Recipe
import com.keysersoze.yumyard.domain.model.RecipeFeedPage

interface RecipeRepository {
    suspend fun search(query: String): List<Recipe>
    suspend fun getRandomFeed(afterRandom: Double, limit: Int, isFirstPage: Boolean): RecipeFeedPage
}
