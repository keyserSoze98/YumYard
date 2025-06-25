package com.keysersoze.yumyard.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_recipe_drafts")
data class UserRecipeDraftEntity(
    @PrimaryKey val id: String,
    val title: String = "",
    val cuisine: String = "",
    val imageUrl: String = "",
    val instructions: String = "",
    val ingredients: List<String> = emptyList(),
    val measures: List<String> = emptyList(),
    val lastUpdated: Long = System.currentTimeMillis()
)