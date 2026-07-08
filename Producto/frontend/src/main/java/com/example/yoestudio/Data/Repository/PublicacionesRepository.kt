package com.example.yoestudio.Data.Repository

import com.example.yoestudio.Data.Network.PublicacionesDTOs
import com.example.yoestudio.Data.Service.PublicaconesService

class PublicacionesRepository
    (
    private val service: PublicaconesService
) {

    suspend fun obtenerPublicaciones() =
        service.obtenerPublicaciones()

    suspend fun obtenerPublicacion(id: String) =
        service.obtenerPublicacion(id)

    suspend fun crearPublicacion(
        publicacion: PublicacionesDTOs
    ) = service.crearPublicacion(publicacion)


    suspend fun darLike(
        id: String, usuarioId: Long) =
        service.darLike(id, usuarioId
    )

    suspend fun actualizarPublicacion(
        id: String,
        publicacion: PublicacionesDTOs
    ) = service.actualizarPublicacion(id, publicacion)

    suspend fun obtenerHoraServidor() =
        service.obtenerHoraServidor()
}
