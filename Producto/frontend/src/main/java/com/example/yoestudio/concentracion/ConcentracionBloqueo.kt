package com.example.yoestudio.concentracion

object ConcentracionBloqueo {

    var tiempoModoConcentracion: Long = 0
    var tiempoFin: Long = 0

    var tiemposPorApp: Map<String, Int> = emptyMap()
    var appsBloqueadas: List<String> = emptyList()

    var tiempoDefault: Int = 10
    var esModoConcentracion: Boolean = false
    var ultimaPreguntaIndex: Int = -1

    fun guardarTiempoPorApp(packageName: String, tiempo: Int) {
        tiemposPorApp = tiemposPorApp + (packageName to tiempo)
    }
}