package com.keysersoze.yumyard.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.keysersoze.yumyard.domain.model.Recipe

@Entity(tableName = "favorites")
class FavoriteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val imageUrl: String,
    val description: String,
    val cuisine: String
)

fun FavoriteEntity.toRecipe(): Recipe {
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