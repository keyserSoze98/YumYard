package com.keysersoze.yumyard.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_recipe_drafts")
data class UserRecipeDraftEntity(
    @PrimaryKey val id: String,
    val title: String = "",
    val cuisine: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val steps: String = "",
    val ingredients: List<Pair<String, String>> = listOf(Pair("", "")),
    val lastUpdated: Long = System.currentTimeMillis()
)