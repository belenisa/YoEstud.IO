package com.example.yoestudio.Data.Network

data class PreguntasDTOs(
    val id: Long?,
    val usuario: Usuario?,
    val pregunta: String,
    val respuesta: String
)


data class Usuario(
    val id: Long
)



