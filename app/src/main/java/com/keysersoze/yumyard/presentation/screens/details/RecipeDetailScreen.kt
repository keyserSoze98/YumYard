package com.keysersoze.yumyard.presentation.screens.details

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.keysersoze.yumyard.domain.model.Recipe
import com.keysersoze.yumyard.domain.model.toFavorite
import com.keysersoze.yumyard.presentation.viewmodels.FavoriteViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Composable
fun RecipeDetailScreen(
    recipeJson: String,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    Log.d("@@@recipeJson", recipeJson)
    val coroutineScope = rememberCoroutineScope()
    var isFav by remember { mutableStateOf(false) }

    val recipe = remember(recipeJson) {
        try {
            Json.decodeFromString<Recipe>(recipeJson)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val cleanedRecipe = remember(recipe) {
        recipe?.copy(
            title = recipe.title.replace("+", " "),
            description = recipe.description.replace("+", " "),
            cuisine = recipe.cuisine.replace("+", " "),
            ingredients = recipe.ingredients.map { it.replace("+", " ") },
            steps = recipe.steps.map {
                it.replace("+", " ")
                    .replace(Regex("^\\d+(\\.\\d+)?[\\s\\.]*"), "")
                    .trimStart()
            }
        )
    }

    if (cleanedRecipe == null) {
        Text("Oops! Recipe couldn't be loaded.", modifier = Modifier.padding(16.dp))
        return
    }

    LaunchedEffect(cleanedRecipe.id) {
        isFav = viewModel.isFavorite(cleanedRecipe.id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        IconButton(
            onClick = {
                coroutineScope.launch {
                    if (isFav) {
                        viewModel.removeFromFavorites(cleanedRecipe.toFavorite())
                    } else {
                        viewModel.addToFavorites(cleanedRecipe.toFavorite())
                    }
                    isFav = !isFav
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFav) Color.Red else Color.Gray
            )
        }

        Image(
            painter = rememberAsyncImagePainter(cleanedRecipe.imageUrl),
            contentDescription = cleanedRecipe.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = cleanedRecipe.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Cuisine: ${cleanedRecipe.cuisine}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = cleanedRecipe.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            text = "🧂 Ingredients",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        cleanedRecipe.ingredients.forEach {
            Text(text = "• $it", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "👨‍🍳 Steps",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 8.dp)
        )

        cleanedRecipe.steps.forEachIndexed { i, step ->
            Text(text = "${i + 1}. $step", style = MaterialTheme.typography.bodyMedium)
        }
    }
}