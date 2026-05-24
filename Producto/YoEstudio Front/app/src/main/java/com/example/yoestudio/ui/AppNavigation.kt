package com.example.yoestudio.ui.theme

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.yoestudio.Data.Modelo.UsuarioModelo
import com.example.yoestudio.ui.Pantallas.Inicio
import com.example.yoestudio.ui.Pantallas.PantallaConfiguracion
import com.example.yoestudio.ui.Pantallas.PantallaIA
import com.example.yoestudio.ViewModel.AsistenteView
import com.example.yoestudio.ViewModel.ConfiguracionView
import com.example.yoestudio.ViewModel.UsuarioView
import com.example.yoestudio.ui.Pantallas.CrearUsuarioPantalla
import com.example.yoestudio.ui.Pantallas.IngresarUsuarioPantalla
import kotlinx.coroutines.CoroutineScope


@Composable
fun AppNavigation(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    configuracionViewModel: ConfiguracionView,
    darkMode: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    usuario: UsuarioModelo?
) {


    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {

        composable("inicio") {
            Inicio(
                drawerState = drawerState,
                scope = scope,
                navController = navController,
                darkMode = darkMode,
                onToggleTheme = onToggleTheme,
                usuario = usuario
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

        composable("crearUsuario") {

            val viewModel: UsuarioView = viewModel()

            CrearUsuarioPantalla(
                navController = navController,
                viewModel = viewModel,
                drawerState = drawerState,
                scope = scope
            )
        }

        composable("ingresarUsuario") {

            val viewModel: UsuarioView = viewModel()

            IngresarUsuarioPantalla(
                navController = navController,
                viewModel = viewModel,
                drawerState = drawerState,
                scope = scope
            )
        }
    }
}

