/*package com.himesh.meshchat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DevicesScreen(navController: NavController, networkingManager: NetworkingManager) {
    val connectedDevices by networkingManager.connectedDevices.collectAsState()

    Scaffold(
        bottomBar = { BottomNavBar(navController, "devices") },
        containerColor = AppBackground
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {

            Text("Active Mesh", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Text("These devices are secured and ready for chat.", color = TextGray)

            Spacer(modifier = Modifier.height(30.dp))

            if (connectedDevices.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No devices connected. Go to Scanner.", color = TextGray)
                }
            } else {
                LazyColumn {
                    items(connectedDevices.toList()) { (id, name) ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), shape = RoundedCornerShape(16.dp)) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(AppBackground), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Color(0xFF4ADE80))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text("Connection Secured", color = Color(0xFF4ADE80), fontSize = 12.sp)
                                }
                                Button(onClick = { navController.navigate("chat") }, colors = ButtonDefaults.buttonColors(containerColor = BlueAccent), shape = RoundedCornerShape(8.dp)) {
                                    Text("Chat", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}*/