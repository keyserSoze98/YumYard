package com.keysersoze.yumyard.domain.model

data class RecipeFeedPage(
    val recipes: List<Recipe>,
    val nextCursor: Double?,
    val fromCache: Boolean = false,
    val limitReached: Boolean = false
)
