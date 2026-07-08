package com.example.yoestudio.Pantallas

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.yoestudio.Utils.TokenManager
import com.example.yoestudio.ViewModel.ConcentracionViewModel
import com.example.yoestudio.concentracion.ConcentracionBloqueo
import com.example.yoestudio.ui.theme.LocalThemeManager
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConfiguracion(
    drawerState: DrawerState, 
    scope: CoroutineScope, 
    navController: NavHostController,
    viewModel: ConcentracionViewModel
) {
    val context = LocalContext.current
    val pm = context.packageManager
    val appsSeleccionadas by viewModel.appsSeleccionadas.collectAsState()
    val tokenManager = remember { TokenManager(context) }
    val darkModeManager = LocalThemeManager.current
    val scope2 = rememberCoroutineScope()

    // Cargar solo apps con icono y nombre (filtrar un poco las del sistema)
    val apps = remember {
        pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { app ->

                val nombreApp = pm.getApplicationLabel(app).toString()

                val launchIntent = try {
                    pm.getLaunchIntentForPackage(app.packageName)
                        ?: pm.getLeanbackLaunchIntentForPackage(app.packageName)
                } catch (e: Exception) {
                    null
                }

                val esSistema = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                val paquetesBloqueados = listOf(
                    "com.android.settings",
                    "com.android.systemui",
                    "com.android.traceur",
                    "com.android.phone",
                    "com.android.dialer",
                    "com.google.android.dialer",
                    "com.android.mms",
                    "com.google.android.apps.messaging",
                    "com.android.contacts",
                    "com.google.android.contacts",
                    "com.android.files",
                    "com.google.android.documentsui",
                    "com.android.switchaccess",
                    "com.google.android.marvin.talkback",
                    "com.android.simtoolkit",
                    "com.android.stk",
                    "com.google.android.apps.restore",
                    "com.example.yoestudio"
                )

                val esAppValida = launchIntent != null
                val noEsBloqueada = app.packageName !in paquetesBloqueados
                val nombreValido = !nombreApp.contains("Switch Access", ignoreCase = true)


                esAppValida && noEsBloqueada && nombreValido
            }
            .sortedBy {
                pm.getApplicationLabel(it).toString().lowercase()
            }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Configuración", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Selecciona apps a bloquear", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Modo oscuro toggle
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (darkModeManager.value) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Modo oscuro", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Switch(
                        checked = darkModeManager.value,
                        onCheckedChange = { checked ->
                            darkModeManager.value = checked
                            scope2.launch {
                                tokenManager.setDarkMode(checked)
                            }
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
                    )
                }
            }

            // Resumen de selección
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${appsSeleccionadas.size} apps seleccionadas",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    if (appsSeleccionadas.isNotEmpty()) {
                        val resumenSegundos = viewModel.segundosApps.values
                            .mapNotNull { it.toIntOrNull() }
                            .distinct()
                            .sorted()

                        val textoSegundos = if (resumenSegundos.isNotEmpty()) {
                            resumenSegundos.joinToString("/") + "s"
                        } else {
                            "10s"
                        }
                        //boton guardar
                        Button(
                            onClick = {
                                if (viewModel.tienePermisos(context)) {

                                    viewModel.guardarConfiguracionCompleta()

                                    ConcentracionBloqueo.tiempoFin = 0

                                    Toast.makeText(context, "Configuración guardada", Toast.LENGTH_SHORT).show()

                                } else {
                                    viewModel.solicitarPermisos(context)
                                }
                            }
                        ) {
                            Text(
                                if (viewModel.tienePermisos(context))
                                    "Guardar"
                                else
                                    "Guardar"
                            )
                        }
                    }
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(apps) { app ->
                    val isSelected = appsSeleccionadas.contains(app.packageName)
                    val segundos = viewModel.segundosApps[app.packageName] ?: ""

                    FilaApp(
                        app = app,
                        pm = pm,
                        segundos = segundos,
                        isSelected = appsSeleccionadas.contains(app.packageName),
                        onToggle = { viewModel.toggleApp(app.packageName) },
                        onSegundosChange = { nuevo ->
                            viewModel.actualizarSegundos(app.packageName, nuevo)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FilaApp(app: ApplicationInfo, pm: PackageManager,
            segundos : String, isSelected: Boolean, onToggle: () -> Unit,
            onSegundosChange: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary.copy(0.5f)
            else
                MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconDrawable = pm.getApplicationIcon(app)
            Image(
                painter = rememberDrawablePainter(drawable = iconDrawable),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pm.getApplicationLabel(app).toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
                Text(
                    text = app.packageName,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
            }
            if (isSelected) {
                OutlinedTextField(
                    value = segundos,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() }) {
                            onSegundosChange(it)
                        }
                    },
                    placeholder = { Text("Seg") },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .width(60.dp)
                        .height(50.dp)
                )
            }
        }
    }
}
