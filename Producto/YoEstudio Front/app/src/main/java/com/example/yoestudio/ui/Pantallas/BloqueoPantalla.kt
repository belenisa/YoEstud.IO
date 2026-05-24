package com.example.yoestudio.ui.Pantallas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yoestudio.utils.ConfiguracionBloqueo
import kotlinx.coroutines.delay

@Composable
fun BloqueoPantalla(
    segundos: Int,
    app: String,
    enModoConcentracion: Boolean,
    onDesbloquear: () -> Unit
) {


    // Estado para la cuenta regresiva local
    var segundosRestantes by remember { mutableStateOf(segundos) }

    // Efecto para disminuir el contador cada segundo
    LaunchedEffect(Unit) {
        while (segundosRestantes > 0) {
            delay(1000L)
            segundosRestantes--
        }
    }
    //temporal mientras se agrega de forma más dinamica
    val preguntas = listOf(
        "¿Cuánto es 2 + 2?" to "4",
        "Capital de Chile" to "Santiago",
        "¿5 x 3?" to "15"
    )
    var indice by remember { mutableStateOf(0) }
    var respuesta by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var preguntaAleatoria by remember { mutableStateOf(preguntas.random()) }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            //temporal mientras se agrega de forma más dinamica
            val pregunta = if (enModoConcentracion) {
                preguntas[indice]
            } else {
                preguntaAleatoria
            }

            Text(
                text = "Responde para continuar",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(pregunta.first)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = respuesta,
                onValueChange = { respuesta = it },
                label = { Text("Respuesta") }
            )

            if (error) {
                Text("Respuesta incorrecta", color = androidx.compose.ui.graphics.Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (respuesta.trim().equals(pregunta.second, ignoreCase = true)) {

                        if (enModoConcentracion) {
                            if (indice < preguntas.size - 1) {
                                indice++
                            } else {
                                indice = 0
                            }
                            respuesta = ""
                            error = false

                        } else {
                            onDesbloquear()
                        }

                    } else {
                        error = true
                        respuesta = ""
                    }
                }
            ) {
                Text("Responder")
            }

        }
    }
}


class BloqueoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val segundos = intent.getIntExtra("tiempo", 10)
        val app = intent.getStringExtra("app") ?: ""

        val enModoConcentracion =
            System.currentTimeMillis() < ConfiguracionBloqueo.tiempoModoConcentracion

        setContent {
            BloqueoPantalla(segundos, app, enModoConcentracion) {

                val ahora = System.currentTimeMillis()

                // ✅ reiniciar el cronómetro desde cero
                ConfiguracionBloqueo.tiempoFin =
                    ahora + (segundos * 1000)

                println("✅ NUEVO TIEMPO FIN: ${ConfiguracionBloqueo.tiempoFin}")

                finish()
            }
        }
    }
}