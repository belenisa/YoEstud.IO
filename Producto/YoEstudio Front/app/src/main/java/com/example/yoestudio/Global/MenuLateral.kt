package com.example.yoestudio.Global

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.yoestudio.Pantallas.Inicio
import com.example.yoestudio.Pantallas.PantallaConfiguracion
import kotlinx.coroutines.launch

@Composable
fun MenuLateral(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var pantallaActual by remember { mutableStateOf("inicio") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menú", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Inicio") },
                    selected = pantallaActual == "inicio",
                    onClick = {
                        pantallaActual = "inicio"
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Configuración") },
                    selected = pantallaActual == "configuracion",
                    onClick = {
                        pantallaActual = "configuracion"
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        androidx.compose.material3.Scaffold(
        ) { paddingValues ->
            // El Box ayuda a que el contenido respete los espacios del Scaffold
            androidx.compose.foundation.layout.Box(modifier = Modifier.padding(paddingValues)) {
                when (pantallaActual) {
                    "inicio" -> Inicio(drawerState = drawerState, scope = scope)
                    "configuracion" -> PantallaConfiguracion(drawerState = drawerState, scope = scope)
                }
            }
        }
    }
}