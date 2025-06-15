package com.keysersoze.yumyard.domain.usecase.favorite

import com.keysersoze.yumyard.data.repository.favorite.FavoriteRepository
import com.keysersoze.yumyard.domain.model.Favorite
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllFavoritesUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    operator fun invoke(): Flow<List<Favorite>> {
        return repository.getAllFavorites()
    }
}