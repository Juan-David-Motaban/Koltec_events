package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.App
import com.example.myapplication.domain.DatePreset
import com.example.myapplication.domain.ListFilters
import com.example.myapplication.domain.OpportunityFilter
import com.example.myapplication.domain.QrContent
import com.example.myapplication.domain.QrPayload
import com.example.myapplication.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListaUiState(
    val items: List<Opportunity> = emptyList(),
    val total: Int = 0,
    val filters: ListFilters = ListFilters(),
    val owners: List<Usuario> = emptyList(),
    val organizations: List<Organizacion> = emptyList(),
    val mensaje: String? = null,
    val loading: Boolean = true
)

/** Búsqueda y filtros: nombre, fecha, estado, tipo, ubicación, prioridad, organización y responsable. */
class ListaOportunidadesViewModel : ViewModel() {
    private val repo = App.container.oportunidades

    private val filters = MutableStateFlow(ListFilters())
    private val owners = MutableStateFlow<List<Usuario>>(emptyList())
    private val organizations = MutableStateFlow<List<Organizacion>>(emptyList())
    private val mensaje = MutableStateFlow<String?>(null)

    init {
        cargarCatalogos()
    }

    /** Vuelve a leer responsables y organizaciones (por si se registró una empresa nueva). */
    fun cargarCatalogos() {
        viewModelScope.launch {
            owners.value = repo.usuarios()
            organizations.value = repo.organizaciones()
        }
    }

    val state: StateFlow<ListaUiState> = combine(
        repo.observeOpportunities(), filters, owners, organizations, mensaje
    ) { all, f, o, g, m ->
        ListaUiState(OpportunityFilter.apply(all, f), all.size, f, o, g, m, loading = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ListaUiState())

    fun onQuery(v: String) = filters.update { it.copy(query = v) }
    fun onStatus(v: OpportunityStatus?) = filters.update { it.copy(status = v) }
    fun onType(v: OpportunityType?) = filters.update { it.copy(type = v) }
    fun onPriority(v: Priority?) = filters.update { it.copy(priority = v) }
    fun onRegion(v: String?) = filters.update { it.copy(region = v, comuna = null) }
    fun onComuna(v: String?) = filters.update { it.copy(comuna = v) }
    fun onOwner(v: String?) = filters.update { it.copy(owner = v) }
    fun onOrg(v: Int?) = filters.update { it.copy(orgId = v) }
    fun onDatePreset(v: DatePreset) = filters.update { it.copy(datePreset = v) }
    /** Resultado del lector QR: si es el QR de una oportunidad existente, se abre su detalle. */
    fun onQrEscaneado(raw: String, abrir: (Long) -> Unit) {
        when (val contenido = QrPayload.parse(raw)) {
            is QrContent.Oportunidad -> viewModelScope.launch {
                if (repo.getOpportunity(contenido.id) != null) {
                    mensaje.value = null
                    abrir(contenido.id)
                } else {
                    mensaje.value = "Esa oportunidad no existe en este dispositivo."
                }
            }
            is QrContent.Empresa -> mensaje.value = "Ese QR es de una empresa. Escanéalo desde Mi empresa."
            null -> mensaje.value = "El código escaneado no es un QR de Koltec Events."
        }
    }

    fun onQrError(texto: String) { mensaje.value = texto }
    fun cerrarMensaje() { mensaje.value = null }

    fun clearFilters() = filters.update { ListFilters(query = it.query) }
}
