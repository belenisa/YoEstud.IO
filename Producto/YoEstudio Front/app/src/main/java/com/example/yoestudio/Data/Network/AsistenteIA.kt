package com.example.yoestudio.Data.Network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AsistenteIA {

    @Multipart
    @POST("api/ai/analizar-archivo")
    suspend fun enviarArchivoYTexto(
        @Part("mensaje") mensaje: RequestBody,
        @Part archivo: MultipartBody.Part
    ): Response<String>

    // Puedes mantener tus otros métodos aquí abajo
    @POST("api/ai/chat")
    suspend fun enviarMensaje(@Body mensaje: String): Response<String>

    // Se conecta con @PostMapping("/estudiar")
    @POST("api/ai/estudiar")
    suspend fun generarCuestionario(@Body texto: String): Response<String>
}