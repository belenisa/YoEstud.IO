package com.example.yoestudio.ViewModel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoestudio.Data.Modelo.UsuarioModelo
import com.example.yoestudio.Repository.UsuarioRepository
import kotlinx.coroutines.launch
import com.example.yoestudio.Data.Modelo.TipoUsuario


class UsuarioView : ViewModel() {

    private val repo = UsuarioRepository()
    var usuarioActual = mutableStateOf<UsuarioModelo?>(null)
        private set
    var entro by mutableStateOf(false)
    fun login(
        context: Context,
        nombre: String,
        password: String,
        onError: () -> Unit
    ) {
        viewModelScope.launch {

            val result = repo.login(nombre, password)

            result.onSuccess { usuario ->

                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

                prefs.edit()
                    .putLong("usuario_id", usuario.id ?: -1)
                    .putString("usuario_nombre", usuario.nombre)
                    .putString("usuario_email", usuario.email)
                    .putString("usuario_tipo", usuario.tipo.name)
                    .putBoolean("logueado", true)
                    .apply()

                usuarioActual.value = usuario
                entro = true
            }

            result.onFailure {
                onError()
            }
        }
    }

    fun generarEmailUnico(): String {
        val tiempo = System.currentTimeMillis()
        return "user$tiempo@yoestudio.com"
    }

    fun generarPassword(): String {
        return java.util.UUID.randomUUID().toString()
    }

    fun generarNombre(): String {
        val num = (1000..9999).random()
        return "YoEstudio$num"
    }

    fun crearUsuarioAuto(context: Context) {

        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val id = prefs.getLong("usuario_id", -1)
        val nombre = prefs.getString("usuario_nombre", "")

        if (id == -1L || nombre.isNullOrEmpty()) {

            viewModelScope.launch {

                val usuario = UsuarioModelo(
                    nombre = generarNombre(),
                    email = generarEmailUnico(),
                    password = generarPassword(),
                    tipo = TipoUsuario.FREE,
                    rolid = 2L
                )

                val result = repo.crear(usuario)

                result.onSuccess { usuario ->

                    val nombreSeguro = if (usuario.nombre.isNullOrEmpty()) {
                        generarNombre()
                    } else {
                        usuario.nombre
                    }

                    prefs.edit()
                        .putLong("usuario_id", usuario.id ?: -1)
                        .putString("usuario_nombre", nombreSeguro)
                        .putString("usuario_email", usuario.email)
                        .putString("usuario_tipo", usuario.tipo.name)
                        .putLong("usuario_auto_id", usuario.id ?: -1)
                        .putString("usuario_auto_nombre", nombreSeguro)
                        .putString("usuario_auto_email", usuario.email)
                        .apply()

                    usuarioActual.value = usuario.copy(nombre = nombreSeguro)
                }
            }

        } else {
            cargarUsuario(context)
        }
    }

    fun crearUsuarioManual(
        context: Context,
        nombre: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {

            val usuario = UsuarioModelo(
                nombre = nombre,
                email = email,
                password = password,
                tipo = TipoUsuario.PREMIUM,
                rolid = 2L
            )

            val result = repo.crear(usuario)

            result.onSuccess {

                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

                prefs.edit()
                    .putLong("usuario_id", it.id ?: -1)
                    .putString("usuario_nombre", it.nombre)
                    .putString("usuario_email", it.email)
                    .putString("usuario_tipo", it.tipo.name)
                    .putBoolean("logueado", true)
                    .apply()

                usuarioActual.value = it

                onSuccess()
            }

            result.onFailure {
                onError()
            }
        }
    }

    fun obtenerUsuarioId(context: Context): Long {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getLong("usuario_id", -1)
    }

    fun cargarUsuario(context: Context) {

        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val nombre = prefs.getString("usuario_nombre", "NO HAY")

        println("DEBUG NOMBRE: $nombre")

        usuarioActual.value = UsuarioModelo(
            id = prefs.getLong("usuario_id", -1),
            nombre = prefs.getString("usuario_nombre", "") ?: "",
            email = prefs.getString("usuario_email", "") ?: "",
            password = "",
            tipo = TipoUsuario.valueOf(
                prefs.getString("usuario_tipo", TipoUsuario.FREE.name)!!
            ),
            rolid = 2L
        )
    }
}


