package com.example.yoestudio.ViewModel


import android.content.pm.ApplicationInfo
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.yoestudio.utils.ConfiguracionBloqueo
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConfiguracionView: ViewModel() {
    private val _appsSeleccionadas = MutableStateFlow<List<String>>(emptyList())
    val appsSeleccionadas = _appsSeleccionadas.asStateFlow()

    private val _segundosApps = mutableStateMapOf<String, String>()
    val segundosApps: Map<String, String> = _segundosApps
    private val _darkMode = mutableStateOf(false)
    val darkMode: State<Boolean> = _darkMode


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

    fun RecordarAppSeleccionada(
        packageName: String,
        checked: Boolean,
        appsUsuario: List<ApplicationInfo>
    ) {

        val current = _appsSeleccionadas.value.toMutableSet()

        val esValida = appsUsuario.any { it.packageName == packageName }

        if (checked && esValida) {
            current.add(packageName)
        } else if (!checked) {
            current.remove(packageName)
        }

        _appsSeleccionadas.value = current.toList()
    }

    fun modoOscuro(value: Boolean) {
        _darkMode.value = value
    }


}
