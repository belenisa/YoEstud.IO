package com.example.yoestudio.ViewModel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoestudio.Data.Modelo.RolModelo
import com.example.yoestudio.Data.Modelo.UsuarioModelo
import com.example.yoestudio.Repository.UsuarioRepository
import kotlinx.coroutines.launch
import com.example.yoestudio.Data.Modelo.TipoUsuario
import java.lang.reflect.Array.set


class UsuarioView : ViewModel() {

    private val repo = UsuarioRepository()
    var usuarioActual = mutableStateOf<UsuarioModelo?>(null)
        private set


    fun login(
        context: Context,
        nombre: String,
        password: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {

            val result = repo.login(nombre, password)

            result.onSuccess { usuario ->

                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

                prefs.edit()
                    .putLong("usuario_id", usuario.id ?: -1)
                    .apply()

                usuarioActual.value = usuario

                onSuccess()
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
        val yaExiste = prefs.getBoolean("usuario_creado", false)

        if (!yaExiste) {

            viewModelScope.launch {

                val usuario = UsuarioModelo(
                    nombre = generarNombre(),
                    email = generarEmailUnico(),
                    password = generarPassword(),
                    tipo = TipoUsuario.FREE,
                    rolid = 2L
                )

                val result = repo.crear(usuario)

                result.onSuccess {

                    prefs.edit()
                        .putBoolean("usuario_creado", true)
                        .putLong("usuario_id", it.id ?: -1)
                        .apply()

                    usuarioActual.value = it

                    cargarUsuario(context)

                }

                result.onFailure {
                    println("Error creando usuario: ${it.message}")
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
                    .putBoolean("usuario_creado", true)
                    .putLong("usuario_id", it.id ?: -1)
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

        val id = obtenerUsuarioId(context)

        if (id != -1L) {

            viewModelScope.launch {

                val result = repo.obtener(id)

                result.onSuccess { usuario ->
                    usuarioActual.value = usuario
                }

                result.onFailure {
                    println("Error cargando usuario")
                }
            }
        }
    }

}


