package com.himesh.meshchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    private lateinit var networkingManager: NetworkingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkingManager = NetworkingManager(this)

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "splash") {
                composable("splash") { SplashScreen(navController) }
                composable("home") { HomeScreen(navController, networkingManager) }
                composable("chat") { ChatScreen(navController, networkingManager) }
                composable("profile") { ProfileScreen(navController, networkingManager) }
              //  composable("devices") { DevicesScreen(navController, networkingManager) }
            }
        }
    }
}