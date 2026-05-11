package com.example.yoestudio.Service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.yoestudio.utils.ConfiguracionBloqueo
import com.example.yoestudio.utils.abrirPantallaBloqueo
import com.example.yoestudio.utils.getAppActual


class MonitoreoBloqueo : Service() {

    private var ultimaApp: String? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Thread {
            while (true) {

                val appActual = getAppActual(this)
                val ahora = System.currentTimeMillis()

                val desbloqueada = (
                        ConfiguracionBloqueo.appDesbloqueada == appActual &&
                                ahora < ConfiguracionBloqueo.tiempoFin
                        )

                // detecta cambio de app
                if (appActual != ultimaApp) {
                    ultimaApp = appActual

                    if (appActual in ConfiguracionBloqueo.appsBloqueadas && !desbloqueada) {
                        abrirPantallaBloqueo(this, appActual)
                    }
                }

                // si se acabó el tiempo → bloquea otra vez
                if (
                    ConfiguracionBloqueo.appDesbloqueada == appActual &&
                    ahora >= ConfiguracionBloqueo.tiempoFin
                ) {
                    abrirPantallaBloqueo(this, appActual)
                }

                Thread.sleep(1500)
            }
        }.start()

        return START_STICKY
    }

    override fun onBind(intent: Intent?) = null
}
