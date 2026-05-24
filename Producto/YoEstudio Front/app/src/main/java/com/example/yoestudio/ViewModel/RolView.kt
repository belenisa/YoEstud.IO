package com.example.yoestudio.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yoestudio.Data.Modelo.RolModelo
import com.example.yoestudio.Repository.RolReepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RolView (
    private val repo: RolReepository = RolReepository()
) : ViewModel() {

    private val _roles = MutableStateFlow<List<RolModelo>>(emptyList())
    val roles: StateFlow<List<RolModelo>> = _roles

    fun cargarRoles() {
        viewModelScope.launch {
            try {
                val lista = repo.listarRoles()
                if (lista != null) _roles.value = lista
            } catch (e: Exception) {
            }
        }
    }
}