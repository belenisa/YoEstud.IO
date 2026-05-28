package com.example.yoestudio

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.yoestudio.ui.theme.AppNavigation
import com.example.yoestudio.ui.theme.YoEstudioTheme
import com.example.yoestudio.Utils.TokenManager

class MainActivity : ComponentActivity() {

    private var mostrarSnackbar = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkIntent(intent)

        setContent {
            val tokenManager = remember { TokenManager(this) }
            val darkMode by tokenManager.isDarkMode.collectAsState(initial = true)
            val themeState = remember(darkMode) { mutableStateOf(darkMode) }

            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(mostrarSnackbar.value) {
                if (mostrarSnackbar.value) {
                    snackbarHostState.showSnackbar(
                        message = "Volviste al estudio \uD83D\uDCAA",
                        duration = SnackbarDuration.Short
                    )
                    mostrarSnackbar.value = false
                }
            }

            YoEstudioTheme(themeState = themeState) {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { padding ->
                    AppNavigation(
                        navController = navController,
                        drawerState = drawerState,
                        scope = scope
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        checkIntent(intent)
    }

    private fun checkIntent(intent: Intent?) {
        if (intent?.getBooleanExtra("desde_concentracion", false) == true) {
            mostrarSnackbar.value = true
        }
    }
}
