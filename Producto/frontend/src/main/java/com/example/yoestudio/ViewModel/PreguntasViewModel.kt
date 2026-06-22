package com.example.yoestudio.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoestudio.Data.Network.ApiNet
import com.example.yoestudio.Data.Network.PreguntasDTOs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PreguntasViewModel: ViewModel() {
    var preguntasUsuario by mutableStateOf<List<PreguntasDTOs>>(emptyList())
    var misPreguntaSeleccionada by mutableStateOf<List<PreguntasDTOs>>(emptyList())

    private val _dificultad = MutableStateFlow("basico")
    val dificultad = _dificultad.asStateFlow()


    private val _modoMatematicas = MutableStateFlow(false)
    val modoMatematicas = _modoMatematicas.asStateFlow()



    fun cambiarDificultad(valor: String) {
        _dificultad.value = valor
    }


    fun usarMatematicas(valor: Boolean) {
        _modoMatematicas.value = valor
    }

    fun seleccionarPregunta(pregunta: PreguntasDTOs) {

        misPreguntaSeleccionada =
            if (misPreguntaSeleccionada.contains(pregunta)) {
                misPreguntaSeleccionada - pregunta
            } else {
                misPreguntaSeleccionada + pregunta
            }

    }


    fun agregarPregunta(pregunta: String, respuesta: String) {

        viewModelScope.launch {

            try {
                val nueva = PreguntasDTOs(
                    id = null,
                    pregunta = pregunta,
                    respuesta = respuesta
                )

                val creada = ApiNet.apiPreguntas.crearPregunta(nueva)

                preguntasUsuario = preguntasUsuario + creada

            } catch (e: Exception) {
                Log.e("API", "Error al guardar", e)
            }
        }
    }
    fun cargarPreguntas() {

        viewModelScope.launch {

            try {
                preguntasUsuario = ApiNet.apiPreguntas.obtenerPreguntas()
            } catch (e: Exception) {
                Log.e("API", "Error al cargar", e)
            }
        }
    }


    fun eliminarPregunta(pregunta: PreguntasDTOs) {

        viewModelScope.launch {

            try {
                pregunta.id?.let {
                    ApiNet.apiPreguntas.eliminarPregunta(it)
                    preguntasUsuario = preguntasUsuario - pregunta
                }

            } catch (e: Exception) {
                Log.e("API", "Error al eliminar", e)
            }
        }
    }

}