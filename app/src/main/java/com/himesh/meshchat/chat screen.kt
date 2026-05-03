package com.himesh.meshchat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    networkingManager: NetworkingManager
) {
    val messages by networkingManager.messages.collectAsState()
    val isConnected by networkingManager.isConnected.collectAsState()
    val peerName by networkingManager.connectedPeerName.collectAsState()

    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(peerName ?: "Mesh Comms", color = Color.White)
                        if (peerName != null) {
                            Text("Connected", color = BlueAccent, fontSize = 12.sp)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    if (isConnected) {
                        IconButton(onClick = {
                            networkingManager.disconnect()
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Default.LinkOff, null, tint = Color.Red)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBackground
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ---- NOT CONNECTED ----
            if (!isConnected) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Not Connected", color = Color.White, fontSize = 20.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Go to Radar to connect",
                            color = TextGray,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = { navController.navigate("home") },
                            colors = ButtonDefaults.buttonColors(BlueAccent)
                        ) {
                            Text("Open Radar", color = Color.White)
                        }
                    }
                }
            }

            // ---- CHAT LIST ----
            else {
                val myName = networkingManager.myUserName

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    items(messages) { msg ->

                        val isMine = msg.startsWith("[$myName]")
                        val clean = msg.removePrefix("[$myName]: ")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isMine) BlueAccent else CardBackground)
                                    .padding(12.dp)
                            ) {
                                Text(clean, color = Color.White)
                            }
                        }
                    }
                }
            }

            // ---- INPUT ----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBackground)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Transmit data...", color = TextGray) },
                    enabled = isConnected,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                )

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (input.isNotBlank()) {
                            networkingManager.sendMessage(input)
                            input = ""
                        }
                    },
                    enabled = isConnected,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isConnected) BlueAccent else TextGray)
                ) {
                    Icon(Icons.Default.Send, null, tint = Color.White)
                }
            }
        }
    }
}