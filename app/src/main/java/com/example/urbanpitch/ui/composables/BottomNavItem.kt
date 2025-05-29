package com.example.urbanpitch.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.urbanpitch.ui.UrbanPitchRoute

sealed class BottomNavItem(
    val route: UrbanPitchRoute,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem(UrbanPitchRoute.Home, Icons.Default.Home, "Home")
    object Map : BottomNavItem(UrbanPitchRoute.Map, Icons.Default.LocationOn, "Mappa")
    object Profile : BottomNavItem(UrbanPitchRoute.Profile, Icons.Default.AccountBox, "Profilo")
    object Favorites : BottomNavItem(UrbanPitchRoute.Favorites, Icons.Default.Favorite, "Preferiti")
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Map,
        BottomNavItem.Favorites,
        BottomNavItem.Profile
    )


    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentDestination == item.route.toString(),
                onClick = {
                    navController.navigate(item.route.toString()) {
                        // Torna alla radice evitando doppioni
                        popUpTo(navController.graph.startDestinationRoute ?: "") {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}


