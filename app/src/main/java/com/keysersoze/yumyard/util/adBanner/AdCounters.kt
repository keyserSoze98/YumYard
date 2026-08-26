package com.keysersoze.yumyard.util.adBanner

object AdCounters {
    private var recipeOpen = 0
    private var detailBack = 0
    private var favoritesOpen = 0

    fun onRecipeOpen(): Boolean = recipeOpen++ % 2 == 0

    fun onDetailBack(): Boolean = ++detailBack % 2 == 0

    fun onFavoritesOpen(): Boolean = ++favoritesOpen % 2 == 0
}
