package com.keysersoze.yumyard.presentation.screens.login

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.*
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

    val launcher = rememberLauncherForActivityResult(StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.result
            signInCredential = account
        } catch (e: Exception) {
            Log.e("@@@LoginScreen", "Login failed", e)
        }
    }

    LaunchedEffect(signInCredential) {
        signInCredential?.let { account ->
            try {
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
                val signInIntent = signInClient.signInIntent
                launcher.launch(signInIntent)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Sign in with Google")
        }
    }
}