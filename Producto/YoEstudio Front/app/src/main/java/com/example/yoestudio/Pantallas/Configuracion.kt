package com.example.yoestudio.Pantallas

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConfiguracion(drawerState: DrawerState, scope: CoroutineScope) {
    val context = LocalContext.current
    val pm = context.packageManager

    // Obtenemos la lista de apps igual que antes
    val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bloqueo de Apps") },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        // LazyColumn es el "ListView" de Compose
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(apps) { app ->
                // Este bloque es el equivalente a tu "getView" y al archivo "item.xml"
                FilaApp(app, pm)
            }
        }
    }
}

@Composable
fun FilaApp(app: ApplicationInfo, pm: PackageManager) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // El icono (usamos Image con el drawable de la app)
        val iconDrawable = pm.getApplicationIcon(app)
        Image(
            painter = rememberDrawablePainter(drawable = iconDrawable),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // El nombre
        Text(
            text = pm.getApplicationLabel(app).toString(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
