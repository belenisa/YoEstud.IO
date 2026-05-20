package com.example.yoestudio.Pantallas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.yoestudio.Domain.Model.Mensaje
import com.example.yoestudio.ViewModel.AsistenteView

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import android.net.Uri

import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaIA(viewModel: AsistenteView, navController: NavHostController) {
    val mensajes by viewModel.mensajes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    var textoUsuario by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    
    val selectorArchivos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.enviarArchivo(context, it)
        }
    }

    
    LaunchedEffect(mensajes.size, cargando) {
        if (mensajes.isNotEmpty() || cargando) {
            val target = if (cargando) mensajes.size else mensajes.size - 1
            if (target >= 0) {
                listState.animateScrollToItem(target)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Asistente IA", fontWeight = FontWeight.Bold)
                        val statusText = if (cargando) "Pensando..." else "En línea"
                        val statusColor = if (cargando) MaterialTheme.colorScheme.primary else Color.Green
                        Text(statusText, fontSize = 12.sp, color = statusColor)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(16.dp)) }
                items(mensajes) { msg ->
                    BurbujaChat(msg) {
                        viewModel.descargarYGuardarPdf(context, msg)
                        Toast.makeText(context, "Descargando: ${msg.nombreArchivo}", Toast.LENGTH_SHORT).show()
                    }
                }
                if (cargando) {
                    item {
                        BurbujaCargando()
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding() 
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp), 
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { 
                            if (!cargando) {
                                
                                selectorArchivos.launch("*/*") 
                            }
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        enabled = !cargando
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Adjuntar")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    TextField(
                        value = textoUsuario,
                        onValueChange = { textoUsuario = it },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp)),
                        placeholder = { Text("Escribe un mensaje...") },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        maxLines = 4,
                        enabled = !cargando
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    val sendButtonColor = if (textoUsuario.isNotBlank() && !cargando) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.outlineVariant

                    IconButton(
                        onClick = {
                            val textoLimpio = textoUsuario.trim()
                            if (textoLimpio.isNotBlank() && !cargando) {
                                viewModel.enviarA_IA(textoLimpio)
                                textoUsuario = ""
                            }
                        },
                        enabled = textoUsuario.isNotBlank() && !cargando,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(sendButtonColor)
                    ) {
                        if (cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.AutoMirrored.Filled.Send, 
                                contentDescription = "Enviar", 
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BurbujaCargando() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Generando respuesta",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                LinearProgressIndicator(
                    modifier = Modifier.width(20.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Composable
fun BurbujaChat(mensaje: Mensaje, onDownload: () -> Unit = {}) {
    val alineacion = if (mensaje.esDelUsuario) Alignment.End else Alignment.Start
    val colorBurbuja = if (mensaje.esDelUsuario) 
        MaterialTheme.colorScheme.primaryContainer 
    else 
        MaterialTheme.colorScheme.secondaryContainer
    
    val shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (mensaje.esDelUsuario) 16.dp else 4.dp,
        bottomEnd = if (mensaje.esDelUsuario) 4.dp else 16.dp
    )

    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalAlignment = alineacion
        ) {
            Surface(
                shape = shape,
                color = colorBurbuja,
                tonalElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    val maxTexto = if (mensaje.esArchivo) 300 else 2000
                    val textoAMostrar = if (mensaje.texto.length > maxTexto) {
                        mensaje.texto.take(maxTexto) + "...\n\n(Descarga el PDF para ver la prueba completa)"
                    } else {
                        mensaje.texto
                    }

                    Text(
                        text = textoAMostrar,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (mensaje.esArchivo) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.Description,
                                    contentDescription = "Archivo",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Documento PDF Listo",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        "Prueba de evaluación personalizada",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Button(
                                    onClick = onDownload,
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Descargar", fontSize = 13.sp)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = mensaje.nombreArchivo ?: "Prueba_Estudio.pdf",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                modifier = Modifier.align(Alignment.Start)
                            )
                        }
                    }
                }
            }
        }
    }
}
