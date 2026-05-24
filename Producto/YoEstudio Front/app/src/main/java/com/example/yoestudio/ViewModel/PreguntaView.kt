package com.example.yoestudio.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoestudio.Data.Modelo.PreguntaModelo
import com.example.yoestudio.Data.Network.ApiNet.preguntaService
import kotlinx.coroutines.launch

class PreguntaView: ViewModel() {

    var preguntas by mutableStateOf<List<PreguntaModelo>>(emptyList())

    fun cargarPreguntas() {
        viewModelScope.launch {
            try {
                preguntas = preguntaService.obtenerPreguntas()
            } catch (e: Exception) {
                println("Error cargando preguntas: ${e.message}")
                }
        }
    }
}