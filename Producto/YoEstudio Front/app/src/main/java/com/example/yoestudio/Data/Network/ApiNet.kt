package com.example.yoestudio.Data.Network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.jvm.java

object  ApiNet {

    private const val BASE_URL = "https://yoestud-io.onrender.com/"
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logging)
        // La IA tarda en responder, aumentamos el tiempo de espera a 1 minuto
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .build()
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttp)
        // IMPORTANTE: Scalars debe ir ANTES que Gson para manejar Strings puros
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Interfaces globales
    val asistenteIA: AsistenteIA = retrofit.create(AsistenteIA::class.java)
    val usuarioService: UsuariosService = retrofit.create(UsuariosService::class.java)
    val rolService: RolService = retrofit.create(RolService::class.java)


}