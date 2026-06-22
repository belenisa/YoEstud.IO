package com.example.yoestudio.Global

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BotonAsistente(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route

    if (currentRoute != "pantalla_ia") {
        ExtendedFloatingActionButton(
            onClick = {
                navController.navigate("pantalla_ia") {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.SmartToy, contentDescription = null) },
            text = { Text(text = "Asistente IA") }
        )
    }
}