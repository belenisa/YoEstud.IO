package com.example.yoestudio.Data.Repository

import com.example.yoestudio.Data.Network.ApiNet
import com.example.yoestudio.Data.Network.ChatRequest
import com.example.yoestudio.Domain.Model.Mensaje
import java.util.UUID

class AsistenteRepository {

    private val api = ApiNet.asistenteIA
    private val sesionId = UUID.randomUUID().toString()

    suspend fun enviarMensajeIA(prompt: String): Result<Mensaje> {
        return try {
            val request = ChatRequest(usuarioId = 1L, sesionId = sesionId, mensaje = prompt)
            val response = api.enviarMensaje(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body == null) {
                    Result.failure(Exception("La IA no devolvió ninguna respuesta."))
                } else {
                    Result.success(Mensaje(
                        texto = body.respuesta,
                        esDelUsuario = false,
                        esArchivo = body.es_archivo,
                        archivoId = body.archivo_id,
                        nombreArchivo = body.nombre_archivo
                    ))
                }
            } else {
                Result.failure(Exception("Error del servidor (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun descargarArchivo(archivoId: String): Result<ByteArray> {
        return try {
            val response = api.descargarArchivo(archivoId)
            if (response.isSuccessful) {
                val bytes = response.body()?.bytes()
                if (bytes != null) {
                    Result.success(bytes)
                } else {
                    Result.failure(Exception("Archivo vacío"))
                }
            } else {
                Result.failure(Exception("Error al descargar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
