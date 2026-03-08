package com.himesh.meshchat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, networkingManager: NetworkingManager) {
    val messages by networkingManager.messages.collectAsState()
    var inputText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF121212))) {
        TopAppBar(
            title = { Text("Mesh Comms", color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1E1E))
        )


        val currentMyName = networkingManager.myUserName // Capture this ONCE for the whole list

        LazyColumn(modifier = Modifier.weight(1f).padding(16.dp)) {
            items(messages) { msg ->
                // Check if the message starts with our current name
                val isMine = msg.startsWith("[$currentMyName]")

                // Clean the message by removing the tag
                val cleanMsg = if (isMine) msg.replace("[$currentMyName]: ", "") else msg

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isMine) Color(0xFF004D40) else Color(0xFF2C2C2C))
                            .padding(12.dp)
                    ) {
                        Text(text = cleanMsg, color = Color.White)
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().background(Color(0xFF1E1E1E)).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Transmit data...", color = Color.Gray) },
                // Use the updated colors API
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF00E676), // NeonAccent
                    unfocusedBorderColor = Color.Gray
                ),
                shape = RoundedCornerShape(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (inputText.isNotBlank()) {
                    networkingManager.sendMessage(inputText)
                    inputText = ""
                }
            }) { Text("Send") }
        }
    }
}