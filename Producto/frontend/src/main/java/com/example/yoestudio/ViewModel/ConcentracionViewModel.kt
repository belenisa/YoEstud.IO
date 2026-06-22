package com.example.yoestudio.ViewModel

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.example.yoestudio.concentracion.ConcentracionBloqueo
import com.example.yoestudio.concentracion.ConcentracionService
import com.example.yoestudio.concentracion.PermisoUsageStatsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConcentracionViewModel : ViewModel() {

     //Lista de paquetes de apps seleccionadas para "monitorear"
    private val _appsSeleccionadas = MutableStateFlow<Set<String>>(emptySet())
    val appsSeleccionadas: StateFlow<Set<String>> = _appsSeleccionadas.asStateFlow()


    private val _segundosApps = mutableStateMapOf<String, String>()
    val segundosApps: Map<String, String> = _segundosApps


    // Estado del temporizador
    private val _tiempoRestante = MutableStateFlow(0L) // en milisegundos
    val tiempoRestante: StateFlow<Long> = _tiempoRestante.asStateFlow()

    private val _estaActivo = MutableStateFlow(false)
    val estaActivo: StateFlow<Boolean> = _estaActivo.asStateFlow()

    private var timer: CountDownTimer? = null

    fun toggleApp(packageName: String) {
        val current = _appsSeleccionadas.value
        _appsSeleccionadas.value = if (current.contains(packageName)) {
            current - packageName
        } else {
            current + packageName
        }
    }

    fun tieneAppsSeleccionadas(): Boolean = _appsSeleccionadas.value.isNotEmpty()


    fun actualizarSegundos(packageName: String, segundos: String) {
        if (segundos.all { it.isDigit() }) {
            _segundosApps[packageName] = segundos

            val tiempoInt = segundos.toIntOrNull() ?: 0

            ConcentracionBloqueo.guardarTiempoPorApp(packageName, tiempoInt)
        }
    }

    fun iniciarEnfoque(context: Context, minutos: Long) {

        if (_appsSeleccionadas.value.isEmpty()) return

        val millis = minutos * 60 * 1000
        val ahora = System.currentTimeMillis()
        ConcentracionBloqueo.esModoConcentracion = true

        ConcentracionBloqueo.appsBloqueadas = _appsSeleccionadas.value.toList()
        ConcentracionBloqueo.tiemposPorApp =
            _segundosApps.mapValues { it.value.toIntOrNull() ?: 10 }
        ConcentracionBloqueo.tiempoModoConcentracion = ahora + millis

        _estaActivo.value = true
        _tiempoRestante.value = millis

        timer?.cancel()
        timer = object : CountDownTimer(millis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _tiempoRestante.value = millisUntilFinished
            }

            override fun onFinish() {
                finalizarModo(context)
            }
        }.start()
    }

    fun finalizarModo(context: Context) {
        timer?.cancel()
        _estaActivo.value = false
        _tiempoRestante.value = 0

        ConcentracionBloqueo.esModoConcentracion = false
        ConcentracionBloqueo.appsBloqueadas = emptyList()
        ConcentracionBloqueo.tiempoFin = Long.MAX_VALUE
        ConcentracionBloqueo.tiempoModoConcentracion = 0

    }

    fun cancelarEnfoque(context: Context) {
        finalizarModo(context)
    }

    fun tienePermisos(context: Context): Boolean {
        return PermisoUsageStatsHelper.estaAccessibilityActiva(context)
    }

    fun solicitarPermisos(context: Context) {
        PermisoUsageStatsHelper.abrirAjustesPermiso(context)
    }

    fun formatearTiempo(millis: Long): String {
        val minutos = (millis / 1000) / 60
        val segundos = (millis / 1000) % 60
        return "%02d:%02d".format(minutos, segundos)
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
    fun guardarConfiguracionCompleta() {
        ConcentracionBloqueo.appsBloqueadas = _appsSeleccionadas.value.toList()

        val mapaTiemposInt = _segundosApps.mapValues { entry ->
            entry.value.toIntOrNull() ?: 10
        }

        ConcentracionBloqueo.esModoConcentracion = true
        ConcentracionBloqueo.tiemposPorApp = mapaTiemposInt
        ConcentracionBloqueo.tiempoFin = 0
    }
}
