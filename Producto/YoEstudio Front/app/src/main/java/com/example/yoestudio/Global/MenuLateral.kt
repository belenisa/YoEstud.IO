package com.example.yoestudio.Global

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MenuLateral(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    ModalDrawerSheet {
        Text("Menú", modifier = Modifier.padding(16.dp))
        HorizontalDivider()

        NavigationDrawerItem(
            label = { Text("Inicio") },
            selected = false,
            onClick = {
                navController.navigate("inicio")
                scope.launch { drawerState.close() }
            }
        )

        NavigationDrawerItem(
            label = { Text("Configuración") },
            selected = false,
            onClick = {
                navController.navigate("configuracion")
                scope.launch { drawerState.close() }
            }
        )
    }
}