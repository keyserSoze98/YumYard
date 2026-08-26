package com.keysersoze.yumyard.presentation.screens.login

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.keysersoze.yumyard.R
import com.keysersoze.yumyard.presentation.navigation.Screen
import com.keysersoze.yumyard.ui.theme.YumCream
import kotlinx.coroutines.tasks.await

private val BrandTop = Color(0xFF170D1C)
private val BrandBottom = Color(0xFF241531)

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BrandTop, BrandBottom)))
            .systemBarsPadding()
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash_logo),
                contentDescription = "YumYard",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(26.dp))
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Welcome to YumYard",
                color = YumCream,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Cook. Share. Enjoy.",
                color = YumCream.copy(alpha = 0.7f),
                fontSize = 15.sp
            )
        }

        GoogleSignInButton(
            isSigningIn = isSigningIn,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            onClick = {
                isSigningIn = true
                signInClient.signOut().addOnCompleteListener {
                    launcher.launch(signInClient.signInIntent)
                }
            }
        )
    }
}

@Composable
private fun GoogleSignInButton(
    isSigningIn: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isSigningIn,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(27.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF1F1F1F),
            disabledContainerColor = Color.White.copy(alpha = 0.6f),
            disabledContentColor = Color(0xFF1F1F1F).copy(alpha = 0.6f)
        )
    ) {
        if (isSigningIn) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = Color(0xFF1F1F1F),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text("Signing you in…", fontWeight = FontWeight.SemiBold)
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text("Sign in with Google", fontWeight = FontWeight.SemiBold)
        }
    }
}
