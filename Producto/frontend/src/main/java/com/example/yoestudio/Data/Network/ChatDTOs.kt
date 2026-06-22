package com.example.yoestudio.Data.Network

data class ChatRequest(
    val usuarioId: Long,
    val sesionId: String,
    val mensaje: String,
    val nombreArchivo: String? = null,
    val archivoBase64: String? = null,
    val nombreUsuario: String? = null
)

data class ChatResponse(
    val respuesta: String,
    val es_archivo: Boolean,
    val archivo_id: String? = null,
    val nombre_archivo: String? = null,
    val es_limpiar: Boolean = false
)

data class HistorialSesion(
    val id: String? = null,
    val usuarioId: Long,
    val sesionId: String,
    val mensajes: List<HistorialMensaje>,
    val fechaInicio: String? = null
)

data class HistorialMensaje(
    val rol: String,
    val contenido: String,
    val fecha: String? = null
)
