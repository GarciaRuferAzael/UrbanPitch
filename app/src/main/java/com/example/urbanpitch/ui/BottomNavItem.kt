package com.example.urbanpitch.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
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

sealed class BottomNavItem(
    val route: UrbanPitchRoute,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem(UrbanPitchRoute.HomeScreen, Icons.Default.Home, "Home")
    object Map : BottomNavItem(UrbanPitchRoute.MapScreen, Icons.Default.LocationOn, "Mappa")
    object Profile : BottomNavItem(UrbanPitchRoute.ProfileScreen, Icons.Default.AccountBox, "Profilo")
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Map,
        BottomNavItem.Profile
    )

    NavigationBar {
        val currentDestination = navController
            .currentBackStackEntryAsState().value?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentDestination == item.route.toString(),
                onClick = {
                    navController.navigate(item.route.toString()) {
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

