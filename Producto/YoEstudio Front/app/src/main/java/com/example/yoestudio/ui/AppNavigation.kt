package com.example.yoestudio.ui.theme

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.yoestudio.ui.Pantallas.Inicio
import com.example.yoestudio.ui.Pantallas.PantallaConfiguracion
import com.example.yoestudio.ui.Pantallas.PantallaIA
import com.example.yoestudio.ViewModel.AsistenteView
import com.example.yoestudio.ViewModel.ConfiguracionView
import kotlinx.coroutines.CoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember

@Composable
fun AppNavigation(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    configuracionViewModel: ConfiguracionView,
    darkMode: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {


    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {

        composable("inicio") {
            Inicio(
                drawerState = drawerState,
                scope = scope,
                navController = navController
            )
        }

        composable("configuracion") {
            PantallaConfiguracion(
                drawerState = drawerState,
                scope = scope,
                viewModel = configuracionViewModel,
                darkMode = darkMode,
                onToggleTheme = onToggleTheme
            )
        }

        composable("pantalla_ia") {
            val viewModel: AsistenteView = viewModel()
            PantallaIA(viewModel = viewModel)
        }

        composable("bloqueopantalla") {
            // vacío por ahora
        }
    }
}

