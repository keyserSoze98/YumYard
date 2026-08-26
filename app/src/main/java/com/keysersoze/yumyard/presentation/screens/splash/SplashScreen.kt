package com.keysersoze.yumyard.presentation.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.keysersoze.yumyard.R
import com.keysersoze.yumyard.presentation.navigation.Screen
import com.keysersoze.yumyard.ui.theme.YumCream
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(700),
        label = "splashAlpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        animationSpec = tween(700),
        label = "splashScale"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(1300)
        val destination = if (auth.currentUser != null) Screen.Home.route else Screen.Login.route
        navController.navigate(destination) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF170D1C), Color(0xFF241531))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha)
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash_logo),
                contentDescription = "YumYard",
                modifier = Modifier
                    .size(150.dp)
                    .scale(scale)
                    .clip(RoundedCornerShape(28.dp))
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "YumYard",
                color = YumCream,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Cook. Share. Enjoy.",
                color = YumCream.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFFED7922),
                strokeWidth = 3.dp,
                modifier = Modifier
                    .padding(bottom = 64.dp)
                    .size(30.dp)
            )
        }
    }
}
