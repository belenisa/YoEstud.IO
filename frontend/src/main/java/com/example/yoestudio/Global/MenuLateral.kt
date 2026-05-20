package com.example.yoestudio.Global

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MenuLateral(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    var darkMode by remember { mutableStateOf(true) }

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxHeight()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Usuario", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Box(modifier = Modifier.width(60.dp).height(4.dp).background(Color.LightGray))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))

            MenuItem(Icons.Default.Book, "Ver Ramos") { }
            MenuItem(Icons.Default.CloudUpload, "Subir Material") { }
            MenuItem(Icons.Default.Download, "Documentos descargados") { }
            MenuItem(Icons.Default.School, "YoEstud.io") { }
            MenuItem(Icons.Default.AutoAwesome, "Chat con IA") {
                navController.navigate("pantalla_ia")
                scope.launch { drawerState.close() }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Modo Oscuro", color = MaterialTheme.colorScheme.onSurface)
                Switch(checked = darkMode, onCheckedChange = { darkMode = it })
            }

            MenuItem(Icons.Default.Settings, "Configuración") {
                navController.navigate("configuracion")
                scope.launch { drawerState.close() }
            }

            TextButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(label) },
        selected = false,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface,
            unselectedIconColor = MaterialTheme.colorScheme.onSurface
        )
    )
}
