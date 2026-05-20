package com.example.yoestudio.Data.Repository

import com.example.yoestudio.Data.Network.ApiNet
import com.example.yoestudio.Domain.Model.Mensaje
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AsistenteRepository {

    private val api = ApiNet.asistenteIA

    suspend fun enviarMensajeIA(prompt: String): Result<Mensaje> {
        return try {
            val requestBody = prompt.toRequestBody("text/plain".toMediaType())
            val response = api.enviarMensaje(requestBody)

            if (response.isSuccessful) {
                val texto = response.body() ?: ""
                if (texto.isBlank()) {
                    Result.failure(Exception("La IA no devolvió ninguna respuesta."))
                } else {
                    Result.success(Mensaje(texto = texto, esDelUsuario = false))
                }
            } else {
                Result.failure(Exception("Error del servidor (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun enviarArchivoIA(archivoPart: MultipartBody.Part, mensaje: RequestBody): Result<Mensaje> {
        return try {
            val response = api.enviarArchivoYTexto(mensaje, archivoPart)
            if (response.isSuccessful) {
                Result.success(Mensaje(texto = response.body() ?: "", esDelUsuario = false, esArchivo = true, nombreArchivo = "Analisis_Documento.pdf"))
            } else {
                Result.failure(Exception("Error al procesar archivo: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun descargarPdf(texto: String): Result<ByteArray> {
        return try {
            val requestBody = texto.toRequestBody("text/plain".toMediaType())
            val response = api.descargarPdf(requestBody)
            if (response.isSuccessful) {
                val bytes = response.body()?.bytes()
                if (bytes != null) {
                    Result.success(bytes)
                } else {
                    Result.failure(Exception("Cuerpo de respuesta vacío"))
                }
            } else {
                Result.failure(Exception("Error al descargar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
