package com.example.yoestudio.concentracion

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore

val Context.concentracionDataStore by preferencesDataStore("concentracion_prefs")

object ConcentracionKeys {
    val APPS_BLOQUEADAS = stringSetPreferencesKey("apps_bloqueadas")
    val DURACION_MINUTOS = intPreferencesKey("duracion_minutos")
    val MODO_ACTIVO = booleanPreferencesKey("modo_activo")
    val INICIO_TIMESTAMP = longPreferencesKey("inicio_timestamp")
}
