package com.example.yoestudio.Service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.example.yoestudio.ui.Pantallas.BloqueoActivity
import com.example.yoestudio.utils.ConfiguracionBloqueo

class MonitoreoBloqueo : AccessibilityService() {

    private var appActual: String? = null
    private var ultimoBloqueo = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (
            event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            event?.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        ) return

        val paquete = event.packageName?.toString() ?: return

        if (paquete == packageName) return
        if (paquete.contains("inputmethod")) return
        if (paquete.contains("launcher")) return

        val ahora = System.currentTimeMillis()

        val tiempo = ConfiguracionBloqueo.tiemposPorApp[paquete]
            ?: ConfiguracionBloqueo.tiempoDefault

        // ✅ iniciar contador solo al cambiar de app
        if (paquete != appActual) {
            appActual = paquete
            ConfiguracionBloqueo.tiempoFin = ahora + (tiempo * 1000)
        }

        val tiempoTerminado = ahora >= ConfiguracionBloqueo.tiempoFin

        if (
            ConfiguracionBloqueo.appsBloqueadas.contains(paquete) &&
            tiempoTerminado
        ) {

            if (ahora - ultimoBloqueo < 1500) return
            ultimoBloqueo = ahora

            val intent = Intent(this, BloqueoActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("app", paquete)
            intent.putExtra("tiempo", tiempo)

            startActivity(intent)
        }
    }

    override fun onInterrupt() {}
}
