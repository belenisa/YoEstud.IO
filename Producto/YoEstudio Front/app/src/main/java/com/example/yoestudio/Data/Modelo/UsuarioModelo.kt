package com.example.yoestudio.Data.Modelo

enum class TipoUsuario {
    Free, Premium
}

data class UsuarioModelo(
    val id: Long? = null,
    val nombre: String? = null,
    val email: String? = null,
    val password: String? = null,
    val TipoUsuario: TipoUsuario

    //val rol: Rol? = null
)