package com.example.yoestudio.ViewModel


import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.example.yoestudio.utils.ConfiguracionBloqueo
//import com.example.yoestudio.utils.ConfiguracionBloqueo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConfiguracionView: ViewModel() {
    private val _appsSeleccionadas = MutableStateFlow<List<String>>(emptyList())
    val appsSeleccionadas = _appsSeleccionadas.asStateFlow()

    private val _segundosApps = mutableStateMapOf<String, String>()
    val segundosApps: Map<String, String> = _segundosApps

    fun actualizarSegundos(packageName: String, segundos: String) {
        if (segundos.all { it.isDigit() }) {

            _segundosApps[packageName] = segundos

            val tiempoInt = segundos.toIntOrNull() ?: 0

            ConfiguracionBloqueo.guardarTiempoPorApp(packageName, tiempoInt)
        }
    }

    fun guardarConfiguracionCompleta() {
        ConfiguracionBloqueo.appsBloqueadas = _appsSeleccionadas.value.toList()

        val mapaTiemposInt = _segundosApps.mapValues { entry ->
            entry.value.toIntOrNull() ?: 10
        }
        ConfiguracionBloqueo.tiemposPorApp = mapaTiemposInt
    }

    fun RecordarAppSeleccionada(packageName: String, checked: Boolean) {

        val current = _appsSeleccionadas.value.toMutableList()

        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }

        _appsSeleccionadas.value = current
        //ConfiguracionBloqueo.appsBloqueadas = current
    }

}
