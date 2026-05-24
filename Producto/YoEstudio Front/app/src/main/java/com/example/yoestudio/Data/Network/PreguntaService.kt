package com.example.yoestudio.Data.Network

import com.example.yoestudio.Data.Modelo.PreguntaModelo
import retrofit2.http.GET

interface PreguntaService {

    @GET("preguntas")
    suspend fun obtenerPreguntas(): List<PreguntaModelo>

}