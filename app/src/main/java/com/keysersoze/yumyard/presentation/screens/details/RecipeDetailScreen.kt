package com.keysersoze.yumyard.presentation.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.keysersoze.yumyard.domain.model.Recipe
import com.keysersoze.yumyard.domain.model.toFavorite
import com.keysersoze.yumyard.presentation.components.EmptyRecipesState
import com.keysersoze.yumyard.presentation.viewmodels.FavoriteViewModel
import com.keysersoze.yumyard.util.adBanner.AdCounters
import com.keysersoze.yumyard.util.adBanner.BannerAdView
import com.keysersoze.yumyard.util.adBanner.loadInterstitialAd
import com.keysersoze.yumyard.util.adBanner.showInterstitialAd
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.ads.interstitial.InterstitialAd

private val OnImageText = Color(0xFFF6EEE0)

@Composable
fun RecipeDetailScreen(
    recipeId: String,
    navController: NavHostController,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    var recipe by remember { mutableStateOf<Recipe?>(null) }
    var loading by remember { mutableStateOf(true) }
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    val context = LocalContext.current
    val activity = context as Activity
    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }

    LaunchedEffect(Unit) {
        loadInterstitialAd(context) { ad -> interstitialAd = ad }
    }

    val goBack: () -> Unit = {
        if (AdCounters.onDetailBack()) {
            showInterstitialAd(
                ad = interstitialAd,
                activity = activity,
                onDone = { navController.popBackStack() },
                onReload = { interstitialAd = it }
            )
        } else {
            navController.popBackStack()
        }
    }

    BackHandler { goBack() }

    LaunchedEffect(recipeId) {
        loading = true
        recipe = try {
            viewModel.fetchFullRecipeById(recipeId)
        } catch (e: Exception) {
            try {
                viewModel.fetchFullUserRecipeById(recipeId)
            } catch (e2: Exception) {
                null
            }
        }
        loading = false
    }

    val current = recipe

    when {
        loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        current == null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                EmptyRecipesState(
                    title = "Couldn't load recipe",
                    subtitle = "Something went wrong. Please try again.",
                    actionLabel = "Go back",
                    onAction = { navController.popBackStack() }
                )
            }
        }

        else -> {
            val isFav = favoriteIds.contains(current.id)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Hero(current)
                    StatsStrip(current)
                    if (current.description.isNotBlank()) {
                        Text(
                            text = current.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                        )
                    }
                    IngredientsSection(current.ingredients)
                    StepsSection(current.steps)
                    Spacer(Modifier.height(72.dp))
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    BannerAdView()
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CircleIconButton(Icons.AutoMirrored.Filled.ArrowBack, "Back") {
                        goBack()
                    }
                    CircleIconButton(
                        icon = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFav) MaterialTheme.colorScheme.primary else OnImageText
                    ) {
                        if (isFav) viewModel.removeFromFavorites(current.toFavorite())
                        else viewModel.addToFavorites(current.toFavorite())
                    }
                }
            }
        }
    }
}

@Composable
private fun Hero(recipe: Recipe) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        AsyncImage(
            model = recipe.imageUrl,
            contentDescription = recipe.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.45f to Color.Transparent,
                        1f to Color(0xF2120A16)
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            if (recipe.cuisine.isNotBlank()) {
                Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.primary) {
                    Text(
                        text = recipe.cuisine,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = OnImageText
            )
            if (recipe.author.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Recipe by ${recipe.author}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnImageText.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
private fun StatsStrip(recipe: Recipe) {
    val stats = buildList {
        if (recipe.readyInMinutes > 0) add(Triple(Icons.Default.Schedule, "${recipe.readyInMinutes} min", "time"))
        if (recipe.difficulty.isNotBlank()) add(Triple(Icons.Default.LocalFireDepartment, recipe.difficulty, "level"))
        if (recipe.servings > 0) add(Triple(Icons.Default.People, recipe.servings.toString(), "serves"))
        if (recipe.rating > 0.0) add(Triple(Icons.Default.Star, recipe.rating.toString(), "rating"))
    }
    if (stats.isEmpty()) return

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            stats.forEachIndexed { index, (icon, value, label) ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (label == "rating") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (index < stats.lastIndex) {
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            }
        }
    }
}

@Composable
private fun IngredientsSection(ingredients: List<String>) {
    if (ingredients.isEmpty()) return
    SectionHeader(Icons.Default.ShoppingBasket, "Ingredients", "${ingredients.size} items")
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp)) {
            ingredients.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(item, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
                if (index < ingredients.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    )
                }
            }
        }
    }
}

@Composable
private fun StepsSection(steps: List<String>) {
    if (steps.isEmpty()) return
    SectionHeader(Icons.Default.Restaurant, "Steps", null)
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        steps.forEachIndexed { index, step ->
            Row(modifier = Modifier.padding(bottom = 14.dp)) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(icon: ImageVector, title: String, trailing: String?) {
    Row(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        if (trailing != null) {
            Spacer(Modifier.width(8.dp))
            Text(trailing, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color = OnImageText,
    onClick: () -> Unit
) {
    Surface(onClick = onClick, shape = CircleShape, color = Color(0x99120A16), modifier = Modifier.size(40.dp)) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(22.dp))
        }
    }
}
