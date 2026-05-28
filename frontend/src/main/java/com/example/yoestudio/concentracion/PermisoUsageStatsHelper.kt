package com.example.yoestudio.concentracion

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object PermisoUsageStatsHelper {

    // Verifica si el permiso ya fue otorgado
    fun tienePermiso(context: Context): Boolean {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val ahora = System.currentTimeMillis()
        val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, ahora - 1000 * 60, ahora)
        return stats != null && stats.isNotEmpty()
    }

    // Abre la pantalla de Ajustes del sistema donde el usuario otorga el permiso
    fun abrirAjustesPermiso(context: Context) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback si el intent con data falla en algunos dispositivos
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}
