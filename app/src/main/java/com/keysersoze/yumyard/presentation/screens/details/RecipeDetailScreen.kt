package com.keysersoze.yumyard.presentation.screens.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.keysersoze.yumyard.presentation.viewmodels.RecipeViewModel

@Composable
fun RecipeDetailScreen(recipeId: String, viewModel: RecipeViewModel = viewModel()) {
    val recipes by viewModel.recipes.collectAsState()
    val recipe = recipes.find { it.id == recipeId }

    recipe?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = rememberAsyncImagePainter(recipe.imageUrl),
                contentDescription = recipe.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = recipe.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = recipe.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = "🧂 Ingredients",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            recipe.ingredients.forEach {
                Text(text = "• $it", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "👨‍🍳 Steps",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )

            recipe.steps.forEachIndexed { i, step ->
                Text(text = "${i + 1}. $step", style = MaterialTheme.typography.bodyMedium)
            }
        }
    } ?: run {
        Text(
            "Recipe not found!",
            modifier = Modifier.padding(16.dp)
        )
    }
}