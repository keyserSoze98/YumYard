package com.keysersoze.yumyard.presentation.screens.addOrEditRecipe

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.keysersoze.yumyard.presentation.viewmodels.AddRecipeViewModel

@Composable
fun AddEditRecipeScreen(
    draftId: String,
    navController: NavController,
    viewModel: AddRecipeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val draftState by viewModel.draft.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadImageAndSetUrl(it, context) }
    }

    LaunchedEffect(draftId) {
        viewModel.loadDraft(draftId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            draftState == null -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }

            else -> {
                val draft = draftState!!
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // 🧑‍🍳 Profile
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(user?.photoUrl),
                            contentDescription = "Profile Pic",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Hello ${user?.displayName ?: "Chef"}, let's cook something up!",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 🖋 Title
                    OutlinedTextField(
                        value = draft.title,
                        onValueChange = { viewModel.updateTitle(it) },
                        label = { Text("Recipe Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 🌍 Cuisine
                    OutlinedTextField(
                        value = draft.cuisine,
                        onValueChange = { viewModel.updateCuisine(it) },
                        label = { Text("Cuisine") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 📸 Image Upload Section
                    Text(
                        "Recipe Image",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(2.dp, Color.LightGray, RoundedCornerShape(16.dp))
                            .clickable(enabled = !isUploading) {
                                imagePickerLauncher.launch("image/*")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (draft.imageUrl.isNotBlank()) {
                            Image(
                                painter = rememberAsyncImagePainter(draft.imageUrl),
                                contentDescription = "Recipe Image",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Upload Icon",
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.Gray
                                )
                                Text("Tap to upload image", color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 📜 Instructions
                    OutlinedTextField(
                        value = draft.steps,
                        onValueChange = { viewModel.updateSteps(it) },
                        label = { Text("Instructions") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 5
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 🧂 Ingredients
                    Text("Ingredients", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.addEmptyIngredient() },
                        enabled = !isUploading
                    ) {
                        Text("+ Add Ingredient")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    draft.ingredients.forEachIndexed { index, pair ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = pair.first,
                                onValueChange = { viewModel.updateIngredient(index, it) },
                                label = { Text("Ingredient") },
                                modifier = Modifier.weight(1f),
                                enabled = !isUploading
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = pair.second,
                                onValueChange = { viewModel.updateMeasure(index, it) },
                                label = { Text("Measure") },
                                modifier = Modifier.weight(1f),
                                enabled = !isUploading
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { viewModel.removeIngredient(index) },
                                enabled = !isUploading
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove Ingredient",
                                    tint = Color.Red
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 🧰 Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                viewModel.saveDraft()
                                Toast.makeText(context, "Draft saved!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            ),
                            enabled = !isUploading
                        ) {
                            Text("Save Draft")
                        }

                        Button(
                            onClick = {
                                viewModel.uploadRecipe(
                                    onSuccess = {
                                        navController.popBackStack()
                                        Toast.makeText(context, "Recipe Uploaded!", Toast.LENGTH_LONG).show()
                                    },
                                    onError = {
                                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF7043),
                                contentColor = Color.White
                            ),
                            enabled = !isUploading
                        ) {
                            Text("Upload Recipe")
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

        // ✨ Overlay always on top
        AnimatedVisibility(visible = isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(Modifier.height(12.dp))
                    Text("Uploading your recipe...", color = Color.White)
                }
            }
        }
    }
}