package com.keysersoze.yumyard.presentation.screens.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.firebase.auth.FirebaseAuth
import com.keysersoze.yumyard.BuildConfig
import com.keysersoze.yumyard.domain.model.Recipe
import com.keysersoze.yumyard.domain.model.toFavorite
import com.keysersoze.yumyard.presentation.components.EmptyRecipesState
import com.keysersoze.yumyard.presentation.components.NoticeBanner
import com.keysersoze.yumyard.presentation.components.RecipeCard
import com.keysersoze.yumyard.presentation.components.ShimmerRecipeList
import com.keysersoze.yumyard.presentation.viewmodels.FavoriteViewModel
import com.keysersoze.yumyard.presentation.viewmodels.RecipeViewModel
import com.keysersoze.yumyard.ui.theme.YumCream
import com.keysersoze.yumyard.ui.theme.YumPurple
import com.keysersoze.yumyard.util.adBanner.AdCounters
import com.keysersoze.yumyard.util.adBanner.BannerAdView
import com.keysersoze.yumyard.util.adBanner.loadInterstitialAd
import com.keysersoze.yumyard.util.adBanner.showInterstitialAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: RecipeViewModel = hiltViewModel(),
    favoriteViewModel: FavoriteViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val recipes by viewModel.recipes.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val query by viewModel.query.collectAsState()
    val notice by viewModel.notice.collectAsState()
    val loadingMore by viewModel.loadingMore.collectAsState()
    val favoriteIds by favoriteViewModel.favoriteIds.collectAsState()

    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            layoutInfo.totalItemsCount > 0 && lastVisible >= layoutInfo.totalItemsCount - 3
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadMore()
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading && recipes.isNotEmpty())

    val context = LocalContext.current
    val activity = context as Activity
    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }

    LaunchedEffect(Unit) {
        loadInterstitialAd(context) { ad -> interstitialAd = ad }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerContent(navController, drawerState, scope) }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { BrandTitle() },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("account") }) {
                            Icon(Icons.Default.Person, contentDescription = "My Account")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = YumPurple,
                        titleContentColor = YumCream,
                        navigationIconContentColor = YumCream,
                        actionIconContentColor = YumCream
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.onQueryChange(it) },
                    label = { Text("Search recipes") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search)
                )

                notice?.let { NoticeBanner(it) }

                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = { viewModel.refresh() },
                    modifier = Modifier.weight(1f)
                ) {
                    when {
                        isLoading && recipes.isEmpty() -> ShimmerRecipeList()

                        recipes.isNotEmpty() -> {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
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
                                        onClick = {
                                            handleRecipeClick(recipe, navController, interstitialAd, AdCounters.onRecipeOpen(), activity) { ad ->
                                                interstitialAd = ad
                                            }
                                        }
                                    )
                                }
                                if (loadingMore) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(28.dp),
                                                strokeWidth = 3.dp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        else -> {
                            val isSearch = query.isNotBlank()
                            EmptyRecipesState(
                                title = if (isSearch) "No results" else "No recipes yet",
                                subtitle = if (isSearch) "Nothing matched \"$query\". Try another search." else "Pull down to refresh, or check your connection.",
                                actionLabel = if (isSearch) "Clear search" else "Refresh",
                                onAction = { if (isSearch) viewModel.onQueryChange("") else viewModel.refresh() }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    BannerAdView()
                }
            }
        }
    }
}

@Composable
private fun BrandTitle() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.RestaurantMenu,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(Modifier.size(10.dp))
        Text("YumYard", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DrawerContent(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    val context = LocalContext.current
    val email = FirebaseAuth.getInstance().currentUser?.email
    var showAbout by remember { mutableStateOf(false) }

    fun go(route: String) {
        navController.navigate(route)
        scope.launch { drawerState.close() }
    }

    ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(YumPurple)
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(44.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.RestaurantMenu, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(Modifier.size(12.dp))
                Column {
                    Text("YumYard", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = YumCream)
                    if (!email.isNullOrBlank()) {
                        Text(email, style = MaterialTheme.typography.bodySmall, color = YumCream.copy(alpha = 0.7f))
                    }
                }
            }
        }

        Spacer(Modifier.size(12.dp))

        NavigationDrawerItem(
            label = { Text("My Account") },
            selected = false,
            onClick = { go("account") },
            icon = { Icon(Icons.Default.Person, contentDescription = "My Account") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        NavigationDrawerItem(
            label = { Text("Favorites") },
            selected = false,
            onClick = { go("favorites") },
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        NavigationDrawerItem(
            label = { Text("My Recipes") },
            selected = false,
            onClick = { go("my_recipes") },
            icon = { Icon(Icons.Default.MenuBook, contentDescription = "My Recipes") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        NavigationDrawerItem(
            label = { Text("Add Recipes") },
            selected = false,
            onClick = { go("draft_recipes") },
            icon = { Icon(Icons.Default.PostAdd, contentDescription = "Add Recipes") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        NavigationDrawerItem(
            label = { Text("Rate us on Play Store") },
            selected = false,
            onClick = {
                openPlayStore(context)
                scope.launch { drawerState.close() }
            },
            icon = { Icon(Icons.Default.Star, contentDescription = "Rate us") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        NavigationDrawerItem(
            label = { Text("About") },
            selected = false,
            onClick = { showAbout = true },
            icon = { Icon(Icons.Default.Info, contentDescription = "About") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }

    if (showAbout) {
        AboutDialog(onDismiss = { showAbout = false })
    }
}

@Composable
private fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
        title = { Text("About YumYard") },
        text = {
            Column {
                Text(
                    "YumYard helps you discover, cook, and save delicious recipes from around the world.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.size(12.dp))
                Text("Version ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Developed by Keyser Soze & Co.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

private fun openPlayStore(context: Context) {
    val packageName = context.packageName
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
    } catch (e: Exception) {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
        )
    }
}

fun handleRecipeClick(
    recipe: Recipe,
    navController: NavHostController,
    interstitialAd: InterstitialAd?,
    showAd: Boolean,
    activity: Activity,
    updateAd: (InterstitialAd?) -> Unit
) {
    val route = "details/${recipe.id}"
    if (showAd) {
        showInterstitialAd(
            ad = interstitialAd,
            activity = activity,
            onDone = { navController.navigate(route) },
            onReload = updateAd
        )
    } else {
        navController.navigate(route)
    }
}
