package com.example.yoestudio.ui.Pantallas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yoestudio.utils.ConfiguracionBloqueo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloqueoPantalla( segundos: Int, app: String,
                     onDesbloquear: () -> Unit)
{
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text("App bloqueada 🚫")

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = onDesbloquear) {
                Text("Usar app")
            }
        }
    }
}



class BloqueoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val segundos = intent.getIntExtra("tiempo", 10)
        val app = intent.getStringExtra("app") ?: ""

        setContent {

            BloqueoPantalla(segundos, app) {
                val ahora = System.currentTimeMillis()

                ConfiguracionBloqueo.appDesbloqueada = app
                ConfiguracionBloqueo.tiempoFin = ahora + (segundos * 1000)

                finish()
            }
        }
    }
}