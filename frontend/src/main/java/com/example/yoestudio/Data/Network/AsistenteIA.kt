package com.example.yoestudio.Data.Network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface AsistenteIA {

    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("api/auth/registro")
    suspend fun registro(@Body request: RegistroRequest): Response<ResponseBody>

    @POST("api/chat/mensaje")
    suspend fun enviarMensaje(@Body request: ChatRequest): Response<ChatResponse>

    @GET("api/archivos/{id}")
    @Streaming
    suspend fun descargarArchivo(@Path("id") id: String): Response<ResponseBody>
}
