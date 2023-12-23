package com.gabrielcastro.dragonballfundamentosandroid.Login.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.Credentials
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class LoginViewModel() : ViewModel() {

    private val baseUrl = "https://dragonball.keepcoding.education"
    private val _uiState = MutableStateFlow<UiState>(UiState.Started(true))
    val uiState: StateFlow<UiState> = _uiState
    var token: String = "" //TODO poner private

    fun tryLogin(email: String, pass: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val url = "$baseUrl/api/auth/login"
            val credentials = Credentials.basic(email, pass)
            val formBody = FormBody.Builder()
                .build()
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", credentials)
                .post(formBody)
                .build()
            val call = client.newCall(request)
            val response = call.execute()

            response.body?.let { responseBody ->
                token = responseBody.string()
                Log.w("TAG", "El token obtenido en el viewmodel es $token")
                _uiState.value = UiState.OnLoginCompleted(token)
            } ?: run { Log.w("TAG", "Error detected") }
        }
    }

    sealed class UiState {
        data class Started(val started: Boolean) : UiState()
        object Ended : UiState()
        data class Error(val error: String) : UiState()
        data class OnLoginCompleted(val token: String) : UiState()
    }
}