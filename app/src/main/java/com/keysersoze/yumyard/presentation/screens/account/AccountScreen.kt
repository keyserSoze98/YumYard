package com.keysersoze.yumyard.presentation.screens.account

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.keysersoze.yumyard.presentation.viewmodels.AccountViewModel
import com.keysersoze.yumyard.ui.theme.YumCream
import com.keysersoze.yumyard.ui.theme.YumPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavController, viewModel: AccountViewModel = hiltViewModel()) {
    val user = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current

    var displayName by remember { mutableStateOf(user?.displayName ?: "") }
    var isEditing by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            isProcessing = true
            viewModel.updatePhotoUrl(it) { success ->
                isProcessing = false
                Toast.makeText(
                    context,
                    if (success) "Profile picture updated!" else "Failed to update picture!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Account", fontWeight = FontWeight.Bold) },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileHeaderCard(
                    displayName = displayName,
                    email = user?.email ?: "",
                    photoModel = selectedImageUri ?: user?.photoUrl,
                    isEditing = isEditing,
                    isProcessing = isProcessing,
                    onNameChange = { displayName = it },
                    onPickImage = { imageLauncher.launch("image/*") },
                    onStartEdit = { isEditing = true },
                    onSaveName = {
                        isProcessing = true
                        viewModel.updateDisplayName(displayName) { success ->
                            isProcessing = false
                            if (success) {
                                Toast.makeText(context, "Profile Updated!", Toast.LENGTH_LONG).show()
                                isEditing = false
                            } else {
                                Toast.makeText(context, "Failed to update!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        AccountActionRow(
                            icon = Icons.AutoMirrored.Filled.ExitToApp,
                            label = "Sign Out",
                            enabled = !isProcessing,
                            onClick = {
                                isProcessing = true
                                viewModel.signOut {
                                    isProcessing = false
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        )
                        AccountActionRow(
                            icon = Icons.Default.Delete,
                            label = "Delete Account",
                            enabled = !isProcessing,
                            destructive = true,
                            onClick = { showDeleteDialog = true }
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isProcessing) {
                ProcessingOverlay()
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    isProcessing = true
                    viewModel.deleteAccount(
                        onSuccess = {
                            isProcessing = false
                            Toast.makeText(context, "Account Deleted!", Toast.LENGTH_LONG).show()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onError = {
                            isProcessing = false
                            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        }
                    )
                }) {
                    Text("Yes, Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to delete your account? This action cannot be undone.") }
        )
    }
}

@Composable
private fun ProfileHeaderCard(
    displayName: String,
    email: String,
    photoModel: Any?,
    isEditing: Boolean,
    isProcessing: Boolean,
    onNameChange: (String) -> Unit,
    onPickImage: () -> Unit,
    onStartEdit: () -> Unit,
    onSaveName: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(104.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        photoModel ?: "https://i.pravatar.cc/150?img=3"
                    ),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable(enabled = !isProcessing, onClick = onPickImage)
                )
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Photo",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(7.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = onNameChange,
                    label = { Text("Display Name") },
                    singleLine = true,
                    enabled = !isProcessing,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onSaveName,
                    enabled = !isProcessing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Save")
                }
            } else {
                Text(
                    displayName.ifBlank { "Chef" },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onStartEdit, enabled = !isProcessing) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Edit Display Name")
                }
            }
        }
    }
}

@Composable
private fun AccountActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    enabled: Boolean,
    destructive: Boolean = false,
    onClick: () -> Unit
) {
    val tint = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(16.dp))
        Text(
            label,
            style = MaterialTheme.typography.titleMedium,
            color = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ProcessingOverlay() {
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
                Text("Processing...", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
