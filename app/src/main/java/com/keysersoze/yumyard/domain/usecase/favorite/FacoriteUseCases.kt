package com.keysersoze.yumyard.domain.usecase.favorite

import javax.inject.Inject

data class FavoriteUseCases @Inject constructor(
    val getAllFavoritesUseCase: GetAllFavoritesUseCase,
    val addToFavoritesUseCase: AddToFavoritesUseCase,
    val deleteFromFavoritesUseCase: DeleteFromFavoritesUseCase,
    val isFavoriteUseCase: IsFavoriteUseCase,
    val fetchFullRecipeByIdUseCase: FetchFullRecipeByIdUseCase
)