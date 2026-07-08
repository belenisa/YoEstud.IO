package com.example.yoestudio.Data.Service

import com.example.yoestudio.Data.Network.PublicacionesDTOs
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
interface PublicaconesService {

    @GET("api/publicaciones")
    suspend fun obtenerPublicaciones(): List<PublicacionesDTOs>

    @GET("api/publicaciones/{id}")
    suspend fun obtenerPublicacion(
        @Path("id") id: String
    ): PublicacionesDTOs

    @POST("api/publicaciones")
    suspend fun crearPublicacion(
        @Body publicacion: PublicacionesDTOs
    ): PublicacionesDTOs

    @PUT("api/publicaciones/{id}/like/{usuarioId}")
    suspend fun darLike(
        @Path("id") id: String,
        @Path("usuarioId") usuarioId: Long
    ): PublicacionesDTOs

    @PUT("api/publicaciones/{id}")
    suspend fun actualizarPublicacion(
        @Path("id") id: String,
        @Body publicacion: PublicacionesDTOs
    ): PublicacionesDTOs

    @GET("api/publicaciones/hora")
    suspend fun obtenerHoraServidor(): String


}