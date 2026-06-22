package com.example.yoestudio.Pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.yoestudio.Global.BotonIA_Circular
import com.example.yoestudio.Global.MenuLateral
import com.example.yoestudio.ViewModel.ConcentracionViewModel
import com.example.yoestudio.ui.theme.LocalThemeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class ApuntePost(
    val autor: String,
    val carrera: String,
    val contenido: String,
    val ramo: String,
    val tiempo: String,
    val likes: Int,
    val comentarios: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Inicio(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavHostController,
    viewModel: ConcentracionViewModel
) {
    val estaActivo by viewModel.estaActivo.collectAsState()
    val tiempoRestante by viewModel.tiempoRestante.collectAsState()
    val context = LocalContext.current
    val darkModeState = LocalThemeManager.current

    val feedApuntes = remember {
        listOf(
            ApuntePost("Marcel Droguett", "Ingeniería Civil", "Acabo de subir el resumen completo de Cálculo II. ¡Espero les sirva!", "Cálculo II", "2h", 24, 5),
            ApuntePost("Belén Alarcón", "Diseño", "Guía de color y tipografía para el examen final. PDF disponible en mi perfil.", "Taller de Diseño", "4h", 42, 12),
            ApuntePost("Matías Sanhueza", "Psicología", "Mapa mental sobre las teorías de Freud. Muy útil para repasar rápido.", "Psicología General", "6h", 15, 3),
            ApuntePost("Benjamín Belmar", "Derecho", "Apuntes de Derecho Romano procesal. Viene con casos de ejemplo.", "Derecho Romano", "1d", 89, 21),
            ApuntePost("Estudiante 2026", "Medicina", "Resumen de anatomía: Sistema Nervioso Central.", "Anatomía I", "2d", 156, 45)
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuLateral(
                navController = navController,
                drawerState = drawerState,
                scope = scope
            )
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text("YOESTUD.IO", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground, letterSpacing = 1.sp)
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { 
                                if (estaActivo) viewModel.cancelarEnfoque(context)
                                else navController.navigate("concentracion_setup")
                            }
                        ) {
                            Icon(
                                imageVector = if (estaActivo) Icons.Default.NotificationsActive else Icons.Default.Timer,
                                contentDescription = null,
                                tint = if (estaActivo) Color(0xFF00FF00) else MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            floatingActionButton = {
                if (!estaActivo) {
                    BotonIA_Circular(navController)
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                if (estaActivo) {
                    Surface(
                        color = Color(0xFF00FF00).copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.cancelarEnfoque(context) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFF00FF00), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Modo Concentración: ${viewModel.formatearTiempo(tiempoRestante)} (Toca para parar)",
                                color = Color(0xFF00FF00),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(feedApuntes) { post ->
                        PostItem(post)
                        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f), thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun PostItem(post: ApuntePost) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), MaterialTheme.colorScheme.primary))),
            contentAlignment = Alignment.Center
        ) {
            Text(post.autor.take(1), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(post.autor, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text("@${post.ramo.replace(" ", "").lowercase()}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 14.sp)
                Text(" · ${post.tiempo}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 14.sp)
            }
            
            Text(post.carrera, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = post.contenido,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "#${post.ramo}",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionIcon(Icons.Outlined.ChatBubbleOutline, post.comentarios.toString())
                ActionIcon(Icons.Outlined.FavoriteBorder, post.likes.toString())
                ActionIcon(Icons.Outlined.Share, "")
            }
        }
    }
}

@Composable
fun ActionIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, count: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
        if (count.isNotEmpty()) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(count, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 13.sp)
        }
    }
}
