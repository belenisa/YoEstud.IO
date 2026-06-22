package com.example.yoestudio.concentracion

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.example.yoestudio.Pantallas.BloqueoActivity

class ConcentracionService : AccessibilityService() {

    private var appsBloqueadas = setOf<String>()
    private var appActual: String = ""
    private var ultimoBloqueo = 0L

    private val handler = android.os.Handler()
    private lateinit var runnable: Runnable
    private val channelId = "concentracion_official_channel"

    private val listaBlanca = setOf(
        "com.android.launcher",
        "com.android.launcher2",
        "com.android.launcher3",
        "com.google.android.apps.nexuslauncher",
        "com.miui.home",
        "com.huawei.android.launcher",
        "com.sec.android.app.launcher",
        "com.android.systemui",
        "com.google.android.inputmethod.latin",
        "com.android.inputmethod.latin"
    )

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val paquete = event.packageName?.toString() ?: return
        val clase = event.className?.toString() ?: ""

        //  IGNORA TU PROPIA APP
        if (paquete == packageName) return

        //  IGNORA LA PANTALLA DE BLOQUEO
        if (clase.contains("BloqueoActivity")) return

        if (paquete.contains("inputmethod")) return

        if (paquete.contains("launcher")) {
            appActual = ""
            return
        }

        val ahora = System.currentTimeMillis()
        val tiempo = ConcentracionBloqueo.tiemposPorApp[paquete]
            ?: ConcentracionBloqueo.tiempoDefault


        if (paquete != appActual) {
            appActual = paquete

            val tiempo = ConcentracionBloqueo.tiemposPorApp[paquete]
                ?: ConcentracionBloqueo.tiempoDefault

            ConcentracionBloqueo.tiempoFin = ahora
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        appActual = ""
        ConcentracionBloqueo.tiempoFin = 0

        runnable = object : Runnable {
            override fun run() {

                checkBloqueo()

                handler.postDelayed(this, 1000) // cada 1 segundo
            }
        }

        handler.post(runnable)
    }

    private fun checkBloqueo() {

        if (!ConcentracionBloqueo.esModoConcentracion) return

        val paquete = appActual.takeIf { it.isNotEmpty() } ?: return

        val ahora = System.currentTimeMillis()

        val tiempo = ConcentracionBloqueo.tiemposPorApp[paquete]
            ?: ConcentracionBloqueo.tiempoDefault

        val bloquear = ahora >= ConcentracionBloqueo.tiempoFin

        if (
            ConcentracionBloqueo.appsBloqueadas.contains(paquete) &&
            bloquear
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
    override fun onInterrupt() {
    }
}
