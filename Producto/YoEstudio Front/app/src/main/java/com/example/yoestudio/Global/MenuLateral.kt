package com.example.yoestudio.Global

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.yoestudio.Data.Modelo.TipoUsuario
import com.example.yoestudio.Data.Modelo.UsuarioModelo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.example.yoestudio.ViewModel.UsuarioView

@Composable
fun MenuLateral(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    darkMode: Boolean,
    nombreUsuario: String = "Usuario",
    usuario: UsuarioModelo?,
    viewModel: UsuarioView
) {
    val context = LocalContext.current
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val drawerItemColors = NavigationDrawerItemDefaults.colors(
        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
    )

    ModalDrawerSheet {
        Text(
            text = "Hola, ${usuario?.nombre ?: "Invitado"}",
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        HorizontalDivider()

        // Inicio
        NavigationDrawerItem(
            label = { Text("Inicio") },
            selected = currentRoute == "inicio",
            colors = drawerItemColors,
            onClick = {
                scope.launch {
                    drawerState.close()

                    val destino = navController.currentBackStackEntry
                    if (destino != null) {
                        navController.navigate("inicio")
                    }
                }
            }
        )

        if (usuario == null || usuario.tipo == TipoUsuario.FREE) {

            NavigationDrawerItem(
                label = { Text("Crear Usuario") },
                selected = currentRoute == "crearUsuario",
                colors = drawerItemColors,
                onClick = {
                    scope.launch {
                        drawerState.close()

                        val destino = navController.currentBackStackEntry
                        if (destino != null) {
                            navController.navigate("crearUsuario")
                        }
                    }
                }
            )

            NavigationDrawerItem(
                label = { Text("Ingresar Usuario") },
                selected = currentRoute == "ingresarUsuario",
                colors = drawerItemColors,
                onClick = {
                    scope.launch {
                        drawerState.close()

                        val destino = navController.currentBackStackEntry
                        if (destino != null) {
                            navController.navigate("ingresarUsuario")
                        }
                    }
                }
            )
        }



        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        //  Configuración
        NavigationDrawerItem(
            label = { Text("Configuración") },
            selected = currentRoute == "configuracion",
            colors = drawerItemColors,
            onClick = {
                scope.launch {
                    drawerState.close()

                    val destino = navController.currentBackStackEntry
                    if (destino != null) {
                        navController.navigate("configuracion")
                    }
                }

            }
        )

        if (usuario?.tipo == TipoUsuario.PREMIUM) {
            NavigationDrawerItem(
                label = { Text("Cerrar sesión") },
                selected = false,
                onClick = {
                    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

                    prefs.edit()
                        .remove("usuario_id")
                        .remove("usuario_creado")
                        .apply()

                    viewModel.usuarioActual.value = null

                    navController.navigate("inicio") {
                        popUpTo(0)
                    }
                    scope.launch { drawerState.close() }
                }
            )
        }

        println("DESTINO ACTUAL: ${navController.currentDestination}")

    }
}