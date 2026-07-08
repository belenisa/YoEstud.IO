package com.example.yoestudio.Pantallas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yoestudio.ViewModel.ConcentracionViewModel
import com.example.yoestudio.ui.theme.YoEstudioTheme
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yoestudio.Utils.TokenManager
import com.example.yoestudio.ViewModel.PreguntasViewModel
import com.example.yoestudio.concentracion.ConcentracionBloqueo
import kotlinx.coroutines.delay
import com.example.yoestudio.preguntas.CreadorPreguntas
import com.example.yoestudio.preguntas.EstadoPreguntas

class BloqueoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tiempoRestante = intent.getStringExtra("tiempo") ?: "10"
        val app = intent.getStringExtra("app") ?: ""
        val enModoConcentracion =
            System.currentTimeMillis() < ConcentracionBloqueo.tiempoModoConcentracion

        setContent {

            val concentracionnViewModel: ConcentracionViewModel = viewModel()
            val themeState = remember { mutableStateOf(false) }
            YoEstudioTheme(themeState = themeState) {

                PantallaBloqueoReal(
                    tiempo = tiempoRestante,
		    app, enModoConcentracion
                ) {
                    ConcentracionBloqueo.esModoConcentracion = false

                    val intentHome = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_HOME)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intentHome)

                    finish()
                }
            }
        }
    }
}

@Composable
fun PantallaBloqueoReal(
    tiempo: String,
    app: String,
    enModoConcentracion: Boolean,
    onHome: () -> Unit
) {

    BackHandler {}
    val context = LocalContext.current
    val tokenManager = remember {
        TokenManager(context)
    }

    val idUsuario by tokenManager
        .getUserId
        .collectAsState(initial = null)

    var segundosRestantes by rememberSaveable {
        mutableStateOf(tiempo.toIntOrNull() ?: 10)
    }
    val esModoConcentracion = ConcentracionBloqueo.esModoConcentracion

    LaunchedEffect(segundosRestantes, esModoConcentracion) {
        if (!esModoConcentracion && segundosRestantes > 0) {
            delay(1000)
            segundosRestantes--
        }
    }


    val preguntasViewModel: PreguntasViewModel = viewModel()

    LaunchedEffect(idUsuario) {

        idUsuario?.let { usuarioId ->

            if (usuarioId != 0L) {
                preguntasViewModel.cargarPreguntas(usuarioId)
            }
        }
    }


    val modoMatematicas by EstadoPreguntas.usarMatematicas
    val dificultad by preguntasViewModel.dificultad.collectAsState()
    val seleccionadas = preguntasViewModel.misPreguntaSeleccionada
    val preguntasUsuario = preguntasViewModel.preguntasUsuario


    var preguntaActual by remember(
        modoMatematicas,
        seleccionadas,
        preguntasUsuario
    ) {
        mutableStateOf(
            when {
                modoMatematicas -> {
                    CreadorPreguntas.generarBasica()
                }

                seleccionadas.isNotEmpty() -> {
                    seleccionadas.random().let { it.pregunta to it.respuesta }
                }

                preguntasUsuario.isNotEmpty() -> {
                    preguntasUsuario.random().let { it.pregunta to it.respuesta }
                }

                else -> {
                    CreadorPreguntas.generarBasica()
                }
            }
        )
    }


    var respuesta by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Block,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(100.dp)
            )
            
            Spacer(Modifier.height(32.dp))
            
            Text(
                "¡ZONA DE ESTUDIO!",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )
            
            Spacer(Modifier.height(16.dp))

            Text(
                "Responde correctamente para continuar",
                fontSize = 18.sp
            )

            Spacer(Modifier.height(24.dp))


            Spacer(Modifier.height(24.dp))

            Text(preguntaActual.first,
                color = MaterialTheme.colorScheme.onBackground)

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = respuesta,
                onValueChange = { respuesta = it },
                label = { Text("Respuesta") }
            )

            if (error) {
                Text("Incorrecto ❌", color = Color.Red)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (respuesta.trim()
                            .equals(preguntaActual.second, ignoreCase = true)
                    ) {

                        error = false

                        if (enModoConcentracion) {

                            if (segundosRestantes <= 0) {
                                onHome()
                            } else {

                                preguntaActual = when {
                                    modoMatematicas -> {
                                        CreadorPreguntas.generarBasica()
                                    }

                                    seleccionadas.isNotEmpty() -> {
                                        seleccionadas.random().let { it.pregunta to it.respuesta }
                                    }

                                    preguntasUsuario.isNotEmpty() -> {
                                        preguntasUsuario.random().let { it.pregunta to it.respuesta }
                                    }

                                    else -> {
                                        CreadorPreguntas.generarBasica()
                                    }
                                }

                                respuesta = ""
                            }

                        } else {
                            val tiempo = ConcentracionBloqueo.tiemposPorApp[app]
                                ?: ConcentracionBloqueo.tiempoDefault

                            ConcentracionBloqueo.tiempoFin =
                                System.currentTimeMillis() + (tiempo * 1000)

                            val intent = context.packageManager.getLaunchIntentForPackage(app)
                            intent?.addFlags(
                                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                            )

                            respuesta = ""
                            error = false
                            context.startActivity(intent)
                        }

                    } else {
                        error = true
                        respuesta = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Responder", fontWeight = FontWeight.Bold)
            }
        }
    }
}
