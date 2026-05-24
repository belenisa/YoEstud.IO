package com.example.yoestudio.Global

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.yoestudio.R

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
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Image(
                painter = painterResource(id = R.drawable.logoasistente),
                contentDescription = "Asistente",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}