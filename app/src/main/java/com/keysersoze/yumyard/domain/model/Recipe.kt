package com.keysersoze.yumyard.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val cuisine: String = "",
    val imageUrl: String = "",
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList()
)

fun Recipe.toFavorite(): Favorite {
    return Favorite(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        description = this.description,
        cuisine = this.cuisine
    )
}