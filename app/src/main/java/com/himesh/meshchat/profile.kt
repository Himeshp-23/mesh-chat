package com.himesh.meshchat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, networkingManager: NetworkingManager) {
    var nameInput by remember { mutableStateOf(networkingManager.myUserName) }
    var showSuccess by remember { mutableStateOf(false) }
    val isConnected by networkingManager.isConnected.collectAsState()
    val connectedPeerName by networkingManager.connectedPeerName.collectAsState()

    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            delay(2000)
            showSuccess = false
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController, "profile") },
        containerColor = AppBackground
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            Text("Node Profile", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(40.dp))

            Icon(Icons.Default.Person, contentDescription = null, tint = BlueAccent, modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Identity Alias", color = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = BlueAccent,
                    unfocusedBorderColor = TextGray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    networkingManager.updateProfileName(nameInput)
                    showSuccess = true
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)
            ) {
                Text("Save Identity", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (showSuccess) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4ADE80))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Identity Saved & Mesh Updated!", color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Mesh Status", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = BlueAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Node Name", color = TextGray, fontSize = 12.sp)
                            Text(networkingManager.myUserName, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    HorizontalDivider(color = AppBackground, thickness = 1.dp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (isConnected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (isConnected) Color(0xFF4ADE80) else TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Connection Status", color = TextGray, fontSize = 12.sp)
                            Text(
                                if (isConnected) "Connected" else "Disconnected",
                                color = if (isConnected) Color(0xFF4ADE80) else TextGray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    connectedPeerName?.let { peerName ->
                        HorizontalDivider(color = AppBackground, thickness = 1.dp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BlueAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Connected Peer", color = TextGray, fontSize = 12.sp)
                                Text(peerName, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}