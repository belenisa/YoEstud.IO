package com.example.yoestudio.Data.Repository

import com.example.yoestudio.Data.Network.ApiNet
import com.example.yoestudio.Data.Network.ChatRequest
import com.example.yoestudio.Domain.Model.Mensaje
import java.util.UUID

class AsistenteRepository {

    private val api = ApiNet.asistenteIA
    var usuarioId: Long = 1L
    var sesionId: String = UUID.randomUUID().toString()

    suspend fun enviarMensajeIA(prompt: String, fileName: String? = null, fileBase64: String? = null, nombreUsuario: String? = null): Result<Mensaje> {
        return try {
            val request = ChatRequest(
                usuarioId = usuarioId,
                sesionId = sesionId,
                mensaje = prompt,
                nombreArchivo = fileName,
                archivoBase64 = fileBase64,
                nombreUsuario = nombreUsuario
            )
            val response = api.enviarMensaje(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body == null) {
                    Result.failure(Exception("La IA no devolvió ninguna respuesta."))
                } else {
                    val esLimpiar = body.es_limpiar
                    Result.success(Mensaje(
                        texto = body.respuesta,
                        esDelUsuario = false,
                        esArchivo = body.es_archivo,
                        archivoId = if (esLimpiar) "LIMPIAR" else body.archivo_id,
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

    suspend fun obtenerHistorial(): Result<List<Mensaje>> {
        return try {
            val response = api.obtenerHistorial(usuarioId)
            if (response.isSuccessful) {
                val sesiones = response.body() ?: emptyList()
                val sesion = sesiones.find { it.sesionId == sesionId }
                val mensajes = sesion?.mensajes?.map { msg ->
                    Mensaje(
                        texto = msg.contenido,
                        esDelUsuario = msg.rol == "user",
                        esArchivo = false
                    )
                } ?: emptyList()
                Result.success(mensajes)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Result.success(emptyList())
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
