package com.example.yoestudio.concentracion

import android.app.*
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.yoestudio.MainActivity
import android.util.Log

class ConcentracionService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private val INTERVALO_MS = 1000L
    private var appsBloqueadas = setOf<String>()
    private val channelId = "concentracion_official_channel"

    // Lista blanca: apps del sistema que nunca deben disparar el reenganche
    private val listaBlanca = setOf(
        "com.android.launcher",
        "com.android.launcher2",
        "com.android.launcher3",
        "com.google.android.apps.nexuslauncher",
        "com.miui.home",                        // Xiaomi/POCO
        "com.huawei.android.launcher",          // Huawei
        "com.sec.android.app.launcher",         // Samsung
        "com.android.systemui",
        "com.google.android.inputmethod.latin",
        "com.android.inputmethod.latin"
    )

    private val monitorRunnable = object : Runnable {
        override fun run() {
            val appActual = obtenerAppEnPrimerPlano()
            // Log.d("ConcentracionService", "App actual: $appActual")

            // Si la app actual no es YOESTUDIO, no está en la lista blanca y está en las bloqueadas -> regresar
            // Nota: En la nueva versión del prompt, se regresa si NO es YOESTUDIO y NO es de sistema.
            // Pero mantendremos el chequeo contra appsBloqueadas para ser precisos con lo que eligió el usuario.
            if (appActual != null && 
                appActual != packageName && 
                appActual !in listaBlanca &&
                appsBloqueadas.contains(appActual)) {

                // COMENTARIO PARA LA TERMINAL (Logcat)
                Log.w("YOESTUDIO_TERMINAL", "🚨 DISTRACCIÓN DETECTADA: El usuario abrió [$appActual]")
                Log.i("ConcentracionService", ">>> Forzando regreso a YOESTUD.IO")
                
                val intent = Intent(this@ConcentracionService, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    putExtra("desde_concentracion", true)
                }
                startActivity(intent)
            }

            handler.postDelayed(this, INTERVALO_MS)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val apps = intent?.getStringArrayListExtra("apps_bloqueadas")
        if (apps != null) {
            appsBloqueadas = apps.toSet()
        }

        crearCanalNotificacion()
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Modo Concentración activo 🎯")
            .setContentText("YOESTUD.IO te mantiene enfocado")
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
        handler.removeCallbacks(monitorRunnable)
        handler.post(monitorRunnable)

        return START_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacks(monitorRunnable)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun obtenerAppEnPrimerPlano(): String? {
        val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val ahora = System.currentTimeMillis()
        val stats = usm.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            ahora - 5000,
            ahora
        )
        return stats
            ?.filter { it.lastTimeUsed > 0 }
            ?.maxByOrNull { it.lastTimeUsed }
            ?.packageName
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                channelId,
                "Modo Concentración",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }
}
