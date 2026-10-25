package com.example.myapplication.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.QrContent
import com.example.myapplication.domain.QrPayload
import com.example.myapplication.model.Organizacion
import com.example.myapplication.model.Usuario
import com.example.myapplication.repository.OrganizacionRepository
import com.example.myapplication.repository.UsuarioRepository

data class MiEmpresaUiState(
    val usuario: Usuario? = null,
    val empresaActual: Organizacion? = null,
    val empresas: List<Organizacion> = emptyList(),
    val busqueda: String = "",
    val mensaje: String? = null,
    val mensajeEsError: Boolean = false
) {
    /** Contenido del QR de la empresa actual (para mostrarlo y que otra persona lo escanee). */
    val qrEmpresa: String? get() = empresaActual?.let { QrPayload.empresa(it.id) }

    val empresasFiltradas: List<Organizacion>
        get() = if (busqueda.isBlank()) empresas
        else empresas.filter {
            it.nombre.contains(busqueda.trim(), ignoreCase = true) ||
                it.rubro.contains(busqueda.trim(), ignoreCase = true) ||
                it.comuna.contains(busqueda.trim(), ignoreCase = true)
        }
}

/** Empresa de la cuenta del usuario: unirse a una existente, registrar una nueva o salir de ella. */
class MiEmpresaViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val correo: String = savedStateHandle.get<String>("username").orEmpty()
    private val usuarios = UsuarioRepository()
    private val empresas = OrganizacionRepository()

    var uiState by mutableStateOf(MiEmpresaUiState())
        private set

    init {
        cargar()
    }

    /** Vuelve a leer usuario y empresas (se llama al volver de registrar una empresa). */
    fun cargar() {
        val u = usuarios.buscarPorCorreo(correo)
        uiState = uiState.copy(
            usuario = u,
            empresaActual = u?.empresaId?.let { empresas.buscarPorId(it) },
            empresas = empresas.obtenerTodas()
        )
    }

    fun onBusqueda(v: String) {
        uiState = uiState.copy(busqueda = v)
    }

    fun unirse(empresa: Organizacion) {
        usuarios.asignarEmpresa(correo, empresa.id)
        cargar()
        uiState = uiState.copy(mensaje = "Ahora perteneces a ${empresa.nombre}.", mensajeEsError = false, busqueda = "")
    }

    fun salir() {
        usuarios.asignarEmpresa(correo, null)
        cargar()
        uiState = uiState.copy(mensaje = "Saliste de la empresa.", mensajeEsError = false)
    }

    /** Resultado del lector QR: si es el QR de una empresa registrada, el usuario se une a ella. */
    fun onQrEscaneado(raw: String) {
        when (val contenido = QrPayload.parse(raw)) {
            is QrContent.Empresa -> {
                val empresa = empresas.buscarPorId(contenido.id)
                if (empresa == null) mostrarError("Esa empresa no está registrada en este dispositivo.")
                else unirse(empresa)
            }
            is QrContent.Oportunidad -> mostrarError("Ese QR es de una oportunidad. Escanéalo desde la pestaña Oportunidades.")
            null -> mostrarError("El código escaneado no es un QR de Koltec Events.")
        }
    }

    fun onQrError(texto: String) = mostrarError(texto)

    private fun mostrarError(texto: String) {
        uiState = uiState.copy(mensaje = texto, mensajeEsError = true)
    }
}
