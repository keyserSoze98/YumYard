package com.keysersoze.yumyard.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.keysersoze.yumyard.presentation.screens.home.HomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen()
        }

        // composable(route = Screen.RecipeDetail.route + "/{id}") { backStackEntry ->
        //     val id = backStackEntry.arguments?.getString("id")
        //     RecipeDetailScreen(id = id?.toIntOrNull() ?: -1)
        // }
    }
}