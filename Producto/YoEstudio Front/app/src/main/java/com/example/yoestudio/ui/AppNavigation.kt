package com.example.yoestudio.ui.theme

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
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

@Composable
fun AppNavigation(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    configuracionViewModel: ConfiguracionView
) {
    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable("inicio") {
            Inicio(
                drawerState = drawerState,
                scope = scope,
                navController = navController   // ✅ agregado
            )
        }

        composable("configuracion") {
            PantallaConfiguracion(
                drawerState = drawerState,
                scope = scope,
                viewModel = configuracionViewModel
            )
        }

        composable("pantalla_ia") {
            val viewModel: AsistenteView = viewModel()
            PantallaIA(viewModel = viewModel)
        }

        composable("bloqueopantalla"){

        }
    }
}
