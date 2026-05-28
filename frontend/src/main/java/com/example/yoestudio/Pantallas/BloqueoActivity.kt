package com.example.yoestudio.Pantallas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class BloqueoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val tiempoRestante = intent.getStringExtra("tiempo_restante") ?: "00:00"

        setContent {
            PantallaBloqueoReal(tiempoRestante) {
                // Volver al home del celular
                val startMain = Intent(Intent.ACTION_MAIN)
                startMain.addCategory(Intent.CATEGORY_HOME)
                startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(startMain)
                finish()
            }
        }
    }
    
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Bloquear el botón atrás
    }
}

@Composable
fun PantallaBloqueoReal(tiempo: String, onHome: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0A1628)
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
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                "Esta aplicación está bloqueada para ayudarte a concentrarte.",
                color = Color.Gray,
                fontSize = 18.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "Tiempo restante: $tiempo",
                color = Color(0xFF0000FF),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(Modifier.height(64.dp))
            
            Button(
                onClick = onHome,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0000FF)),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(Icons.Default.Home, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text("SALIR AL INICIO", fontWeight = FontWeight.Bold)
            }
        }
    }
}
