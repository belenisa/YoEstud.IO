package com.example.yoestudio.Pantallas



import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Inicio(drawerState: DrawerState, scope: CoroutineScope) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("YoEstud.io") },
                navigationIcon = {
                    IconButton(onClick = {
                        // Ahora sí sabe a qué drawer referirse
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Text("Contenido principal aquí")
        }
    }
}

@Composable
fun MenuLateral() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var pantallaActual by remember { mutableStateOf("inicio") }

    ModalNavigationDrawer(
        drawerState = drawerState, // Pasamos el estado al controlador
        drawerContent = {
            ModalDrawerSheet {
                Text("Menú", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Inicio") },
                    selected = pantallaActual == "inicio",
                    onClick = {
                        pantallaActual = "inicio"
                        scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Configuración") },
                    selected = pantallaActual == "configuracion",
                    onClick = {
                        pantallaActual = "configuracion" // Cambiamos el estado
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        // 2. Aquí decidimos qué función llamar según el estado
        when (pantallaActual) {
            "inicio" -> Inicio(drawerState = drawerState, scope = scope)
            "configuracion" -> PantallaConfiguracion(drawerState = drawerState, scope = scope)
        }
    }
}