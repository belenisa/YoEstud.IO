package com.example.yoestudio.Repository

import com.example.yoestudio.Data.Modelo.RolModelo
import com.example.yoestudio.Data.Network.ApiNet
import com.example.yoestudio.Data.Network.RolService

class RolReepository (
    private val service: RolService = ApiNet.rolService
    ) {
        suspend fun listarRoles(): List<RolModelo>? {
            val response = service.listar()
            return if (response.isSuccessful) response.body() else null
        }

        suspend fun obtenerRol(id: Long): RolModelo? {
            val response = service.obtener(id)
            return if (response.isSuccessful) response.body() else null
        }
}