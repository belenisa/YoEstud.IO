package com.example.yoestudio.Pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yoestudio.ViewModel.PreguntasViewModel
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.example.yoestudio.Utils.TokenManager
import com.example.yoestudio.preguntas.EstadoPreguntas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RamosPantalla(navController: NavHostController) {

    val preguntasViewModel: PreguntasViewModel = viewModel()
    val context = LocalContext.current

    val tokenManager = remember {
        TokenManager(context)
    }


    val idUsuario by tokenManager
        .getUserId
        .collectAsState(initial = null)


    LaunchedEffect(idUsuario) {

        idUsuario?.let { usuarioId ->

            if (usuarioId != 0L) {
                preguntasViewModel.cargarPreguntas(usuarioId)
            }
        }
    }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {

            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Ramos",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                "Ramos Predeterminados",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            CardMatematicas()

            Spacer(modifier = Modifier.height(16.dp))

            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Mis preguntas",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))


            SeccionPreguntas(
                preguntasViewModel,
                idUsuario
            )


            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Mis Ramos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun CardMatematicas() {

    var expandido by remember { mutableStateOf(false) }
    var dificultadSeleccionada by remember { mutableStateOf("") }
    val preguntasViewModel: PreguntasViewModel = viewModel()


    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {
                expandido = !expandido
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Matemáticas",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f) // empuja el icono a la derecha
                )

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(6.dp)
                        .clickable
                        {}
                )
            }

            if (expandido) {
                Column(modifier = Modifier.padding(top = 12.dp)) {

                    Text(
                        text = "Básico",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(
                                if (dificultadSeleccionada == "basico")
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                else
                                    Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )

                            .clickable {
                                dificultadSeleccionada = "basico"
                                preguntasViewModel.cambiarDificultad("basico")
                                EstadoPreguntas.usarMatematicas.value = true
                            }
                            .padding(8.dp)
                    )

                    Text(
                        text = "Medio",
                        color = if (dificultadSeleccionada == "medio")
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                dificultadSeleccionada = "medio"
                            }
                            .padding(8.dp)
                    )

                    Text(
                        text = "Alto",
                        color = if (dificultadSeleccionada == "alto")
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                dificultadSeleccionada = "alto"
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun SeccionPreguntas(
    preguntasViewModel: PreguntasViewModel,
    idUsuario: Long?
) {

    var expandido by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    var mensaje by remember { mutableStateOf<String?>(null) }

    var mostrarFormulario by remember { mutableStateOf(false) }
    var textoPregunta by remember { mutableStateOf("") }
    var textoRespuesta by remember { mutableStateOf("") }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            mensaje = null
        }
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { expandido = !expandido }
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Preguntas",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(6.dp)
                        .clickable
                        {}
                )
            }

            if (expandido) {

                Column(modifier = Modifier.padding(top = 12.dp)) {

                    Text(
                        text = "Agregar preguntas",
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clickable { mostrarFormulario = !mostrarFormulario }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Nueva pregunta")
                    }

                    if (mostrarFormulario) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                        ) {

                            OutlinedTextField(
                                value = textoPregunta,
                                onValueChange = { textoPregunta = it },
                                label = { Text("Pregunta") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(8.dp))

                            OutlinedTextField(
                                value = textoRespuesta,
                                onValueChange = { textoRespuesta = it },
                                label = { Text("Respuesta") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {

                                TextButton(onClick = {
                                    mostrarFormulario = false
                                }) {
                                    Text("Cancelar")
                                }

                                Spacer(Modifier.width(8.dp))

                                Button(onClick = {

                                    if (textoPregunta.isBlank() || textoRespuesta.isBlank()) {
                                        mensaje = "Completa todos los campos"
                                    } else {

                                        preguntasViewModel.agregarPregunta(
                                            idUsuario ?: return@Button,
                                            textoPregunta,
                                            textoRespuesta
                                        )

                                        textoPregunta = ""
                                        textoRespuesta = ""
                                        mostrarFormulario = false

                                        mensaje = "Pregunta guardada"
                                    }

                                }) {
                                    Text("Guardar")
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Mis preguntas",
                        fontWeight = FontWeight.SemiBold
                    )

                    preguntasViewModel.preguntasUsuario.forEach { pregunta ->

                        val seleccionada = preguntasViewModel.misPreguntaSeleccionada.contains(pregunta)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(
                                    if (seleccionada)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    else
                                        Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    preguntasViewModel.seleccionarPregunta(pregunta)
                                    EstadoPreguntas.usarMatematicas.value = false
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = pregunta.pregunta,
                                modifier = Modifier.weight(1f),
                                color = if (seleccionada)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )

                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Eliminar",
                                tint = Color.Red,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        preguntasViewModel.eliminarPregunta(pregunta)
                                        mensaje = "Pregunta eliminada"
                                    }
                            )
                        }
                    }
                }
            }
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(16.dp)
    )
}
