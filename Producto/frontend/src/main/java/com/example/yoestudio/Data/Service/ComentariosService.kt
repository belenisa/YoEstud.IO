package com.example.yoestudio.Data.Service

import com.example.yoestudio.Data.Network.ComentariosDTOs
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ComentariosService {

    @POST("api/comentarios")
    suspend fun crearComentario(
        @Body comentario: ComentariosDTOs
    ): ComentariosDTOs

    @GET("api/comentarios/publicacion/{idPublicacion}")
    suspend fun obtenerComentarios(
        @Path("idPublicacion") idPublicacion: String
    ): List<ComentariosDTOs>

    @GET("api/comentarios/usuario/{idUsuario}")
    suspend fun obtenerPorUsuario(
        @Path("idUsuario") idUsuario: Long
    ): List<ComentariosDTOs>

    @DELETE("api/comentarios/{id}")
    suspend fun eliminarComentario(
        @Path("id") id: String
    )

}