package com.keysersoze.yumyard.presentation.screens.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.app.Activity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.keysersoze.yumyard.domain.model.toRecipe
import com.keysersoze.yumyard.presentation.components.EmptyRecipesState
import com.keysersoze.yumyard.presentation.components.RecipeCard
import com.keysersoze.yumyard.presentation.viewmodels.FavoriteViewModel
import com.keysersoze.yumyard.ui.theme.YumCream
import com.keysersoze.yumyard.ui.theme.YumPurple
import com.keysersoze.yumyard.util.adBanner.AdCounters
import com.keysersoze.yumyard.util.adBanner.BannerAdView
import com.keysersoze.yumyard.util.adBanner.loadInterstitialAd
import com.keysersoze.yumyard.util.adBanner.showInterstitialAd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    navController: NavController,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (AdCounters.onFavoritesOpen()) {
            loadInterstitialAd(context) { ad ->
                showInterstitialAd(ad, context as Activity, onDone = {})
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites", fontWeight = FontWeight.Bold) },
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
            if (favorites.isEmpty()) {
                EmptyRecipesState(
                    title = "No favorites yet",
                    subtitle = "Tap the heart on any recipe to save it here for later.",
                    icon = Icons.Default.FavoriteBorder,
                    actionLabel = "Browse recipes",
                    onAction = { navController.popBackStack() }
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(favorites) { favorite ->
                        RecipeCard(
                            recipe = favorite.toRecipe(),
                            isFavorite = true,
                            onToggleFavorite = { viewModel.removeFromFavorites(favorite) },
                            onClick = { navController.navigate("details/${favorite.id}") }
                        )
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
    }
}
