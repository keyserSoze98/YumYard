package com.keysersoze.yumyard.presentation.screens.login

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.keysersoze.yumyard.R
import com.keysersoze.yumyard.presentation.navigation.Screen
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var signInCredential by remember { mutableStateOf<GoogleSignInAccount?>(null) }
    var isSigningIn by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.result
            signInCredential = account
        } catch (e: Exception) {
            Log.e("@@@LoginScreen", "Login failed", e)
            Toast.makeText(context, "Login cancelled or failed.", Toast.LENGTH_SHORT).show()
            isSigningIn = false
        }
    }

    LaunchedEffect(signInCredential) {
        signInCredential?.let { account ->
            try {
                isSigningIn = true
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).await()
                Log.d("@@@LoginScreen", "Login successful: ${auth.currentUser?.email}")
                Toast.makeText(context, "Login Successful!", Toast.LENGTH_LONG).show()
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            } catch (e: Exception) {
                Log.e("@@@LoginScreen", "Firebase sign-in failed", e)
                Toast.makeText(context, "Login Failed!", Toast.LENGTH_LONG).show()
                isSigningIn = false
            }
        }
    }

    val signInClient = remember {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("547749321332-uv648a4kp6cddram9op7t5cls49l0l22.apps.googleusercontent.com")
                .requestEmail()
                .build()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to YumYard!", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                isSigningIn = true
                signInClient.signOut().addOnCompleteListener {
                    val signInIntent = signInClient.signInIntent
                    launcher.launch(signInIntent)
                }
            },
            enabled = !isSigningIn
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(if (isSigningIn) "Please wait..." else "Sign in with Google")
        }
    }

    if (isSigningIn) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { },
            confirmButton = {},
            title = { Text("Signing you in...") },
            text = { Text("Please wait while we connect to Google.") }
        )
    }
}