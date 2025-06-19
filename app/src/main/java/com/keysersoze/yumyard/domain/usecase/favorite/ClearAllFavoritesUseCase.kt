package com.keysersoze.yumyard.domain.usecase.favorite

import com.keysersoze.yumyard.data.repository.favorite.FavoriteRepository
import javax.inject.Inject

class ClearAllFavoritesUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke() = repository.clearAllFavorites()
}