package com.keysersoze.yumyard.domain.usecase.favorite

import com.keysersoze.yumyard.data.repository.favorite.FavoriteRepository
import com.keysersoze.yumyard.domain.model.Recipe
import javax.inject.Inject

class FetchFullRecipeByIdUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(id: String): Recipe {
        return repository.fetchFullRecipeById(id)
    }
}