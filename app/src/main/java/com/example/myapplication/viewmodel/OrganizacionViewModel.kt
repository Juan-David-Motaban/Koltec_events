package com.example.myapplication.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.Geo
import com.example.myapplication.model.Organizacion
import com.example.myapplication.repository.OrganizacionRepository
import com.example.myapplication.repository.UsuarioRepository
import com.example.myapplication.validation.OrgField
import com.example.myapplication.validation.OrgInput
import com.example.myapplication.validation.OrgValidator

data class OrganizacionUiState(
    val input: OrgInput = OrgInput(),
    val errores: Map<OrgField, String> = emptyMap(),
    val organizaciones: List<Organizacion> = emptyList(),
    val mensaje: String? = null,
    val exito: Boolean = false
)

/** Registro y listado de empresas. Toda la validación está en OrgValidator, no en la pantalla. */
class OrganizacionViewModel : ViewModel() {

    private val repository = OrganizacionRepository()
    private val usuarios = UsuarioRepository()

    var uiState by mutableStateOf(OrganizacionUiState())
        private set

    /** Id de la última organización registrada con éxito (para asociarla al usuario). */
    var ultimaRegistradaId: Int? = null
        private set

    init {
        cargar()
    }

    fun cargar() {
        uiState = uiState.copy(organizaciones = repository.obtenerTodas())
    }

    fun onCampo(campo: OrgField, valor: String) {
        val i = uiState.input
        val nuevo = when (campo) {
            OrgField.NOMBRE -> i.copy(nombre = valor)
            OrgField.TIPO -> i.copy(tipo = valor)
            OrgField.RUBRO -> i.copy(rubro = valor)
            OrgField.COMUNA -> i.copy(comuna = valor)
            OrgField.SITIO_WEB -> i.copy(sitioWeb = valor)
            OrgField.PUBLICO -> i.copy(publico = valor)
            OrgField.OBSERVACIONES -> i.copy(observaciones = valor)
        }
        val errores = uiState.errores.toMutableMap()
        val e = OrgValidator.validar(campo, nuevo)
        if (e == null) errores.remove(campo) else errores[campo] = e
        uiState = uiState.copy(input = nuevo, errores = errores, mensaje = null, exito = false)
    }

    fun registrar() {
        val i = uiState.input
        val errores = OrgValidator.validarTodo(i)
        if (errores.isNotEmpty()) {
            uiState = uiState.copy(errores = errores, mensaje = "Revisa los campos marcados en rojo.", exito = false)
            return
        }
        val geo = Geo.byName(i.comuna.trim())
        val id = repository.agregar(
            Organizacion(
                nombre = i.nombre.trim(), tipoOrganizacion = i.tipo.trim(), rubro = i.rubro.trim(),
                comuna = geo?.name ?: i.comuna.trim(), region = geo?.region ?: "",
                sitioWeb = i.sitioWeb.trim(), publicoAsociado = i.publico.trim(),
                observaciones = i.observaciones.trim()
            )
        )
        ultimaRegistradaId = id
        uiState = OrganizacionUiState(
            organizaciones = repository.obtenerTodas(),
            mensaje = "Organización registrada correctamente.", exito = true
        )
    }

    /** La empresa recién registrada queda asociada a la cuenta del usuario. */
    fun vincularConUsuario(correo: String, empresaId: Int) {
        usuarios.asignarEmpresa(correo, empresaId)
    }
}
