package com.example.yoestudio.utils

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import com.example.yoestudio.ui.Pantallas.BloqueoActivity


fun getAppActual (context: Context): String {
    val usage =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    val time = System.currentTimeMillis()

    val stats = usage.queryUsageStats(
        UsageStatsManager.INTERVAL_DAILY,
        time - 10000,
        time
    )

    val reciente = stats.maxByOrNull { it.lastTimeUsed }

    return reciente?.packageName ?: ""
}


fun abrirPantallaBloqueo(context: Context, app: String) {
    val intent = Intent(context, BloqueoActivity::class.java)

    // Buscamos el tiempo configurado para ESTA app específico
    val tiempoConfigurado = ConfiguracionBloqueo.tiemposPorApp[app] ?: ConfiguracionBloqueo.tiempoDefault

    intent.putExtra("app", app)
    intent.putExtra("tiempo", tiempoConfigurado)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    //context.startActivity(intent)
}