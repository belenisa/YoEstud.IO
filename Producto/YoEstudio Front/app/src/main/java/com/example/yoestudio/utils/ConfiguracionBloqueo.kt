package com.example.yoestudio.utils


object ConfiguracionBloqueo {

    var tiempoModoConcentracion: Long = 0
    var tiempoFin: Long = 0

    var tiemposPorApp: Map<String, Int> = emptyMap()
    var appsBloqueadas: List<String> = emptyList()

    var tiempoDefault: Int = 10

    fun guardarTiempoPorApp(packageName: String, tiempo: Int) {
        tiemposPorApp = tiemposPorApp + (packageName to tiempo)
    }

}