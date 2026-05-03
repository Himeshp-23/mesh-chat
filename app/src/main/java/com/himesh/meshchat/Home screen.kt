package com.himesh.meshchat

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

val AppBackground = Color(0xFF131A22)
val CardBackground = Color(0xFF1E2633)
val BlueAccent = Color(0xFF2E8CFF)
val TextGray = Color(0xFF94A3B8)
@SuppressLint("InlinedApi")
@Composable
fun HomeScreen(navController: NavController, networkingManager: NetworkingManager) {
    val availableDevices by networkingManager.availableDevices.collectAsState()
    val incomingRequest by networkingManager.incomingRequest.collectAsState()
    val isConnected by networkingManager.isConnected.collectAsState()
    val connectedPeerName by networkingManager.connectedPeerName.collectAsState()
    var isScanning by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val permissionsToRequest = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.NEARBY_WIFI_DEVICES); add(Manifest.permission.BLUETOOTH_SCAN); add(Manifest.permission.BLUETOOTH_ADVERTISE); add(Manifest.permission.BLUETOOTH_CONNECT)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.BLUETOOTH_SCAN); add(Manifest.permission.BLUETOOTH_ADVERTISE); add(Manifest.permission.BLUETOOTH_CONNECT)
        } else
            add(Manifest.permission.BLUETOOTH); add(Manifest.permission.BLUETOOTH_ADMIN)

    }
    val btLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    LaunchedEffect(Unit) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter?.isEnabled == false)
            btLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))

        val allGranted = permissionsToRequest.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (allGranted && !isScanning) {
            networkingManager.startMesh()
            isScanning = true
        }
    }

    LaunchedEffect(isConnected) {
        if (isConnected)
            navController.navigate("chat")
    }

    if (incomingRequest != null) {
        val (endpointId, reqName) = incomingRequest!!
        AlertDialog(
            onDismissRequest = { networkingManager.rejectConnection(endpointId) },
            containerColor = CardBackground,
            title = { Text("Secure Connection Request", color = Color.White) },
            text = { Text("$reqName wants to connect to your device.", color = TextGray) },
            confirmButton = {
                Button(onClick = { networkingManager.acceptConnection(endpointId) }, colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)) {
                    Text("Accept", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { networkingManager.rejectConnection(endpointId) }) { Text("Reject", color = Color.Red) } }
        ) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
        if (perms.values.all { it }) {
            networkingManager.startMesh()
            isScanning = true } }
    Scaffold(
        bottomBar = { BottomNavBar(navController, "home") },
        containerColor = AppBackground
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bluetooth, contentDescription = null, tint = BlueAccent, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mesh Radar", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = { launcher.launch(permissionsToRequest.toTypedArray()) }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = BlueAccent)
                }}
            Spacer(modifier = Modifier.height(30.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(BlueAccent.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(if (isScanning) BlueAccent else TextGray), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.WifiTethering, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    } }}
            Spacer(modifier = Modifier.height(20.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(if (isScanning) "Radar Active..." else "Sensors Idle", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.height(24.dp))
            if (isConnected) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(androidx.compose.foundation.shape.CircleShape).background(Color(0xFF4ADE80)))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Connected to ${connectedPeerName ?: "Peer"}", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { networkingManager.disconnect() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Disconnect", color = Color.White)
                            }
                            Button(
                                onClick = { navController.navigate("chat") },
                                colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Go to Chat", color = Color.White)
                            } } }}
                Spacer(modifier = Modifier.height(16.dp)) }

            if (!isScanning) {
                Button(
                    onClick = { launcher.launch(permissionsToRequest.toTypedArray()) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Initialize Radar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Nearby Devices", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("${availableDevices.size} Found", color = BlueAccent, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(availableDevices.toList()) { (id, name) ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = CardBackground), shape = RoundedCornerShape(16.dp)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(AppBackground), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Color(0xFF4ADE80))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text("Ready to Connect", color = TextGray, fontSize = 12.sp)
                            }
                            Button(
                                onClick = { networkingManager.requestConnection(id) },
                                colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Connect", color = Color.White)
                            } }
                    }}
            }}} }
@Composable
fun BottomNavBar(navController: NavController, currentRoute: String) {
    NavigationBar(containerColor = AppBackground, contentColor = TextGray) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Bluetooth, null) }, label = { Text("Radar") },
            selected = currentRoute == "home",
            onClick = { if (currentRoute != "home") navController.navigate("home") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = BlueAccent, selectedTextColor = BlueAccent, indicatorColor = Color.Transparent))
        NavigationBarItem(
            icon = { Icon(Icons.Default.Chat, null)},label={Text("Chat") },
            selected = currentRoute == "chat",
            onClick = { if (currentRoute!="chat")navController.navigate("chat") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = BlueAccent, selectedTextColor = BlueAccent, indicatorColor = Color.Transparent)
        )
    }
}