package com.example.myapplication.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.repository.AuthRepository

class LoginViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onUsernameChange(value: String) {
        uiState = uiState.copy(username = value, error = null)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value, error = null)
    }

    fun onRecordarChange(value: Boolean) {
        uiState = uiState.copy(recordar = value)
    }

    /** Valida y entrega la ruta de destino (Mi empresa si aún no tiene empresa, o el panel). */
    fun submit(onSuccess: (String) -> Unit) {
        if (uiState.username.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(error = "Ingresa tu correo y contraseña")
            return
        }
        uiState = uiState.copy(isLoading = true, error = null)
        val usuario = repo.login(uiState.username, uiState.password, uiState.recordar)
        uiState = uiState.copy(isLoading = false)

        if (usuario == null) {
            uiState = uiState.copy(error = "Credenciales inválidas")
            return
        }
        uiState = uiState.copy(password = "")
        onSuccess(repo.rutaInicial(usuario))
    }
}
