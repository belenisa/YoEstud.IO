package com.example.yoestudio.Pantallas

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.yoestudio.Domain.Model.Mensaje
import com.example.yoestudio.Utils.TokenManager
import com.example.yoestudio.ViewModel.AsistenteView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaIA(viewModel: AsistenteView, navController: NavHostController) {

    val mensajes by viewModel.mensajes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val cargandoHistorial by viewModel.cargandoHistorial.collectAsState()
    var textoUsuario by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val nombreUsuario by tokenManager.getName.collectAsState(initial = null)
    val primerNombre = remember(nombreUsuario) {
        nombreUsuario?.split("\\s+".toRegex())?.firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Usuario"
    }
    
    // Estado para el archivo seleccionado
    var nombreArchivo by remember { mutableStateOf<String?>(null) }
    var archivoBase64 by remember { mutableStateOf<String?>(null) }

    // Contexto de corrutina para realizar la lectura de archivos pesados en hilos secundarios
    val scope = rememberCoroutineScope()

    // Launcher para seleccionar archivos
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: android.net.Uri? ->
        if (uri == null) {
            Log.d("PantallaIA", "Selección cancelada por el usuario.")
            return@rememberLauncherForActivityResult
        }

        scope.launch {
            try {
                val contentResolver = context.contentResolver

                // Otorgar permisos persistentes para garantizar el acceso al stream en segundo plano
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    Log.w("PantallaIA", "No se pudo persistir el permiso URI de forma permanente: ${e.message}")
                }

                // Extraer el nombre legible del archivo en un hilo I/O
                var name: String? = null
                withContext(Dispatchers.IO) {
                    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        if (cursor.moveToFirst() && nameIndex != -1) {
                            name = cursor.getString(nameIndex)
                        }
                    }
                }
                nombreArchivo = name ?: "archivo_adjunto.pdf"

                // Leer y codificar a Base64 en segundo plano liberando el hilo principal (Main Thread)
                val resultadoBase64 = withContext(Dispatchers.IO) {
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        val bytes = inputStream.readBytes()
                        // NO_WRAP previene cortes de cadena inválidos que rompen parsers en Spring Boot
                        android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                    }
                }

                if (resultadoBase64 != null) {
                    archivoBase64 = resultadoBase64
                    Toast.makeText(context, "Archivo adjunto: $nombreArchivo", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "No fue posible procesar el archivo.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("PantallaIA", "Fallo al procesar el archivo seleccionado", e)
                Toast.makeText(context, "Error al cargar el archivo del sistema.", Toast.LENGTH_LONG).show()
            }
        }
    }

    LaunchedEffect(mensajes.size, cargando) {
        if (mensajes.isNotEmpty()) {
            listState.animateScrollToItem(mensajes.size - 1)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Asistente Académico", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(if (cargando) "IA escribiendo..." else "En línea", color = if (cargando) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else Color(0xFF00FF00), fontSize = 11.sp)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (cargandoHistorial) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.height(8.dp))
                                Text("Cargando historial...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 13.sp)
                            }
                        }
                    }
                }
                items(mensajes) { msg ->
                    BurbujaChat(msg, onDownload = { viewModel.descargarArchivo(context, msg) }, nombreUsuario = primerNombre)
                }
                if (cargando) {
                    item { BurbujaCargando() }
                }
            }

            // Indicador de archivo seleccionado
            nombreArchivo?.let { name ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(name, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    IconButton(onClick = { nombreArchivo = null; archivoBase64 = null }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Input Bar con colores anteriores
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { launcher.launch(
                            arrayOf(
                                "application/pdf",
                                "application/msword",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                            )
                        ) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }

                    TextField(
                        value = textoUsuario,
                        onValueChange = { textoUsuario = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        placeholder = { Text("Escribe un mensaje...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 5,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    IconButton(
                        onClick = {
                            if (textoUsuario.isNotBlank() || archivoBase64 != null) {
                                viewModel.enviarA_IA(textoUsuario, nombreArchivo, archivoBase64, primerNombre)
                                textoUsuario = ""
                                nombreArchivo = null
                                archivoBase64 = null
                            }
                        },
                        enabled = (textoUsuario.isNotBlank() || archivoBase64 != null) && !cargando,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if ((textoUsuario.isNotBlank() || archivoBase64 != null) && !cargando) 
                                    Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary))
                                else 
                                    Brush.linearGradient(listOf(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f), MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)))
                            )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BurbujaChat(mensaje: Mensaje, onDownload: () -> Unit, nombreUsuario: String = "Usuario") {
    val alineacion = if (mensaje.esDelUsuario) Alignment.End else Alignment.Start
    val colorBurbuja = if (mensaje.esDelUsuario) 
        Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary)) 
    else 
        Brush.linearGradient(listOf(MaterialTheme.colorScheme.surface, Color(0xFF253545)))
        
    val shape = if (mensaje.esDelUsuario) 
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp) 
    else 
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alineacion
    ) {
        Surface(
            modifier = Modifier
                .shadow(4.dp, shape)
                .background(colorBurbuja, shape)
                .clip(shape),
            color = Color.Transparent
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = mensaje.texto, 
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )
                if (mensaje.esArchivo) {
                    Button(
                        onClick = onDownload,
                        modifier = Modifier.padding(top = 10.dp).fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.width(8.dp))
                            Text("Descargar PDF", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
        Text(
            text = if (mensaje.esDelUsuario) nombreUsuario else "Asistente",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 4.dp, start = 8.dp, end = 8.dp)
        )
    }
}

@Composable
fun BurbujaCargando() {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF253545))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.primary, strokeWidth = 2.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Text("Pensando...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 12.sp)
    }
}
