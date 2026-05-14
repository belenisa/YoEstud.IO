package com.example.yoestudio.Service


import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.yoestudio.utils.ConfiguracionBloqueo
import com.example.yoestudio.utils.abrirPantallaBloqueo
import com.example.yoestudio.utils.getAppActual


class MonitoreoBloqueo : Service() {

    private var ultimaApp: String? = null
    private var isRunning = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            isRunning = true
            iniciarMonitoreo()
        }
        return START_STICKY
    }

    private fun iniciarMonitoreo() {
        Thread {
            while (isRunning) {
                val appActual = getAppActual(this)
                val ahora = System.currentTimeMillis()

                val estaEnPeriodoDeGracia = ConfiguracionBloqueo.appDesbloqueada == appActual &&
                        ahora < ConfiguracionBloqueo.tiempoFin

                //Bloqueo
                if (ConfiguracionBloqueo.appsBloqueadas.contains(appActual)) {

                    if (appActual != ultimaApp && !estaEnPeriodoDeGracia) {
                        abrirPantallaBloqueo(this, appActual)
                    }
                    else if (appActual == ConfiguracionBloqueo.appDesbloqueada && ahora >= ConfiguracionBloqueo.tiempoFin) {
                        ConfiguracionBloqueo.appDesbloqueada = null
                        abrirPantallaBloqueo(this, appActual)
                    }
                }

                ultimaApp = appActual

                try {
                    Thread.sleep(1500)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }.start()
    }

    override fun onDestroy() {
        isRunning = false
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
