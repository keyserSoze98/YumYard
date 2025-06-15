package com.keysersoze.yumyard.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.keysersoze.yumyard.domain.model.Favorite

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val imageUrl: String,
    val description: String,
    val cuisine: String
)

fun FavoriteEntity.toDomain(): Favorite {
    return Favorite(
        id = id,
        title = title,
        imageUrl = imageUrl,
        description = description,
        cuisine = cuisine
    )
}