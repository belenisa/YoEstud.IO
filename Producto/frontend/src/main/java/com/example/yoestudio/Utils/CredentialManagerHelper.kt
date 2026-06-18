package com.example.yoestudio.Utils

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CredentialManagerHelper {

    suspend fun saveCredential(context: Context, username: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val credentialManager = CredentialManager.create(context)
                val request = CreatePasswordRequest(username, password)
                credentialManager.createCredential(context, request)
                true
            } catch (e: CreateCredentialException) {
                false
            } catch (e: Exception) {
                false
            }
        }
    }

    suspend fun retrieveCredential(context: Context): Pair<String, String>? {
        return withContext(Dispatchers.IO) {
            try {
                val credentialManager = CredentialManager.create(context)
                val request = GetCredentialRequest(
                    listOf(GetPasswordOption())
                )
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential
                if (credential is PasswordCredential) {
                    Pair(credential.id, credential.password)
                } else {
                    null
                }
            } catch (e: GetCredentialException) {
                null
            } catch (e: Exception) {
                null
            }
        }
    }
}
