package com.example.yoestudio.Data.Network

data class ChatRequest(
    val usuarioId: Long,
    val sesionId: String,
    val mensaje: String
)

data class ChatResponse(
    val respuesta: String,
    val es_archivo: Boolean,
    val archivo_id: String? = null,
    val nombre_archivo: String? = null
)
