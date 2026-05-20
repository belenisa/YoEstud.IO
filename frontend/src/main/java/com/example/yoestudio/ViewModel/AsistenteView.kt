package com.example.yoestudio.ViewModel

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoestudio.Data.Repository.AsistenteRepository
import com.example.yoestudio.Domain.Model.Mensaje
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class AsistenteView : ViewModel() {

    private val repository = AsistenteRepository()
    
    private val _mensajes = MutableStateFlow<List<Mensaje>>(emptyList())
    val mensajes = _mensajes.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando = _cargando.asStateFlow()

    fun enviarA_IA(prompt: String) {
        if (prompt.isBlank() || _cargando.value) return

        _mensajes.value += Mensaje(prompt, esDelUsuario = true)
        _cargando.value = true

        viewModelScope.launch {
            repository.enviarMensajeIA(prompt)
                .onSuccess { mensajeRespuesta ->
                    _mensajes.value += mensajeRespuesta
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
                        val fileName = mensaje.nombreArchivo ?: "Prueba_${System.currentTimeMillis()}.txt"
                        var success = false

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val contentValues = ContentValues().apply {
                                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
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
