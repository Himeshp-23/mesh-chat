package com.himesh.meshchat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.WifiOff
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, networkingManager: NetworkingManager) {
    val messages by networkingManager.messages.collectAsState()
    val isConnected by networkingManager.isConnected.collectAsState()
    val connectedPeerName by networkingManager.connectedPeerName.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to the latest message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mesh Comms", color = Color.White, fontWeight = FontWeight.Bold)
                        if (isConnected && connectedPeerName != null) {
                            Text(connectedPeerName!!, color = BlueAccent, fontSize = 12.sp)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (isConnected) {
                        IconButton(onClick = {
                            networkingManager.disconnect()
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Default.LinkOff, contentDescription = "Disconnect", tint = Color.Red)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardBackground)
            )
        },
        bottomBar = { BottomNavBar(navController, "chat") },
        containerColor = AppBackground
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (!isConnected) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                        Icon(Icons.Default.WifiOff, contentDescription = null, tint = TextGray, modifier = Modifier.size(72.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Not Connected", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Go to Radar to find and connect to a nearby device.", color = TextGray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { navController.navigate("home") },
                            colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Go to Radar")
                        }
                    }
                }
            } else {
                val currentMyName = networkingManager.myUserName

                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(messages) { msg ->
                        val isMine = msg.startsWith("[$currentMyName]")
                        val cleanMsg = if (isMine) msg.replace("[$currentMyName]: ", "") else msg

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isMine) BlueAccent else CardBackground)
                                    .padding(12.dp)
                            ) {
                                Text(text = cleanMsg, color = Color.White)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBackground)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Transmit data...", color = TextGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = BlueAccent,
                            unfocusedBorderColor = TextGray
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                networkingManager.sendMessage(inputText)
                                inputText = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = BlueAccent)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}
