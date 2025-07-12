package com.keysersoze.yumyard.presentation.screens.drafts

import BannerAdView
import android.app.Activity
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.firebase.auth.FirebaseAuth
import com.keysersoze.yumyard.data.local.entities.UserRecipeDraftEntity
import com.keysersoze.yumyard.presentation.screens.adBanner.loadInterstitialAd
import com.keysersoze.yumyard.presentation.viewmodels.DraftViewModel
import java.text.DateFormat
import java.util.Date
import java.util.UUID

@Composable
fun DraftRecipesScreen(
    navController: NavController,
    viewModel: DraftViewModel = hiltViewModel()
) {
    val drafts by viewModel.drafts.collectAsState()
    val user = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current
    val activity = context as Activity
    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }

    LaunchedEffect(Unit) {
        loadInterstitialAd(context) { ad -> interstitialAd = ad }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(user?.photoUrl),
                        contentDescription = "User Photo",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Hey ${user?.displayName ?: "Chef"} 👨‍🍳",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = {
                        val newId = UUID.randomUUID().toString()
                        showAdThenNavigate(
                            interstitialAd, activity,
                            onNavigate = {
                                navController.navigate("add_edit_recipe/$newId")
                            },
                            updateAd = { ad -> interstitialAd = ad }
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "+ Add Recipe")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (drafts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 48.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No drafts yet!", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap + Add Recipe to get started 🚀")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 70.dp)
                ) {
                    items(drafts) { draft ->
                        DraftRecipeCard(
                            draft = draft,
                            onDelete = { viewModel.deleteDraft(draft) },
                            onClick = {
                                showAdThenNavigate(
                                    interstitialAd, activity,
                                    onNavigate = {
                                        navController.navigate("add_edit_recipe/${draft.id}")
                                    },
                                    updateAd = { ad -> interstitialAd = ad }
                                )
                            }
                        )
                    }
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

@Composable
fun DraftRecipeCard(
    draft: UserRecipeDraftEntity,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = draft.title.ifBlank { "Untitled Recipe" },
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Last edited: ${
                        DateFormat.getDateTimeInstance().format(Date(draft.lastUpdated))
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            IconButton(onClick = { onDelete() }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Draft", tint = Color.Red)
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