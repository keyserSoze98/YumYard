package com.keysersoze.yumyard.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object RecipeDetail : Screen("recipe_detail")
    object Splash : Screen("splash")
    object Login : Screen("login")

}