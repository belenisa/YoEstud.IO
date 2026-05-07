package com.example.yoestudio.Pantallas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yoestudio.Data.Network.AsistenteModelo
import com.example.yoestudio.ViewModel.AsistenteView


@Composable
fun PantallaIA(viewModel: AsistenteView) {
    val mensajes by viewModel.mensajes.collectAsState()
    var textoUsuario by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Mensajes
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
            items(mensajes) { msg ->
                    BurbujaChat(msg)
            }
        }

        // Input
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Aquí abres el selector de archivos */ }) {
                Icon(Icons.Default.Add, contentDescription = "Adjuntar")
            }

            TextField(
                value = textoUsuario,
                onValueChange = { textoUsuario = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Pregúntale a la IA...") }
            )

            IconButton(onClick = {
                viewModel.enviarA_IA(textoUsuario)
                textoUsuario = ""
            }) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Color.Blue)
            }
        }
    }
}

@Composable
fun BurbujaChat(mensaje: AsistenteModelo) {
    val alineacion = if (mensaje.esDelUsuario) Alignment.End else Alignment.Start
    val color = if (mensaje.esDelUsuario) Color(0xFFD1E7FF) else Color(0xFFF1F1F1)

    Column(modifier = Modifier.fillMaxWidth().padding(4.dp), horizontalAlignment = alineacion) {
        Surface(shape = RoundedCornerShape(12.dp), color = color) {
            Text(text = mensaje.texto, modifier = Modifier.padding(12.dp))
        }
    }
}