package com.keysersoze.yumyard.domain.model

import com.keysersoze.yumyard.data.local.entities.FavoriteEntity

data class Favorite(
    val id: String,
    val title: String,
    val imageUrl: String,
    val description: String,
    val cuisine: String
)

fun Favorite.toEntity(): FavoriteEntity {
    return FavoriteEntity(
        id = id,
        title = title,
        imageUrl = imageUrl,
        description = description,
        cuisine = cuisine
    )
}

fun Favorite.toRecipe(): Recipe {
    return Recipe(
        id = this.id,
        title = this.title,
        description = this.description,
        cuisine = this.cuisine,
        imageUrl = this.imageUrl,
        ingredients = emptyList(),
        steps = emptyList()
    )
}