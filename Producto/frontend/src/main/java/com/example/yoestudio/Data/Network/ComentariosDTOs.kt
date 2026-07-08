package com.example.yoestudio.Data.Network

data class ComentariosDTOs(

    val id: String? = null,
    val idPublicacion: String,
    val idUsuario: Long,
    val nombreUsuario: String,
    val descripcion: String,
    val fechaComentario: String? = null

)
