package com.example.yoestudio.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.yoestudio.Pantallas.*
import com.example.yoestudio.Utils.TokenManager
import com.example.yoestudio.ViewModel.AsistenteView
import com.example.yoestudio.ViewModel.ConcentracionViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun AppNavigation(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    val concentracionViewModel: ConcentracionViewModel = viewModel()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.getToken.collectAsState(initial = null)
    val revisado = remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        if (!revisado.value) {
            revisado.value = true
            if (token != null) {
                navController.navigate("inicio") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login",
        enterTransition = { fadeIn(animationSpec = tween(400)) + slideInHorizontally(animationSpec = tween(400), initialOffsetX = { it }) },
        exitTransition = { fadeOut(animationSpec = tween(400)) + slideOutHorizontally(animationSpec = tween(400), targetOffsetX = { -it }) },
        popEnterTransition = { fadeIn(animationSpec = tween(400)) + slideInHorizontally(animationSpec = tween(400), initialOffsetX = { -it }) },
        popExitTransition = { fadeOut(animationSpec = tween(400)) + slideOutHorizontally(animationSpec = tween(400), targetOffsetX = { it }) }
    ) {
        composable("login") {
            LoginPantalla(navController = navController)
        }

        composable("registro") {
            RegistroPantalla(navController = navController)
        }

        composable("recuperar_contrasena") {
            RecuperarContrasenaPantalla(navController = navController)
        }

        composable("verificar_codigo/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerificarCodigoPantalla(navController = navController, email = email)
        }

        composable("inicio") {
            Inicio(
                drawerState = drawerState,
                scope = scope,
                navController = navController,
                viewModel = concentracionViewModel
            )
        }

        composable("configuracion") {
            PantallaConfiguracion(
                drawerState = drawerState,
                scope = scope,
                navController = navController,
                viewModel = concentracionViewModel
            )
        }

        composable("concentracion_setup") {
            PantallaConcentracionSetup(
                navController = navController,
                viewModel = concentracionViewModel
            )
        }

        composable("pantalla_ia") {
            val viewModel: AsistenteView = viewModel()
            PantallaIA(
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}
