package com.keysersoze.yumyard.presentation.screens.favorite

import com.keysersoze.yumyard.util.adBanner.BannerAdView
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.keysersoze.yumyard.domain.model.toRecipe
import com.keysersoze.yumyard.presentation.screens.home.RecipeCard
import com.keysersoze.yumyard.presentation.viewmodels.FavoriteViewModel
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

    Box(modifier = Modifier.fillMaxSize()) {

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No favorite recipes yet 😢")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 70.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Start,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        text = "Favorites"
                    )
                }

                items(favorites) { favorite ->
                    val basicRecipe = favorite.toRecipe()
                    RecipeCard(
                        recipe = basicRecipe,
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    val fullRecipe = try {
                                        viewModel.fetchFullRecipeById(favorite.id)
                                    } catch (apiEx: Exception) {
                                        Log.d("@@@FavExc", "API fetch failed, trying Firestore: ${apiEx.message}")
                                        try {
                                            viewModel.fetchFullUserRecipeById(favorite.id)
                                        } catch (firestoreEx: Exception) {
                                            Log.d("@@@FavExc", "Firestore fetch also failed: ${firestoreEx.message}")
                                            throw firestoreEx
                                        }
                                    }

                                    val encoded = URLEncoder.encode(
                                        Json.encodeToString(fullRecipe),
                                        StandardCharsets.UTF_8.toString()
                                    )
                                    navController.navigate("details/$encoded")
                                } catch (finalEx: Exception) {
                                    Toast.makeText(context, "Failed to load recipe", Toast.LENGTH_SHORT).show()
                                    Log.d("@@@FavExc", finalEx.toString())
                                }
                            }
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(50.dp),
            contentAlignment = Alignment.Center
        ) {
            BannerAdView()
        }
    }
}