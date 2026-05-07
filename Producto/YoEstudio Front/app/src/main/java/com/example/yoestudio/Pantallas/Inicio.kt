package com.example.yoestudio.Pantallas



import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.yoestudio.Global.BotonAsistente
import com.example.yoestudio.ui.theme.AppNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Inicio(drawerState: DrawerState, scope: CoroutineScope) {
    // 1. Creamos el controlador que se compartirá en TODA la pantalla
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("YoEstud.io") },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        // 2. IMPORTANTE: Pasamos el MISMO navController al botón
        floatingActionButton = {
            BotonAsistente(navController = navController)
        }
    ) { paddingValues ->
        // 3. El contenedor debe envolver al NavHost
        Box(modifier = Modifier.padding(paddingValues)) {
            // 4. Llamamos a tu lógica de navegación pasándole el controlador
            // Esto conecta el "cable" (navController) con el "mapa" (NavHost)
            AppNavigation(
                navController = navController,
                drawerState = drawerState,
                scope = scope
            )
        }
    }
}