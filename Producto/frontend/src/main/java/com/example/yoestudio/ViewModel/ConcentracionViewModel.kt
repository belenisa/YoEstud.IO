package com.example.yoestudio.ViewModel

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import com.example.yoestudio.concentracion.ConcentracionService
import com.example.yoestudio.concentracion.PermisoUsageStatsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConcentracionViewModel : ViewModel() {

    // Lista de paquetes de apps seleccionadas para "monitorear"
    private val _appsSeleccionadas = MutableStateFlow<Set<String>>(emptySet())
    val appsSeleccionadas: StateFlow<Set<String>> = _appsSeleccionadas.asStateFlow()

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

    fun iniciarEnfoque(context: Context, minutos: Long) {
        val millis = minutos * 60 * 1000
        _estaActivo.value = true
        _tiempoRestante.value = millis

        // Iniciar Servicio de Reenganche Oficial
        val intent = Intent(context, ConcentracionService::class.java).apply {
            putStringArrayListExtra("apps_bloqueadas", ArrayList(_appsSeleccionadas.value.toList()))
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

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
        context.stopService(Intent(context, ConcentracionService::class.java))
    }

    fun cancelarEnfoque(context: Context) {
        finalizarModo(context)
    }

    fun tienePermisos(context: Context): Boolean {
        return PermisoUsageStatsHelper.tienePermiso(context)
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
}
