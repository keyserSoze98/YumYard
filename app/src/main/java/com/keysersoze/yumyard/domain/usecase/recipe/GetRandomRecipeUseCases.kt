package com.keysersoze.yumyard.domain.usecase.recipe

import com.keysersoze.yumyard.data.repository.recipe.RecipeRepository
import com.keysersoze.yumyard.domain.model.Recipe
import javax.inject.Inject

class GetRandomRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(): List<Recipe> {
        return repository.getRandomRecipes()
    }
}