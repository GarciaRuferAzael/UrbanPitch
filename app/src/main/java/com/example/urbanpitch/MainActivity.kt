package com.example.urbanpitch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.urbanpitch.ui.UrbanPitchNavGraph
import com.example.urbanpitch.ui.UrbanPitchRoute
import com.example.urbanpitch.ui.theme.UrbanPitchTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

        setContent {
            UrbanPitchTheme {
                val navController = rememberNavController()
                val startDestination = if (isLoggedIn) {
                    UrbanPitchRoute.Home.toString()
                } else {
                    UrbanPitchRoute.Login.toString()
                }

                UrbanPitchNavGraph(
                    navController = navController,
                    startDestination = startDestination
                )
            }
        }
    }
}
