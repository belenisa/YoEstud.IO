package com.example.yoestudio.Data.Network

import com.example.yoestudio.Data.Modelo.CredencialesModelo
import retrofit2.Response
import retrofit2.http.GET
import com.example.yoestudio.Data.Modelo.UsuarioModelo
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface UsuariosService {
    @GET("api/usuarios")
    suspend fun listar(): Response<List<UsuarioModelo>>

    @GET("api/usuarios/{id}")
    suspend fun obtener(@Path("id") id: Long): Response<UsuarioModelo>

    @POST("api/usuarios")
    suspend fun crear(@Body nuevo: UsuarioModelo): Response<UsuarioModelo>


    @POST("api/usuarios/login")
    suspend fun login(@Body credenciales: CredencialesModelo): Response<UsuarioModelo>

    @PUT("api/usuarios/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body datos: UsuarioModelo): Response<UsuarioModelo>

    @DELETE("api/usuarios/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>


}