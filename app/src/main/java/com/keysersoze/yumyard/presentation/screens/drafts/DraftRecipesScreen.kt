package com.keysersoze.yumyard.presentation.screens.drafts

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.google.firebase.auth.FirebaseAuth
import com.keysersoze.yumyard.data.local.entities.UserRecipeDraftEntity
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

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        if (drafts.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberAsyncImagePainter(user?.photoUrl),
                    contentDescription = "User Photo",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Hello ${user?.displayName ?: "Chef"} 👨‍🍳")

                Spacer(modifier = Modifier.height(4.dp))

                Text("Let's add your recipe!", fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val newId = UUID.randomUUID().toString()
                        navController.navigate("add_edit_recipe/$newId")
                    }
                ) {
                    Text("Add Recipe")
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ){
                items(drafts) { draft ->
                    DraftRecipeCard(
                        draft = draft,
                        onDelete = { viewModel.deleteDraft(draft) },
                        onClick = {
                            navController.navigate("add_edit_recipe/${draft.id}")
                        }
                    )
                }
            }
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