package com.example.yoestudio.ViewModel

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoestudio.Data.Repository.AsistenteRepository
import com.example.yoestudio.Domain.Model.Mensaje
import com.example.yoestudio.Utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class AsistenteView(application: Application) : AndroidViewModel(application) {

    private val repository = AsistenteRepository()
    private val tokenManager = TokenManager(application)

    private val _mensajes = MutableStateFlow<List<Mensaje>>(emptyList())
    val mensajes = _mensajes.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando = _cargando.asStateFlow()

    private val _cargandoHistorial = MutableStateFlow(true)
    val cargandoHistorial = _cargandoHistorial.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = tokenManager.getUserId.first()
            var sesId = tokenManager.getSesionId.first()
            if (sesId == null) {
                sesId = UUID.randomUUID().toString()
                tokenManager.saveSesionId(sesId)
            }
            if (userId != null) {
                repository.usuarioId = userId
                repository.sesionId = sesId
                repository.obtenerHistorial().onSuccess { historial ->
                    _mensajes.value = historial
                }
            }
            _cargandoHistorial.value = false
        }
    }

    fun enviarA_IA(prompt: String, fileName: String? = null, fileBase64: String? = null, nombreUsuario: String? = null) {
        if (prompt.isBlank() && fileBase64 == null || _cargando.value) return

        val textoUsuario = if (fileName != null) "$prompt\n(Archivo adjunto: $fileName)" else prompt
        _mensajes.value += Mensaje(textoUsuario, esDelUsuario = true)
        _cargando.value = true

        viewModelScope.launch {
            repository.enviarMensajeIA(prompt, fileName, fileBase64, nombreUsuario)
                .onSuccess { mensajeRespuesta ->
                    if (mensajeRespuesta.archivoId == "LIMPIAR") {
                        _mensajes.value = listOf(mensajeRespuesta)
                    } else {
                        _mensajes.value += mensajeRespuesta
                    }
                }
                .onFailure { error ->
                    _mensajes.value += Mensaje(
                        texto = "Error: ${error.localizedMessage}",
                        esDelUsuario = false
                    )
                }
            _cargando.value = false
        }
    }

    fun descargarArchivo(context: Context, mensaje: Mensaje) {
        val id = mensaje.archivoId ?: return
        viewModelScope.launch {
            repository.descargarArchivo(id)
                .onSuccess { bytes ->
                    try {
                        val fileName = mensaje.nombreArchivo ?: "Prueba_${System.currentTimeMillis()}.pdf"
                        var success = false

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val contentValues = ContentValues().apply {
                                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                            }

                            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                            uri?.let {
                                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                                    outputStream.write(bytes)
                                    success = true
                                }
                            }
                        } else {
                            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            if (!downloadsDir.exists()) downloadsDir.mkdirs()
                            val file = File(downloadsDir, fileName)
                            file.writeBytes(bytes)
                            success = true
                        }
                        
                        if (success) {
                            android.widget.Toast.makeText(context, "Archivo guardado en Descargas: $fileName", android.widget.Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        android.widget.Toast.makeText(context, "Error al guardar: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
                .onFailure { error ->
                    android.widget.Toast.makeText(context, "Error al descargar: ${error.localizedMessage}", android.widget.Toast.LENGTH_SHORT).show()
                }
        }
    }
}
