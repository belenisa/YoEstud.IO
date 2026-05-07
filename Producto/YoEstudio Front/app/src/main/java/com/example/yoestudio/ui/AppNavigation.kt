package com.example.yoestudio.ui.theme

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.yoestudio.Global.BotonAsistente
import com.example.yoestudio.Pantallas.Inicio
import com.example.yoestudio.Pantallas.PantallaConfiguracion
import com.example.yoestudio.Pantallas.PantallaIA
import com.example.yoestudio.ViewModel.AsistenteView
import kotlinx.coroutines.CoroutineScope

@Composable
fun AppNavigation(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable("inicio") {
            Inicio(drawerState = drawerState, scope = scope)
        }
        composable("configuracion") {
            PantallaConfiguracion(drawerState = drawerState, scope = scope)
        }

        composable("pantalla_ia") {
            val viewModel: AsistenteView = viewModel()
            PantallaIA(viewModel = viewModel)
        }
    }
}