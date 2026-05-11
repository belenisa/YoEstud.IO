package com.example.yoestudio.ViewModel

import androidx.lifecycle.ViewModel
import com.example.yoestudio.utils.ConfiguracionBloqueo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConfiguracionView: ViewModel() {
    private val _appsSeleccionadas = MutableStateFlow<List<String>>(emptyList())
    val appsSeleccionadas = _appsSeleccionadas.asStateFlow()

    //Time
    private val _tiempoBloqueo = MutableStateFlow(10)
    val tiempoBloqueo = _tiempoBloqueo.asStateFlow()

    fun cambiarTiempo(segundos: Int) {
        _tiempoBloqueo.value = segundos
        ConfiguracionBloqueo.tiempoPantalla = segundos
    }

    fun RecordarAppSeleccionada(packageName: String, checked: Boolean) {

        val current = _appsSeleccionadas.value.toMutableList()

        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }

        _appsSeleccionadas.value = current
        ConfiguracionBloqueo.appsBloqueadas = current
    }

}