package com.keysersoze.yumyard.presentation.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object RecipeDetail : Screen("recipe_detail")
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Favorites: Screen("favorites")
    data object Account: Screen("account")
    data object DraftRecipes: Screen("draft_recipes")
    data object AddEditRecipe: Screen("add_edit_recipe")
}