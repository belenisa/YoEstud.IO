package com.example.yoestudio

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.yoestudio.Global.BotonAsistente
import com.example.yoestudio.Global.MenuLateral
import com.example.yoestudio.ViewModel.ConfiguracionView
import com.example.yoestudio.ViewModel.UsuarioView
import com.example.yoestudio.ui.theme.AppNavigation
import com.example.yoestudio.ui.theme.YoEstudioTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }

        setContent {
            val context = LocalContext.current

            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val configuracionViewModel: ConfiguracionView = viewModel()

            val usuarioViewModel: UsuarioView = viewModel()
            val usuario by usuarioViewModel.usuarioActual
            LaunchedEffect(Unit) {
                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val id = prefs.getLong("usuario_id", -1)

                if (id == -1L) {
                    usuarioViewModel.crearUsuarioAuto(context)
                } else {
                    usuarioViewModel.cargarUsuario(context)
                }
            }

            val darkMode by configuracionViewModel.darkMode

            YoEstudioTheme(darkTheme = darkMode) {

                val systemUiController = rememberSystemUiController()
                val useDarkIcons = !darkMode
                systemUiController.setStatusBarColor(
                    color = MaterialTheme.colorScheme.surface,
                    darkIcons = !darkMode
                )

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        MenuLateral(navController = navController,
                            drawerState = drawerState,
                            scope = scope,
                            darkMode = darkMode,
                            usuario = usuario,
                            viewModel = usuarioViewModel
                        )
                    }
                ) {
                    Scaffold(
                        contentWindowInsets = WindowInsets.safeDrawing,
                        floatingActionButton = {
                            BotonAsistente(navController)
                        }
                    ) { paddingValues ->

                        Box(modifier = Modifier.padding(paddingValues)) {

                            AppNavigation(
                                navController = navController,
                                drawerState = drawerState,
                                scope = scope,
                                configuracionViewModel = configuracionViewModel,
                                darkMode = darkMode,
                                onToggleTheme = {
                                    configuracionViewModel.modoOscuro(it)
                                },
                                usuario = usuario,
                                usuarioViewModel = usuarioViewModel
                            )

                        }
                    }
                }
            }
        }
    }
}