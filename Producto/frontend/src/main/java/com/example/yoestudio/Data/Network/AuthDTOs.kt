package com.example.yoestudio.Data.Network

data class AuthRequest(
    val username: String,
    val password: String
)

data class RegistroRequest(
    val nombre: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val username: String,
    val usuarioId: Long
)

data class RecuperarRequest(
    val email: String
)

data class VerificarCodigoRequest(
    val email: String,
    val codigo: String,
    val nuevaPassword: String
)
