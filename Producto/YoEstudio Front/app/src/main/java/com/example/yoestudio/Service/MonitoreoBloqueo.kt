package com.example.yoestudio.Service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.example.yoestudio.ui.Pantallas.BloqueoActivity
import com.example.yoestudio.utils.ConfiguracionBloqueo

class MonitoreoBloqueo : AccessibilityService() {

    private var appActual: String = ""
    private var ultimoBloqueo = 0L

    private val handler = android.os.Handler()
    private lateinit var runnable: Runnable

    private fun checkBloqueo() {

        val paquete = appActual.takeIf { it.isNotEmpty() } ?: return

        val ahora = System.currentTimeMillis()

        val tiempo = ConfiguracionBloqueo.tiemposPorApp[paquete]
            ?: ConfiguracionBloqueo.tiempoDefault

        val bloquear = ahora >= ConfiguracionBloqueo.tiempoFin

        if (ConfiguracionBloqueo.appsBloqueadas.contains(paquete) && bloquear
        ) {

            if (ahora - ultimoBloqueo < 1500) return
            ultimoBloqueo = ahora

            val intent = Intent(this, BloqueoActivity::class.java)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
            intent.putExtra("app", paquete)
            intent.putExtra("tiempo", tiempo)

            startActivity(intent)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        appActual = ""
        ConfiguracionBloqueo.tiempoFin = 0

        runnable = object : Runnable {
            override fun run() {

                checkBloqueo()

                handler.postDelayed(this, 1000) // cada 1 segundo
            }
        }

        handler.post(runnable)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val paquete = event.packageName?.toString() ?: return

        //  ignora apps no relevantes
        if (paquete == packageName) return
        if (paquete.contains("inputmethod")) return

        // DETECTA launcher (pantalla inicio)
        if (paquete.contains("launcher")) {
            appActual = "" // 🔥 AQUÍ ESTABA EL BUG
            return
        }

        val ahora = System.currentTimeMillis()

        val tiempo = ConfiguracionBloqueo.tiemposPorApp[paquete]
            ?: ConfiguracionBloqueo.tiempoDefault

        // cambio de app
        if (paquete != appActual) {
            appActual = paquete

            // 🔥 bloquear inmediatamente al entrar
            ConfiguracionBloqueo.tiempoFin = ahora
        }
    }

    override fun onInterrupt() {}
}