package com.example.yoestudio.utils


object ConfiguracionBloqueo {
    var appDesbloqueada: String? = null
    var tiempoFin: Long = 0

    var tiemposPorApp: Map<String, Int> = emptyMap()
    var appsBloqueadas: List<String> = emptyList()

    var tiempoDefault: Int = 10

    fun guardarTiempoPorApp(packageName: String, tiempo: Int) {
        tiemposPorApp = tiemposPorApp + (packageName to tiempo)
    }
}