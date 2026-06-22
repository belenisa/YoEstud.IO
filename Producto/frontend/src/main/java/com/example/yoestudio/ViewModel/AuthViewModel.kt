package com.example.yoestudio.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoestudio.Data.Network.*
import com.example.yoestudio.Utils.CredentialManagerHelper
import com.example.yoestudio.Utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class AuthViewModel : ViewModel() {

    private val api = ApiNet.asistenteIA

    private val _cargando = MutableStateFlow(false)
    val cargando = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _loginExitoso = MutableStateFlow(false)
    val loginExitoso = _loginExitoso.asStateFlow()

    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso = _registroExitoso.asStateFlow()

    private val _codigoEnviado = MutableStateFlow(false)
    val codigoEnviado = _codigoEnviado.asStateFlow()

    private val _codigoRecibido = MutableStateFlow<String?>(null)
    val codigoRecibido = _codigoRecibido.asStateFlow()

    private val _contrasenaActualizada = MutableStateFlow(false)
    val contrasenaActualizada = _contrasenaActualizada.asStateFlow()

    fun login(context: Context, usuario: String, pass: String) {
        println("Intentando login para usuario: $usuario")
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                val response = api.login(AuthRequest(usuario, pass))
                println("Respuesta recibida: ${response.code()}")
                if (response.isSuccessful && response.body() != null) {
                    val auth = response.body()!!
                    println("Login exitoso, guardando token...")
                    TokenManager(context).saveToken(auth.token, auth.username, auth.usuarioId)
                    CredentialManagerHelper.saveCredential(context, usuario, pass)
                    _loginExitoso.value = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error en login: $errorBody")
                    _error.value = parseError(errorBody) ?: "Credenciales incorrectas"
                }
            } catch (e: Exception) {
                println("Error de conexión en login: ${e.message}")
                _error.value = "Error de conexión: ${e.localizedMessage}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun registrar(nombre: String, email: String, pass: String) {
        println("Intentando registro para: $email")
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                val response = api.registro(RegistroRequest(nombre, email, pass))
                println("Respuesta registro: ${response.code()}")
                if (response.isSuccessful) {
                    println("Registro exitoso")
                    _registroExitoso.value = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("Error en registro: $errorBody")
                    _error.value = parseError(errorBody) ?: "Error en el registro"
                }
            } catch (e: Exception) {
                println("Error de conexión en registro: ${e.message}")
                _error.value = "Error de conexión: ${e.localizedMessage}"
            } finally {
                _cargando.value = false
            }
        }
    }

    private fun parseError(json: String?): String? {
        return try {
            json?.let { JSONObject(it).getString("error") }
        } catch (e: Exception) {
            null
        }
    }

    fun recuperarContrasena(email: String) {
        println("Solicitando recuperación para: $email")
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                val response = api.recuperarContrasena(RecuperarRequest(email))
                println("Respuesta recuperación: ${response.code()}")
                if (response.isSuccessful) {
                    val body = response.body()?.string()
                    val codigo = JSONObject(body ?: "{}").optString("codigo", "")
                    _codigoRecibido.value = codigo
                    _codigoEnviado.value = true
                    println("Código recibido: $codigo")
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = parseError(errorBody) ?: "Error al enviar código"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.localizedMessage}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun verificarCodigo(email: String, codigo: String, nuevaPassword: String) {
        println("Verificando código para: $email")
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                val response = api.verificarCodigo(VerificarCodigoRequest(email, codigo, nuevaPassword))
                println("Respuesta verificación: ${response.code()}")
                if (response.isSuccessful) {
                    _contrasenaActualizada.value = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = parseError(errorBody) ?: "Código inválido"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.localizedMessage}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun resetEstado() {
        _loginExitoso.value = false
        _registroExitoso.value = false
        _codigoEnviado.value = false
        _codigoRecibido.value = null
        _contrasenaActualizada.value = false
        _error.value = null
    }
}
