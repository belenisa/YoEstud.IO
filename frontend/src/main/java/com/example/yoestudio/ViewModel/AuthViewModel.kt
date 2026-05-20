package com.example.yoestudio.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoestudio.Data.Network.ApiNet
import com.example.yoestudio.Data.Network.AuthRequest
import com.example.yoestudio.Data.Network.RegistroRequest
import com.example.yoestudio.Utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    fun login(context: Context, usuario: String, pass: String) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                val response = api.login(AuthRequest(usuario, pass))
                if (response.isSuccessful && response.body() != null) {
                    val auth = response.body()!!
                    TokenManager(context).saveToken(auth.token, auth.username)
                    _loginExitoso.value = true
                } else {
                    _error.value = "Credenciales incorrectas"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun registrar(nombre: String, email: String, pass: String) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                val response = api.registro(RegistroRequest(nombre, email, pass))
                if (response.isSuccessful) {
                    _registroExitoso.value = true
                } else {
                    _error.value = "Error en el registro"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun resetEstado() {
        _loginExitoso.value = false
        _registroExitoso.value = false
        _error.value = null
    }
}
