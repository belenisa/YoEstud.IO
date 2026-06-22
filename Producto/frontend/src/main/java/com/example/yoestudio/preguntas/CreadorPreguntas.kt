package com.example.yoestudio.preguntas

import androidx.compose.runtime.mutableStateOf

object CreadorPreguntas {

    fun generarBasica(): Pair<String, String> {
        val tipo = (1..4).random()

        return when (tipo) {
            1 -> {
                val a = (1..20).random()
                val b = (1..20).random()
                "$a + $b" to (a + b).toString()
            }

            2 -> {
                val a = (5..20).random()
                val b = (1..a).random()
                "$a - $b" to (a - b).toString()
            }

            3 -> {
                val a = (1..10).random()
                val b = (1..10).random()
                "$a × $b" to (a * b).toString()
            }

            else -> {
                val b = (1..10).random()
                val resultado = (1..10).random()
                val a = b * resultado
                "$a ÷ $b" to resultado.toString()
            }
        }
    }
}


object EstadoPreguntas {
    var usarMatematicas = mutableStateOf(false)
}

