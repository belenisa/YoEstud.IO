package com.example.yoestudio.ViewModel

import android.content.ContentValues
import android.content.Context
import android.net.Uri
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

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
                    val esPrueba = mensajeRespuesta.texto.contains("# ") || mensajeRespuesta.texto.contains("Prueba")
                    _mensajes.value += mensajeRespuesta.copy(
                        esArchivo = esPrueba,
                        nombreArchivo = if (esPrueba) "Prueba_Estudio.pdf" else null
                    )
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

    fun enviarArchivo(context: Context, uri: Uri, prompt: String = "Analiza este documento") {
        if (_cargando.value) return
        
        _cargando.value = true
        _mensajes.value += Mensaje("Enviando archivo...", esDelUsuario = true)

        viewModelScope.launch {
            try {
                val file = uriToFile(context, uri)
                val requestFile = file.asRequestBody(context.contentResolver.getType(uri)?.toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("archivo", file.name, requestFile)
                val mensajeBody = prompt.toRequestBody("text/plain".toMediaTypeOrNull())

                repository.enviarArchivoIA(body, mensajeBody)
                    .onSuccess { 
                        _mensajes.value += it.copy(esArchivo = true, nombreArchivo = "Resumen_${file.name}.pdf") 
                    }
                    .onFailure { 
                        _mensajes.value += Mensaje("Error: ${it.localizedMessage}", false)
                    }
            } catch (e: Exception) {
                _mensajes.value += Mensaje("Error al preparar archivo: ${e.localizedMessage}", false)
            } finally {
                _cargando.value = false
            }
        }
    }

    fun descargarYGuardarPdf(context: Context, mensaje: Mensaje) {
        android.util.Log.d("AsistenteView", "Iniciando descarga de PDF para el texto: ${mensaje.texto.take(50)}...")
        viewModelScope.launch {
            repository.descargarPdf(mensaje.texto)
                .onSuccess { bytes ->
                    android.util.Log.d("AsistenteView", "PDF recibido con éxito, tamaño: ${bytes.size} bytes")
                    try {
                        val fileName = mensaje.nombreArchivo ?: "Prueba_YoEstudio_${System.currentTimeMillis()}.pdf"
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
                            android.util.Log.d("AsistenteView", "Archivo guardado exitosamente en Descargas")
                            android.widget.Toast.makeText(context, "Archivo guardado en Descargas: $fileName", android.widget.Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AsistenteView", "Error al guardar el archivo", e)
                        android.widget.Toast.makeText(context, "Error al guardar: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
                .onFailure { error ->
                    android.util.Log.e("AsistenteView", "Error en el repositorio al descargar PDF", error)
                    android.widget.Toast.makeText(context, "Error al descargar el PDF: ${error.localizedMessage}", android.widget.Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_upload_${System.currentTimeMillis()}")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        return file
    }
}
