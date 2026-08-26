package com.keysersoze.yumyard.presentation.screens.editor

import android.app.Activity
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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import com.keysersoze.yumyard.presentation.viewmodels.AddRecipeViewModel
import com.keysersoze.yumyard.ui.theme.YumCream
import com.keysersoze.yumyard.ui.theme.YumPurple
import com.keysersoze.yumyard.util.adBanner.loadInterstitialAd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeEditorScreen(
    draftId: String,
    navController: NavController,
    viewModel: AddRecipeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val draftState by viewModel.draft.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()
    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadImageAndSetUrl(it, context) }
    }

    LaunchedEffect(draftId) {
        viewModel.loadDraft(draftId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Details", fontWeight = FontWeight.Bold) },
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
        bottomBar = {
            val draft = draftState
            if (draft != null) {
                UploadActionBar(
                    enabled = !isUploading,
                    onSaveDraft = {
                        viewModel.saveDraft()
                        Toast.makeText(context, "Draft saved!", Toast.LENGTH_SHORT).show()
                    },
                    onUpload = {
                        val doUpload = {
                            viewModel.uploadRecipe(
                                onSuccess = {
                                    navController.popBackStack()
                                    Toast.makeText(context, "Recipe Uploaded!", Toast.LENGTH_LONG).show()
                                },
                                onError = {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                            )
                            loadInterstitialAd(context) { ad -> interstitialAd = ad }
                        }
                        val ad = interstitialAd
                        if (ad != null) {
                            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() = doUpload()
                                override fun onAdFailedToShowFullScreenContent(adError: AdError) = doUpload()
                                override fun onAdShowedFullScreenContent() {
                                    interstitialAd = null
                                }
                            }
                            ad.show(context as Activity)
                        } else {
                            doUpload()
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val draft = draftState) {
                null -> CircularProgressIndicator(
                    Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(Modifier.height(4.dp))

                        ChefGreetingCard(
                            name = user?.displayName ?: "Chef",
                            photoUrl = user?.photoUrl
                        )

                        SectionCard(icon = Icons.Default.Restaurant, title = "Details") {
                            OutlinedTextField(
                                value = draft.title,
                                onValueChange = viewModel::updateTitle,
                                label = { Text("Recipe Title") },
                                singleLine = true,
                                enabled = !isUploading,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(
                                value = draft.cuisine,
                                onValueChange = viewModel::updateCuisine,
                                label = { Text("Cuisine") },
                                singleLine = true,
                                enabled = !isUploading,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = if (draft.readyInMinutes > 0) draft.readyInMinutes.toString() else "",
                                    onValueChange = { viewModel.updateReadyInMinutes(it.toIntOrNull() ?: 0) },
                                    label = { Text("Time (min)") },
                                    singleLine = true,
                                    enabled = !isUploading,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(Modifier.width(12.dp))
                                OutlinedTextField(
                                    value = if (draft.servings > 0) draft.servings.toString() else "",
                                    onValueChange = { viewModel.updateServings(it.toIntOrNull() ?: 0) },
                                    label = { Text("Servings") },
                                    singleLine = true,
                                    enabled = !isUploading,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            DifficultyDropdown(
                                selected = draft.difficulty,
                                enabled = !isUploading,
                                onSelect = viewModel::updateDifficulty
                            )
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(
                                value = draft.category,
                                onValueChange = viewModel::updateCategory,
                                label = { Text("Category (e.g. Dessert)") },
                                singleLine = true,
                                enabled = !isUploading,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        SectionCard(icon = Icons.Default.AddPhotoAlternate, title = "Recipe Image") {
                            ImageDropZone(
                                imageUrl = draft.imageUrl,
                                enabled = !isUploading,
                                onPick = { imagePickerLauncher.launch("image/*") }
                            )
                        }

                        SectionCard(icon = Icons.Default.Description, title = "Description") {
                            OutlinedTextField(
                                value = draft.description,
                                onValueChange = viewModel::updateDescription,
                                label = { Text("A short summary of your dish") },
                                enabled = !isUploading,
                                minLines = 2,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        SectionCard(icon = Icons.Default.FormatListNumbered, title = "Instructions") {
                            draft.steps.forEachIndexed { index, step ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .padding(top = 14.dp)
                                            .size(26.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${index + 1}",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    OutlinedTextField(
                                        value = step,
                                        onValueChange = { viewModel.updateStep(index, it) },
                                        label = { Text("Step ${index + 1}") },
                                        enabled = !isUploading,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { viewModel.removeStep(index) },
                                        enabled = !isUploading && draft.steps.size > 1
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Remove Step",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                            OutlinedButton(
                                onClick = { viewModel.addEmptyStep() },
                                enabled = !isUploading,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Add Step")
                            }
                        }

                        SectionCard(icon = Icons.Default.ShoppingBasket, title = "Ingredients") {
                            draft.ingredients.forEachIndexed { index, pair ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = pair.first,
                                        onValueChange = { viewModel.updateIngredient(index, it) },
                                        label = { Text("Ingredient") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        enabled = !isUploading
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    OutlinedTextField(
                                        value = pair.second,
                                        onValueChange = { viewModel.updateMeasure(index, it) },
                                        label = { Text("Measure") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        enabled = !isUploading
                                    )
                                    IconButton(
                                        onClick = { viewModel.removeIngredient(index) },
                                        enabled = !isUploading
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Remove Ingredient",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                            OutlinedButton(
                                onClick = { viewModel.addEmptyIngredient() },
                                enabled = !isUploading,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Add Ingredient")
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            AnimatedVisibility(visible = isUploading) {
                ProcessingOverlay(message = "Uploading your recipe...")
            }
        }
    }
}

@Composable
private fun ChefGreetingCard(name: String, photoUrl: Uri?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(photoUrl),
                contentDescription = "Profile Pic",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    "Hello $name!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "Let's cook something up.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DifficultyDropdown(
    selected: String,
    enabled: Boolean,
    onSelect: (String) -> Unit
) {
    val options = listOf("Easy", "Medium", "Hard")
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text("Difficulty") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ImageDropZone(
    imageUrl: String,
    enabled: Boolean,
    onPick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(enabled = enabled, onClick = onPick),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNotBlank()) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Recipe Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Surface(
                shape = RoundedCornerShape(topStart = 12.dp),
                color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.55f),
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        tint = YumCream,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Change", color = YumCream, style = MaterialTheme.typography.labelLarge)
                }
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Upload Icon",
                    modifier = Modifier.size(44.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Tap to upload image",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun UploadActionBar(
    enabled: Boolean,
    onSaveDraft: () -> Unit,
    onUpload: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onSaveDraft,
                enabled = enabled,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Icon(Icons.Default.SaveAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Save Draft")
            }
            Button(
                onClick = onUpload,
                enabled = enabled,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Upload")
            }
        }
    }
}

@Composable
private fun ProcessingOverlay(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(28.dp)
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(14.dp))
                Text(message, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
