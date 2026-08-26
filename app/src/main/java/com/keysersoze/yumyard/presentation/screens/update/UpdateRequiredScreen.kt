package com.keysersoze.yumyard.presentation.screens.update

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keysersoze.yumyard.R
import com.keysersoze.yumyard.ui.theme.YumCream
import com.keysersoze.yumyard.ui.theme.YumOrange

private val BrandTop = Color(0xFF170D1C)
private val BrandBottom = Color(0xFF241531)

@Composable
fun UpdateRequiredScreen(onUpdate: () -> Unit) {
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
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash_logo),
                contentDescription = "YumYard",
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(24.dp))
            )
            Spacer(Modifier.height(28.dp))
            Icon(
                imageVector = Icons.Default.SystemUpdate,
                contentDescription = null,
                tint = YumOrange,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Update Required",
                color = YumCream,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "A new version of YumYard is available. Please update to keep cooking with the latest recipes and fixes.",
                color = YumCream.copy(alpha = 0.75f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Button(
            onClick = onUpdate,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(27.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = YumOrange,
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Default.SystemUpdate, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text("Update Now", fontWeight = FontWeight.SemiBold)
        }
    }
}
