package com.example.yoestudio.Data.Modelo

import com.google.gson.annotations.SerializedName

enum class TipoUsuario {
    FREE, PREMIUM
}

data class UsuarioModelo(
    val id: Long? = null,
    val nombre: String? = null,
    val email: String? = null,
    val password: String? = null,
    val tipo: TipoUsuario,
    @SerializedName("rolId")
    val rolid: Long
)