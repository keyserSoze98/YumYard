package com.keysersoze.yumyard.presentation.screens.drafts

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.keysersoze.yumyard.data.local.entities.UserRecipeDraftEntity
import com.keysersoze.yumyard.presentation.components.EmptyRecipesState
import com.keysersoze.yumyard.presentation.navigation.Screen
import com.keysersoze.yumyard.presentation.viewmodels.DraftViewModel
import com.keysersoze.yumyard.util.adBanner.BannerAdView
import com.keysersoze.yumyard.util.adBanner.loadInterstitialAd
import com.keysersoze.yumyard.ui.theme.YumCream
import com.keysersoze.yumyard.ui.theme.YumPurple
import java.text.DateFormat
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraftsScreen(
    navController: NavController,
    viewModel: DraftViewModel = hiltViewModel()
) {
    val drafts by viewModel.drafts.collectAsState()
    val context = LocalContext.current
    val activity = context as Activity
    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }

    LaunchedEffect(Unit) {
        loadInterstitialAd(context) { ad -> interstitialAd = ad }
    }

    val openEditor: (String) -> Unit = { id ->
        showAdThenNavigate(
            interstitialAd, activity,
            onNavigate = { navController.navigate("${Screen.AddEditRecipe.route}/$id") },
            updateAd = { ad -> interstitialAd = ad }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Recipe", fontWeight = FontWeight.Bold) },
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { openEditor(UUID.randomUUID().toString()) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Recipe") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (drafts.isEmpty()) {
                EmptyRecipesState(
                    title = "No drafts yet",
                    subtitle = "Start a new recipe and it'll be saved here as a draft until you publish it.",
                    icon = Icons.Default.EditNote,
                    actionLabel = "Create a recipe",
                    onAction = { openEditor(UUID.randomUUID().toString()) }
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Drafts",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp)
                    ) {
                        items(drafts, key = { it.id }) { draft ->
                            DraftCard(
                                draft = draft,
                                onDelete = { viewModel.deleteDraft(draft) },
                                onClick = { openEditor(draft.id) }
                            )
                        }
                    }
                    BannerAdView()
                }
            }
        }
    }
}

@Composable
private fun DraftCard(
    draft: UserRecipeDraftEntity,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = draft.title.ifBlank { "Untitled Recipe" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = DateFormat.getDateInstance(DateFormat.MEDIUM)
                            .format(Date(draft.lastUpdated)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.DeleteOutline,
                    contentDescription = "Delete Draft",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

fun showAdThenNavigate(
    interstitialAd: InterstitialAd?,
    activity: Activity,
    onNavigate: () -> Unit,
    updateAd: (InterstitialAd?) -> Unit
) {
    if (interstitialAd != null) {
        interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                onNavigate()
                loadInterstitialAd(activity) { updateAd(it) }
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                onNavigate()
                loadInterstitialAd(activity) { updateAd(it) }
            }

            override fun onAdShowedFullScreenContent() {
                updateAd(null)
            }
        }
        interstitialAd.show(activity)
    } else {
        onNavigate()
    }
}
