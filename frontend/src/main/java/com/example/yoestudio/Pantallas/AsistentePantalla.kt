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
import androidx.compose.material.icons.filled.*
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
import com.example.yoestudio.Domain.Model.Mensaje
import com.example.yoestudio.ViewModel.AsistenteView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaIA(viewModel: AsistenteView, navController: NavHostController) {
    val mensajes by viewModel.mensajes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    var textoUsuario by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    LaunchedEffect(mensajes.size) {
        if (mensajes.isNotEmpty()) {
            listState.animateScrollToItem(mensajes.size - 1)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chat con IA", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF0D1B2A))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF0A1628))
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
            ) {
                items(mensajes) { msg ->
                    BurbujaChat(msg) {
                        viewModel.descargarArchivo(context, msg)
                    }
                }
                if (cargando) {
                    item { BurbujaCargando() }
                }
            }

            Surface(
                color = Color(0xFF1A2B3C),
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray, modifier = Modifier.padding(4.dp))
                    Icon(Icons.Default.Code, contentDescription = null, tint = Color.Gray, modifier = Modifier.padding(4.dp))
                    Icon(Icons.Default.Mic, contentDescription = null, tint = Color.Gray, modifier = Modifier.padding(4.dp))

                    Spacer(modifier = Modifier.width(8.dp))

                    TextField(
                        value = textoUsuario,
                        onValueChange = { textoUsuario = it },
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(24.dp)),
                        placeholder = { Text("Pregúntale a la IA...", color = Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            containerColor = Color(0xFF0D1B2A)
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (textoUsuario.isNotBlank()) {
                                viewModel.enviarA_IA(textoUsuario)
                                textoUsuario = ""
                            }
                        },
                        modifier = Modifier.clip(CircleShape).background(Color(0xFF0000FF))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun BurbujaChat(mensaje: Mensaje, onDownload: () -> Unit) {
    val alineacion = if (mensaje.esDelUsuario) Alignment.End else Alignment.Start
    val colorBurbuja = if (mensaje.esDelUsuario) Color.White else Color(0xFF1A2B3C)
    val colorTexto = if (mensaje.esDelUsuario) Color.Black else Color.White

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalAlignment = alineacion
    ) {
        Surface(
            color = colorBurbuja,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = mensaje.texto, color = colorTexto)
                if (mensaje.esArchivo) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onDownload,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0000FF))
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("📄 Descargar prueba generada")
                    }
                }
            }
        }
    }
}

@Composable
fun BurbujaCargando() {
    Box(modifier = Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.CenterStart) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF0000FF))
    }
}
