package com.example.yoestudio

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.yoestudio.Global.BotonAsistente
import com.example.yoestudio.Global.MenuLateral
import com.example.yoestudio.Service.MonitoreoBloqueo
import com.example.yoestudio.ViewModel.ConfiguracionView
import com.example.yoestudio.ViewModel.UsuarioView
import com.example.yoestudio.ui.theme.AppNavigation
import com.example.yoestudio.ui.theme.YoEstudioTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val context = LocalContext.current

            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val configuracionViewModel: ConfiguracionView = viewModel()

            val usuarioViewModel: UsuarioView = viewModel()
            val usuario by usuarioViewModel.usuarioActual
            LaunchedEffect(Unit) {
                if (usuarioViewModel.usuarioActual.value == null) {
                    usuarioViewModel.crearUsuarioAuto(context)
                }
            }

            val darkMode by configuracionViewModel.darkMode

            YoEstudioTheme(darkTheme = darkMode) {

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