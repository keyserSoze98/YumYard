package com.keysersoze.yumyard.domain.usecase.favorite

import com.keysersoze.yumyard.data.repository.favorite.FavoriteRepository
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(id: String): Boolean {
        return repository.isFavorite(id)
    }
}