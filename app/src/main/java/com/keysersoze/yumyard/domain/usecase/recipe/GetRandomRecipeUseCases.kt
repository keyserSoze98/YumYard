package com.keysersoze.yumyard.domain.usecase.recipe

import com.keysersoze.yumyard.data.repository.recipe.RecipeRepository
import com.keysersoze.yumyard.domain.model.RecipeFeedPage
import javax.inject.Inject

class GetRandomRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(afterRandom: Double, limit: Int, isFirstPage: Boolean): RecipeFeedPage {
        return repository.getRandomFeed(afterRandom, limit, isFirstPage)
    }
}
