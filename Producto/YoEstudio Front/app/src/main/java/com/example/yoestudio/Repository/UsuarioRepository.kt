package com.example.yoestudio.Repository

import com.example.yoestudio.Data.Modelo.CredencialesModelo
import com.example.yoestudio.Data.Modelo.UsuarioModelo
import com.example.yoestudio.Data.Network.ApiNet
import com.example.yoestudio.Data.Network.UsuariosService
import retrofit2.Response

class UsuarioRepository ( private val service: UsuariosService = ApiNet.usuarioService)
{
    private fun <T> Response<T>.unwrap(): T {
            if (isSuccessful) {
                val body = body()
                if (body != null) return body
                // Usa code() y message() como métodos, y reemplaza -> correcto
                throw Exception(
                    when (code()) {
                        204, 205 -> "OK sin contenido (HTTP ${code()})"
                        else     -> "Respuesta vacía del servidor (HTTP ${code()})"
                    }
                )
            } else {
                val msg = try { errorBody()?.string() } catch (_: Exception) { null }
                //code() y message() (no $code ni $message)
                throw Exception("HTTP ${code()}: ${msg ?: message() ?: "Error desconocido"}")
            }
        }

        suspend fun listar(): Result<List<UsuarioModelo>> =
            runCatching { service.listar().unwrap() }

        suspend fun obtener(id: Long): Result<UsuarioModelo> =
            runCatching { service.obtener(id).unwrap() }

        suspend fun crear(nuevo: UsuarioModelo): Result<UsuarioModelo> =
            runCatching { service.crear(nuevo).unwrap() }

        suspend fun actualizar(id: Long, datos: UsuarioModelo): Result<UsuarioModelo> =
            runCatching { service.actualizar(id, datos).unwrap() }

        suspend fun eliminar(id: Long): Result<Unit> = runCatching {
            val resp = service.eliminar(id)
            if (resp.isSuccessful) {
                Unit
            } else {
                val msg = try { resp.errorBody()?.string() } catch (_: Exception) { null }
                throw Exception("HTTP ${resp.code()}: ${msg ?: resp.message() ?: "Error desconocido"}")
            }
        }

        suspend fun login(nombre: String, contrasena: String): Result<UsuarioModelo> =
            runCatching { service.login(CredencialesModelo(nombre, contrasena)).unwrap() }

    }