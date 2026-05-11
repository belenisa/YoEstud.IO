package com.example.yoestudio.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoestudio.Data.Network.AsistenteModelo
import com.example.yoestudio.Data.Network.ApiNet // Asegúrate de importar tu objeto ApiNet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class AsistenteView : ViewModel() {

    private val _mensajes = MutableStateFlow<List<AsistenteModelo>>(emptyList())
    val mensajes = _mensajes.asStateFlow()

    fun enviarA_IA(prompt: String) {

        if (prompt.isBlank()) return

        // ✅ 1. Mensaje del usuario
        _mensajes.value += AsistenteModelo(prompt, esDelUsuario = true)

        viewModelScope.launch {
            try {

                // ✅ 2. CONVERSIÓN CLAVE (ARREGLA TU ERROR)
                val requestBody = prompt
                    .toRequestBody("text/plain".toMediaType())

                val response = ApiNet.asistenteIA.enviarMensaje(requestBody)

                if (response.isSuccessful) {

                    _mensajes.value += AsistenteModelo(
                        texto = response.body() ?: "Sin respuesta",
                        esDelUsuario = false
                    )

                } else {

                    _mensajes.value += AsistenteModelo(
                        texto = "Error del servidor: ${response.code()}",
                        esDelUsuario = false
                    )
                }

            } catch (e: Exception) {

                _mensajes.value += AsistenteModelo(
                    texto = "Error de conexión: ${e.localizedMessage}",
                    esDelUsuario = false
                )
            }
        }
    }
}