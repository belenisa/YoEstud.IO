package com.example.yoestudio.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoestudio.Data.Network.ApiNet
import com.example.yoestudio.Data.Network.ComentariosDTOs
import kotlinx.coroutines.launch

class ComentariosViewModel : ViewModel() {

    var comentarios by mutableStateOf<List<ComentariosDTOs>>(emptyList())

    fun cargarComentarios(idPublicacion: String) {

        viewModelScope.launch {

            try {
                comentarios =
                    ApiNet.apiComentarios
                        .obtenerComentarios(idPublicacion)

            } catch (e: Exception) {
                Log.e("API", "Error al cargar comentarios", e)
            }
        }
    }

    fun agregarComentario(
        idPublicacion: String,
        idUsuario: Long,
        nombreUsuario: String,
        descripcion: String
    ) {

        viewModelScope.launch {

            try {

                val comentario = ComentariosDTOs(
                    id = null,
                    idPublicacion = idPublicacion,
                    idUsuario = idUsuario,
                    nombreUsuario = nombreUsuario,
                    descripcion = descripcion
                )

                ApiNet.apiComentarios.crearComentario(comentario)

                cargarComentarios(idPublicacion)

            } catch (e: Exception) {
                Log.e("API", "Error al guardar comentario", e)
            }
        }
    }

    fun eliminarComentario(comentario: ComentariosDTOs) {

        viewModelScope.launch {

            try {

                comentario.id?.let { id ->

                    ApiNet.apiComentarios.eliminarComentario(id)

                    comentarios = comentarios - comentario
                }

            } catch (e: Exception) {

                Log.e(
                    "API",
                    "Error al eliminar comentario",
                    e
                )
            }
        }
    }
}
