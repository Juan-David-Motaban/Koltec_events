package com.example.myapplication.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.TipoUsuario
import com.example.myapplication.repository.UsuarioRepository
import com.example.myapplication.util.ValidadorFormulario

data class RegistroUsuarioUiState(
    val nombre: String = "",
    val correo: String = "",
    val password: String = "",
    val confirmacion: String = "",
    val tipo: TipoUsuario = TipoUsuario.NORMAL,
    val codigoAdmin: String = "",
    val errores: Map<String, String> = emptyMap(),
    val registrado: Boolean = false,
    val mensaje: String? = null
)

class RegistroUsuarioViewModel(
    private val repo: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    var uiState by mutableStateOf(RegistroUsuarioUiState())
        private set

    private fun limpiar(campo: String) = uiState.errores - campo

    fun onNombre(v: String) { uiState = uiState.copy(nombre = v, errores = limpiar("nombre"), mensaje = null) }
    fun onCorreo(v: String) { uiState = uiState.copy(correo = v, errores = limpiar("correo"), mensaje = null) }
    fun onPassword(v: String) { uiState = uiState.copy(password = v, errores = limpiar("password"), mensaje = null) }
    fun onConfirmacion(v: String) { uiState = uiState.copy(confirmacion = v, errores = limpiar("confirmacion"), mensaje = null) }
    fun onCodigoAdmin(v: String) { uiState = uiState.copy(codigoAdmin = v, errores = limpiar("codigoAdmin"), mensaje = null) }

    fun onTipo(t: TipoUsuario) {
        uiState = uiState.copy(tipo = t, codigoAdmin = "", errores = limpiar("codigoAdmin"), mensaje = null)
    }

    fun registrar() {
        val s = uiState
        val errores = ValidadorFormulario.erroresUsuario(
            nombre = s.nombre,
            correo = s.correo,
            password = s.password,
            confirmacion = s.confirmacion,
            correoExiste = repo.existeCorreo(s.correo),
            esAdministrador = s.tipo == TipoUsuario.ADMINISTRADOR,
            codigoAdminValido = repo.codigoAdminValido(s.codigoAdmin)
        )

        if (errores.isNotEmpty()) {
            uiState = s.copy(errores = errores, mensaje = "Revisa los campos marcados antes de continuar.")
            return
        }

        repo.registrar(s.nombre, s.correo, s.password, s.tipo)
        uiState = RegistroUsuarioUiState(
            tipo = s.tipo,
            registrado = true,
            mensaje = "Cuenta de ${s.tipo.etiqueta.lowercase()} creada. Inicia sesión para unirte a una empresa o registrar la tuya."
        )
    }
}
