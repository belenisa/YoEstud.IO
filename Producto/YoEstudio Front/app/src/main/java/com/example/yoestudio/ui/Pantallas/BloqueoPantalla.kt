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

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Espera para usar $app ⏳",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (!enModoConcentracion) {
                Button(onClick = onDesbloquear) {
                    Text("Desbloquear app ahora")
                }
            } else {
                Text(
                    text = "Modo concentración activo 🔒",
                    style = MaterialTheme.typography.bodyLarge
                )
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

