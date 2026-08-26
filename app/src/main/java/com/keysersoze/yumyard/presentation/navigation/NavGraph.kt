package com.keysersoze.yumyard.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.keysersoze.yumyard.presentation.screens.account.AccountScreen
import com.keysersoze.yumyard.presentation.screens.details.RecipeDetailScreen
import com.keysersoze.yumyard.presentation.screens.drafts.DraftsScreen
import com.keysersoze.yumyard.presentation.screens.editor.RecipeEditorScreen
import com.keysersoze.yumyard.presentation.screens.favorite.FavoriteScreen
import com.keysersoze.yumyard.presentation.screens.home.HomeScreen
import com.keysersoze.yumyard.presentation.screens.login.LoginScreen
import com.keysersoze.yumyard.presentation.screens.myrecipes.MyRecipesScreen
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

        composable(
            route = "details/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            RecipeDetailScreen(recipeId = recipeId, navController = navController)
        }

        composable(route = Screen.Favorites.route) {
            FavoriteScreen(navController)
        }

        composable(route = "my_recipes") {
            MyRecipesScreen(navController)
        }

        composable(route = Screen.Account.route) {
            AccountScreen(navController)
        }

        composable(route = Screen.DraftRecipes.route) {
            DraftsScreen(navController)
        }

        composable(
            route = "${Screen.AddEditRecipe.route}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val draftId = backStackEntry.arguments?.getString("id") ?: return@composable
            RecipeEditorScreen(draftId, navController)
        }
    }
}