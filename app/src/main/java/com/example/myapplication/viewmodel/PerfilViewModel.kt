package com.example.myapplication.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.Usuario
import com.example.myapplication.repository.AuthRepository
import com.example.myapplication.repository.OrganizacionRepository

data class PerfilUiState(
    val usuario: Usuario? = null,
    val empresa: String? = null
)

/** Perfil del usuario con sesión activa y cierre de sesión. */
class PerfilViewModel : ViewModel() {
    private val auth = AuthRepository()
    private val empresas = OrganizacionRepository()

    var uiState by mutableStateOf(PerfilUiState())
        private set

    init {
        cargar()
    }

    fun cargar() {
        val u = auth.usuarioActual()
        uiState = PerfilUiState(u, u?.empresaId?.let { empresas.buscarPorId(it)?.nombre })
    }

    fun cerrarSesion() = auth.logout()
}
