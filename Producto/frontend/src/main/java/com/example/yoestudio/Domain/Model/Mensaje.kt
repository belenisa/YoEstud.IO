package com.example.yoestudio.Domain.Model

data class Mensaje(
    val texto: String,
    val esDelUsuario: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val esArchivo: Boolean = false,
    val nombreArchivo: String? = null,
    val archivoId: String? = null
)
