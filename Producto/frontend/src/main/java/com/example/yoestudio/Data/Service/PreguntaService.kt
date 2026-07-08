package com.example.yoestudio.Data.Service

import com.example.yoestudio.Data.Network.PreguntasDTOs
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PreguntaService {

    @GET("api/preguntas")
    suspend fun obtenerPreguntas(): List<PreguntasDTOs>

    @POST("api/preguntas")
    suspend fun crearPregunta(@Body pregunta: PreguntasDTOs): PreguntasDTOs

    @DELETE("api/preguntas/{id}")
    suspend fun eliminarPregunta(@Path("id") id: Long)

    @GET("api/preguntas/usuario/{usuarioId}")
    suspend fun obtenerPorUsuario(
        @Path("usuarioId") usuarioId: Long
    ): List<PreguntasDTOs>

}