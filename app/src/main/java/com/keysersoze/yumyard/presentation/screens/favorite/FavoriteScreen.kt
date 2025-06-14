package com.keysersoze.yumyard.presentation.screens.favorite

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.keysersoze.yumyard.presentation.viewmodels.FavoriteViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.keysersoze.yumyard.data.local.entities.toRecipe
import com.keysersoze.yumyard.presentation.screens.home.RecipeCard
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun FavoriteScreen(
    navController: NavController,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    if (favorites.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No favorite recipes yet 😢")
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(favorites) { favorite ->
                val basicRecipe = favorite.toRecipe()
                RecipeCard(
                    recipe = basicRecipe,
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val fullRecipe = viewModel.fetchFullRecipeById(favorite.id)
                                val encoded = URLEncoder.encode(
                                    Json.encodeToString(fullRecipe),
                                    StandardCharsets.UTF_8.toString()
                                )
                                navController.navigate("details/$encoded")
                            } catch (e: Exception) {
                                Toast.makeText(context, "Failed to load recipe", Toast.LENGTH_SHORT).show()
                                Log.d("@@@Exc", e.toString())
                            }
                        }
                    }
                )
            }
        }
    }
}