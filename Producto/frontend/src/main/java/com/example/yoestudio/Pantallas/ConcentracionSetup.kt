package com.example.yoestudio.Pantallas

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.yoestudio.ViewModel.ConcentracionViewModel
import com.example.yoestudio.concentracion.ConcentracionBloqueo
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConcentracionSetup(
    navController: NavHostController,
    viewModel: ConcentracionViewModel
) {
    val context = LocalContext.current
    val pm = context.packageManager
    val appsSeleccionadas by viewModel.appsSeleccionadas.collectAsState()
    
    var mostrarTimePicker by remember { mutableStateOf(false) }

    val apps = remember {
        pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 || pm.getLaunchIntentForPackage(it.packageName) != null }
            .sortedBy { pm.getApplicationLabel(it).toString().lowercase() }
    }

    if (mostrarTimePicker) {
        AlertDialog(
            onDismissRequest = { mostrarTimePicker = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("¿Cuánto tiempo vas a estudiar?", color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp) },
            text = {
                Column {
                    listOf(15L, 30L, 45L, 60L).forEach { mins ->
                        Button(
                            onClick = {

                                viewModel.iniciarEnfoque(context, mins)
                                mostrarTimePicker = false
                                navController.popBackStack()
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("$mins minutos", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Modo Concentración", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column {
                // Cabecera informativa
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Selecciona las aplicaciones que suelen distraerte. Las bloquearemos visualmente para ayudarte a enfocar.",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(16.dp),
                        lineHeight = 18.sp
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp), // Espacio para el botón inferior
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(apps) { app ->
                        val isSelected = appsSeleccionadas.contains(app.packageName)
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF1E3A8A) else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleApp(app.packageName) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = rememberDrawablePainter(drawable = pm.getApplicationIcon(app)),
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(pm.getApplicationLabel(app).toString(), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                                    Text(app.packageName, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 10.sp)
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00FF00))
                                }
                            }
                        }
                    }
                }
            }

            // Botón flotante inferior que aparece solo si hay selección
            AnimatedVisibility(
                visible = appsSeleccionadas.isNotEmpty(),
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp)
            ) {
                Button(
                    onClick = { 
                        if (viewModel.tienePermisos(context)) {

                            ConcentracionBloqueo.appsBloqueadas = appsSeleccionadas.toList()
                            ConcentracionBloqueo.tiemposPorApp =
                                viewModel.segundosApps.mapValues { it.value.toIntOrNull() ?: 10 }

                            mostrarTimePicker = true 
                        } else {
                            viewModel.solicitarPermisos(context)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(28.dp),
                    elevation = ButtonDefaults.buttonElevation(8.dp)
                ) {
                    Icon(if (viewModel.tienePermisos(context)) Icons.Default.Timer else Icons.Default.Settings, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        if (viewModel.tienePermisos(context)) "ESTABLECER TIEMPO" else "CONCEDER PERMISOS",
                        fontWeight = FontWeight.ExtraBold, 
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
