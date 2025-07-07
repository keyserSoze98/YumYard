package com.keysersoze.yumyard.presentation.screens.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.keysersoze.yumyard.domain.model.Recipe
import com.keysersoze.yumyard.domain.model.toFavorite
import com.keysersoze.yumyard.presentation.viewmodels.FavoriteViewModel
import com.keysersoze.yumyard.presentation.viewmodels.RecipeViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: RecipeViewModel = hiltViewModel(), navController: NavHostController) {
    val recipes by viewModel.recipes.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val query by viewModel.query.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "YumYard",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                HorizontalDivider(
                    modifier = Modifier.padding(8.dp),
                    thickness = 4.dp,
                    color = Color.Red
                )

                NavigationDrawerItem(
                    label = { Text("My Account") },
                    selected = false,
                    onClick = {
                        navController.navigate("account")
                        scope.launch { drawerState.close() }
                    },
                    icon = {
                        Icon(Icons.Default.Person, contentDescription = "My Account")
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Favorites") },
                    selected = false,
                    onClick = {
                        navController.navigate("favorites")
                        scope.launch { drawerState.close() }
                    },
                    icon = {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Your Recipes") },
                    selected = false,
                    onClick = {
                        navController.navigate("draft_recipes")
                        scope.launch { drawerState.close() }
                    },
                    icon = {
                        Icon(Icons.Default.Add, contentDescription = "Your Recipes")
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Exit App") },
                    selected = false,
                    onClick = {
                        exitProcess(0)
                    },
                    icon = {
                        Icon(Icons.Default.Close, contentDescription = "Favorites")
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("YumYard") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    viewModel.onQueryChange("")
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { viewModel.onQueryChange(it) },
                        label = { Text("Search Recipes") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search)
                    )

                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        recipes.isNotEmpty() -> {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(recipes) { recipe ->
                                    RecipeCard(
                                        recipe = recipe,
                                        onClick = {
                                            val encodedRecipe = URLEncoder.encode(
                                                Json.encodeToString(recipe),
                                                StandardCharsets.UTF_8.toString()
                                            )
                                            navController.navigate("details/$encodedRecipe")
                                        }
                                    )
                                }
                            }
                        }

                        else -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No recipes found 😞")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    Log.d("@@@Recipe", recipe.toString())
    val coroutineScope = rememberCoroutineScope()
    var isFav by remember { mutableStateOf(false) }

    LaunchedEffect(recipe.id) {
        isFav = viewModel.isFavorite(recipe.id)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(model = recipe.imageUrl),
                    contentDescription = recipe.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            if (isFav) {
                                viewModel.removeFromFavorites(recipe.toFavorite())
                            } else {
                                viewModel.addToFavorites(recipe.toFavorite())
                            }
                            isFav = !isFav
                        }
                    },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFav) Color.Red else Color.Gray
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(recipe.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Cuisine: ${recipe.cuisine}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(recipe.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
    }
}