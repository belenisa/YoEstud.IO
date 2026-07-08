package com.example.yoestudio.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoestudio.Data.Network.ApiNet
import com.example.yoestudio.Data.Network.PublicacionesDTOs
import com.example.yoestudio.Data.Repository.PublicacionesRepository
import com.example.yoestudio.Data.Service.PublicaconesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PublicacionesViewModel : ViewModel() {


    private val repository = PublicacionesRepository(
        ApiNet.retrofit.create(PublicaconesService::class.java)
    )

    private val _publicaciones =
        MutableStateFlow<List<PublicacionesDTOs>>(emptyList())

    val publicaciones: StateFlow<List<PublicacionesDTOs>> =
        _publicaciones


    private val _horaServidor = MutableStateFlow("")

    val horaServidor: StateFlow<String> = _horaServidor


    fun cargarPublicaciones() {
        viewModelScope.launch {
            try {

                _horaServidor.value =
                    repository.obtenerHoraServidor()

                _publicaciones.value =
                    repository.obtenerPublicaciones()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun crearPublicacion(publicacion: PublicacionesDTOs) {
        viewModelScope.launch {
            try {
                repository.crearPublicacion(publicacion)
                cargarPublicaciones()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun darLike(
        id: String,
        usuarioId: Long
    ) {
        viewModelScope.launch {
            try {
                repository.darLike(
                    id,
                    usuarioId
                )
                cargarPublicaciones()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun actualizarPublicacion(
        id: String,
        publicacion: PublicacionesDTOs
    ) {
        viewModelScope.launch {
            try {
                repository.actualizarPublicacion(
                    id,
                    publicacion
                )

                cargarPublicaciones()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}