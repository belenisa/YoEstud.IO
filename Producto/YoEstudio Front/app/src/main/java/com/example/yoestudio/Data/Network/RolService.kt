package com.example.yoestudio.Data.Network

import com.example.yoestudio.Data.Modelo.RolModelo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface RolService {

    @GET("api/rolusuarios")
    suspend fun listar(): Response<List<RolModelo>>

    @GET("api/rolusuarios/{id}")
    suspend fun obtener(@Path("id") id: Long): Response<RolModelo>

    @PUT("api/rolusuarios/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body datos: RolModelo): Response<RolModelo>
}