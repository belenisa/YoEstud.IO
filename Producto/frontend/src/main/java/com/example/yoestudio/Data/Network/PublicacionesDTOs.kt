package com.example.yoestudio.Data.Network

data class PublicacionesDTOs(
    val id: String,
    val idUsuario: Long,
    val nombreUsuario: String,
    val carrera: String,
    val descripcion: String,
    val documento: String?,
    val hashtag: String?,
    val fechaPublicacion: String?,
    val likes: Int,
    val usuariosLikes: List<Long>?
)
