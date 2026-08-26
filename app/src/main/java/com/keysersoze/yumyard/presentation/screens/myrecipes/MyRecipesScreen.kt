package com.keysersoze.yumyard.presentation.screens.myrecipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.keysersoze.yumyard.domain.model.Recipe
import com.keysersoze.yumyard.domain.model.toFavorite
import com.keysersoze.yumyard.presentation.components.EmptyRecipesState
import com.keysersoze.yumyard.presentation.components.RecipeCard
import com.keysersoze.yumyard.presentation.viewmodels.FavoriteViewModel
import com.keysersoze.yumyard.presentation.viewmodels.MyRecipesViewModel
import com.keysersoze.yumyard.ui.theme.YumCream
import com.keysersoze.yumyard.ui.theme.YumPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRecipesScreen(
    navController: NavController,
    viewModel: MyRecipesViewModel = hiltViewModel(),
    favoriteViewModel: FavoriteViewModel = hiltViewModel()
) {
    val recipes by viewModel.recipes.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val favoriteIds by favoriteViewModel.favoriteIds.collectAsState()
    var toDelete by remember { mutableStateOf<Recipe?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Recipes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = YumPurple,
                    titleContentColor = YumCream,
                    navigationIconContentColor = YumCream
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                recipes.isEmpty() -> {
                    EmptyRecipesState(
                        title = "No recipes yet",
                        subtitle = "Recipes you publish will show up here for everyone to discover.",
                        icon = Icons.Filled.MenuBook,
                        actionLabel = "Add a recipe",
                        onAction = { navController.navigate("draft_recipes") }
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(recipes) { recipe ->
                            RecipeCard(
                                recipe = recipe,
                                isFavorite = favoriteIds.contains(recipe.id),
                                onToggleFavorite = {
                                    if (favoriteIds.contains(recipe.id)) {
                                        favoriteViewModel.removeFromFavorites(recipe.toFavorite())
                                    } else {
                                        favoriteViewModel.addToFavorites(recipe.toFavorite())
                                    }
                                },
                                onClick = { navController.navigate("details/${recipe.id}") }
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { toDelete = recipe }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.size(6.dp))
                                    Text("Delete", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    toDelete?.let { recipe ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(recipe)
                    toDelete = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { toDelete = null }) { Text("Cancel") }
            },
            title = { Text("Delete recipe") },
            text = { Text("Delete \"${recipe.title}\"? This removes it for everyone and can't be undone.") }
        )
    }
}
