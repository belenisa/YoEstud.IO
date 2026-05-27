package com.example.yoestudio.ui.Pantallas

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.widget.Toast

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.yoestudio.ViewModel.ConfiguracionView
import com.example.yoestudio.utils.ConfiguracionBloqueo
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConfiguracion(
    drawerState: DrawerState,
    scope: CoroutineScope,
    viewModel: ConfiguracionView,
    darkMode: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {

    val context = LocalContext.current
    val pm = context.packageManager

    // Obtenemos la lista de apps
    val apps = remember {
        pm.getInstalledApplications(PackageManager.GET_META_DATA)
    }
    val seleccionadas by viewModel.appsSeleccionadas.collectAsState()

    val appsUsuario = remember {
        apps.filter { app ->
            val nombreApp = pm.getApplicationLabel(app).toString()
            val launchIntent = try {
                pm.getLaunchIntentForPackage(app.packageName)
                    ?: pm.getLeanbackLaunchIntentForPackage(app.packageName)
            } catch (e: Exception) {
                null
            }

            val paquetesBloqueados = listOf(
                "com.android.settings",
                "com.android.systemui",
                "com.android.traceur",
                "com.android.contacts",
                "com.google.android.contacts",
                "com.android.phone",
                "com.android.dialer",
                "com.android.mms",
                "com.google.android.dialer",
                "com.google.android.apps.messaging",
                "com.android.simtoolkit",
                "com.android.stk",
                "com.google.android.apps.restore",
                "com.android.switchaccess",
                "com.google.android.marvin.talkback",
                "com.example.yoestudio",
                "com.android,files"
            )

            launchIntent != null &&
                    app.packageName !in paquetesBloqueados &&
                    !nombreApp.contains("Switch Access", ignoreCase = true)
        }.sortedBy {
            pm.getApplicationLabel(it).toString()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuracion") },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ){

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                // MODO OSCURO
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )
                {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Modo Oscuro",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Switch(
                                checked = darkMode,
                                onCheckedChange = onToggleTheme
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // MODO CONCENTRACIÓN
                Button(
                    onClick = {
                        val ahora = System.currentTimeMillis()

                        ConfiguracionBloqueo.tiempoModoConcentracion =
                            ahora + (10 * 60 * 1000)

                        ConfiguracionBloqueo.appsBloqueadas =
                            appsUsuario.map { it.packageName }

                        Toast.makeText(
                            context,
                            "Modo concentración activado (10 min)",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Modo Concentración",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Boton guardar
                Button(
                    onClick = {
                        viewModel.guardarConfiguracionCompleta()
                        Toast.makeText(context, "Cambios aplicados", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Done, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Guardar Configuración",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista Apps
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {

                        items(appsUsuario) { app ->

                            val isSelected = seleccionadas.contains(app.packageName)
                            val segundos = viewModel.segundosApps[app.packageName] ?: ""

                            FilaApp(
                                app = app,
                                pm = pm,
                                seleccionado = isSelected,
                                segundos = segundos,
                                onCheckedChange = { checked ->
                                    viewModel.RecordarAppSeleccionada(app.packageName, checked, appsUsuario)
                                },
                                onSegundosChange = { nuevosSegundos ->
                                    viewModel.actualizarSegundos(app.packageName, nuevosSegundos)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun FilaApp(
    app: ApplicationInfo,
    pm: PackageManager,
    seleccionado: Boolean,
    segundos: String,
    onCheckedChange: (Boolean) -> Unit,
    onSegundosChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = seleccionado,
            onCheckedChange = onCheckedChange
        )

        // Icono de la App
        val iconDrawable = try { pm.getApplicationIcon(app) } catch (e: Exception) { null }
        if (iconDrawable != null) {
            Image(
                painter = rememberDrawablePainter(drawable = iconDrawable),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Nombre de la App
        val nombreApp = try { pm.getApplicationLabel(app).toString() } catch (e: Exception) { app.packageName }

        Text(
            text = nombreApp,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )


        // Input de Segundos
        OutlinedTextField(
            value = segundos,
            onValueChange = { newValue ->
                // Validación básica: solo permitir números
                if (newValue.all { it.isDigit() }) {
                    onSegundosChange(newValue)
                }
            },
            label = { Text("Seg") },
            modifier = Modifier.width(80.dp),
            singleLine = true,
            enabled = seleccionado,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.bodySmall,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        )
    }
}
