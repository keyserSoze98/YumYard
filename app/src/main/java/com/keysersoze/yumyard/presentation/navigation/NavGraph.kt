package com.keysersoze.yumyard.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.keysersoze.yumyard.presentation.screens.account.AccountScreen
import com.keysersoze.yumyard.presentation.screens.details.RecipeDetailScreen
import com.keysersoze.yumyard.presentation.screens.favorite.FavoriteScreen
import com.keysersoze.yumyard.presentation.screens.home.HomeScreen
import com.keysersoze.yumyard.presentation.screens.login.LoginScreen
import com.keysersoze.yumyard.presentation.screens.splash.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable("details/{recipeJson}") { backStackEntry ->
            val recipeJson = backStackEntry.arguments?.getString("recipeJson") ?: ""
            RecipeDetailScreen(recipeJson = recipeJson)
        }

        composable(route = Screen.Favorites.route) {
            FavoriteScreen(navController)
        }

        composable(route = Screen.Account.route) {
            AccountScreen(navController)
        }
    }
}