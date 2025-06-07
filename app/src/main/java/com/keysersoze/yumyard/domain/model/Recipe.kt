package com.keysersoze.yumyard.domain.model

data class Recipe(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val ingredients: List<String>,
    val steps: List<String>
)
