package com.keysersoze.yumyard.domain.usecase

import com.keysersoze.yumyard.data.repository.RecipeRepository
import com.keysersoze.yumyard.domain.model.Recipe
import javax.inject.Inject

class SearchRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(query: String): List<Recipe> {
        return repository.search(query)
    }
}