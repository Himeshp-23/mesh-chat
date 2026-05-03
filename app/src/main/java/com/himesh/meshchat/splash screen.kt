package com.himesh.meshchat

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
@Composable
fun SplashScreen(navController: NavController) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = ""
    )
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("home") { popUpTo("splash") { inclusive = true } }
    }
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF131A22)),
        contentAlignment = Alignment.Center )
    { Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(100.dp).scale(scale).clip(CircleShape).background(Color(0xFF2E8CFF).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center )
            {
                Text("⬡", fontSize = 60.sp, color = Color(0xFF2E8CFF)) }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Mesh Chat", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Peer-to-Peer Mesh Network", fontSize = 14.sp, color = Color(0xFF2E8CFF))
        } } }