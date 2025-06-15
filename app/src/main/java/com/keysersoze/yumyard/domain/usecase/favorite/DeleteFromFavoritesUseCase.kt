package com.keysersoze.yumyard.domain.usecase.favorite

import com.keysersoze.yumyard.data.repository.favorite.FavoriteRepository
import com.keysersoze.yumyard.domain.model.Favorite
import javax.inject.Inject

class DeleteFromFavoritesUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(favorite: Favorite) {
        return repository.deleteFromFavorites(favorite)
    }
}