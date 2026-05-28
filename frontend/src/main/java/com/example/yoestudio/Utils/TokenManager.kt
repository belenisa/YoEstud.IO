package com.example.yoestudio.Utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class TokenManager(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_ID = stringPreferencesKey("user_id")
        private val SESION_ID = stringPreferencesKey("sesion_id")
        private val DARK_MODE_KEY = stringPreferencesKey("dark_mode")
    }

    suspend fun saveToken(token: String, name: String, userId: Long) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_NAME] = name
            preferences[USER_ID] = userId.toString()
        }
    }

    val getToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    val getName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME]
    }

    val getUserId: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID]?.toLongOrNull()
    }

    suspend fun saveSesionId(sesionId: String) {
        context.dataStore.edit { preferences ->
            preferences[SESION_ID] = sesionId
        }
    }

    val getSesionId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[SESION_ID]
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] == "true"
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled.toString()
        }
    }

    suspend fun clearData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
