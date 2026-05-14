package com.example.yoestudio.ui.Pantallas

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.yoestudio.ViewModel.ConfiguracionView
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConfiguracion(
    drawerState: DrawerState,
    scope: CoroutineScope,
    viewModel: ConfiguracionView
) {

    val context = LocalContext.current
    val pm = context.packageManager

    // Obtenemos la lista de apps igual que antes
    val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)


    val seleccionadas by viewModel.appsSeleccionadas.collectAsState()


    val appsUsuario = apps.filter { app ->

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
            "com.google.android.marvin.talkback"

        )

        launchIntent != null &&
                app.packageName !in paquetesBloqueados &&
                !nombreApp.contains("Switch Access", ignoreCase = true)

    }.sortedBy {
        pm.getApplicationLabel(it).toString()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // LISTA DE APPS
            LazyColumn {
                items(appsUsuario) { app ->

                    val isSelected = seleccionadas.contains(app.packageName)

                    FilaApp(
                        app = app,
                        pm = pm,
                        seleccionado = isSelected,
                        onCheckedChange = { checked ->
                            viewModel.RecordarAppSeleccionada(app.packageName, checked)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FilaApp(app: ApplicationInfo, pm: PackageManager,
            seleccionado: Boolean,
            onCheckedChange: (Boolean) -> Unit)
{ Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Checkbox(
            checked = seleccionado,
            onCheckedChange = onCheckedChange
        )

        Spacer(modifier = Modifier.width(8.dp))

        val iconDrawable = try {
            pm.getApplicationIcon(app)
        } catch (e: Exception) {
            null
        }

        if (iconDrawable != null) {
            Image(
                painter = rememberDrawablePainter(drawable = iconDrawable),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        val nombreApp = try {
            pm.getApplicationLabel(app).toString()
        } catch (e: Exception) {
            app.packageName
        }

        Text(
            text = nombreApp,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


