package com.example.myapplication.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.App
import com.example.myapplication.domain.DateUtils
import com.example.myapplication.model.*
import com.example.myapplication.repository.AuthRepository
import com.example.myapplication.validation.FormField
import com.example.myapplication.validation.OpportunityInput
import com.example.myapplication.validation.OpportunityValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormularioUiState(
    val id: Long = 0,
    val input: OpportunityInput = OpportunityInput(),
    val type: OpportunityType = OpportunityType.EVENTO_CORPORATIVO,
    val priority: Priority = Priority.MEDIA,
    val status: OpportunityStatus = OpportunityStatus.EVENTO_IDENTIFICADO,
    val potential: Int = 3,
    val affinity: Int = 3,
    val owner: String = "",
    val orgId: Int? = null,
    val region: String = "",
    val photoPath: String? = null,
    val errors: Map<FormField, String> = emptyMap(),
    val orgError: String? = null,
    val owners: List<Usuario> = emptyList(),
    val orgs: List<Organizacion> = emptyList(),
    val loaded: Boolean = false,
    val saving: Boolean = false,
    val savedId: Long? = null,
    val message: String? = null
) {
    val isEdit: Boolean get() = id != 0L
}

/** Formulario de oportunidad. La validación vive en OpportunityValidator; la pantalla solo muestra el estado. */
class FormularioOportunidadViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val repo = App.container.oportunidades
    private val auth = AuthRepository()
    private val opportunityId: Long = savedStateHandle.get<Long>("id") ?: 0L

    private val _state = MutableStateFlow(FormularioUiState(id = opportunityId))
    val state: StateFlow<FormularioUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val owners = repo.usuarios()
            val orgs = repo.organizaciones()
            val me = auth.usuarioActual()
            if (opportunityId == 0L) {
                _state.update {
                    it.copy(
                        owners = owners, orgs = orgs, owner = me?.correo.orEmpty(),
                        orgId = me?.empresaId, loaded = true,
                        input = it.input.copy(detectedDate = DateUtils.format(System.currentTimeMillis()))
                    )
                }
            } else {
                val o = repo.getOpportunity(opportunityId)
                if (o == null) _state.update { it.copy(owners = owners, orgs = orgs, loaded = true, message = "La oportunidad no existe") }
                else _state.update { desde(o, owners, orgs) }
            }
        }
    }

    private fun desde(o: Opportunity, owners: List<Usuario>, orgs: List<Organizacion>) = FormularioUiState(
        id = o.id,
        input = OpportunityInput(
            name = o.name, description = o.description, source = o.source,
            detectedDate = DateUtils.format(o.detectedDate),
            eventStart = DateUtils.format(o.eventStart), eventEnd = DateUtils.format(o.eventEnd),
            schedule = o.schedule, comuna = o.comuna, address = o.address,
            attendees = o.estimatedAttendees.toString(), targetAudience = o.targetAudience,
            referenceCost = o.referenceCost.toString(), conditions = o.conditions, notes = o.notes
        ),
        type = o.type, priority = o.priority, status = o.status, potential = o.potential,
        affinity = o.affinity, owner = o.owner, orgId = o.orgId, region = o.region,
        photoPath = o.photoPath, owners = owners, orgs = orgs, loaded = true
    )

    fun onFieldChange(field: FormField, value: String) = _state.update { s ->
        val input = when (field) {
            FormField.NAME -> s.input.copy(name = value)
            FormField.DESCRIPTION -> s.input.copy(description = value)
            FormField.SOURCE -> s.input.copy(source = value)
            FormField.DETECTED_DATE -> s.input.copy(detectedDate = value)
            FormField.EVENT_START -> s.input.copy(eventStart = value)
            FormField.EVENT_END -> s.input.copy(eventEnd = value)
            FormField.SCHEDULE -> s.input.copy(schedule = value)
            FormField.COMUNA -> s.input.copy(comuna = value)
            FormField.ADDRESS -> s.input.copy(address = value)
            FormField.ATTENDEES -> s.input.copy(attendees = value.filter { it.isDigit() })
            FormField.TARGET -> s.input.copy(targetAudience = value)
            FormField.COST -> s.input.copy(referenceCost = value.filter { it.isDigit() })
            FormField.CONDITIONS -> s.input.copy(conditions = value)
            FormField.NOTES -> s.input.copy(notes = value)
        }
        // Validación en línea del campo editado (y de la fecha de término si cambia el inicio).
        val toCheck = buildList {
            add(field)
            if (field == FormField.EVENT_START && input.eventEnd.isNotBlank()) add(FormField.EVENT_END)
        }
        val errors = s.errors.toMutableMap()
        toCheck.forEach { f ->
            val e = OpportunityValidator.validateField(f, input)
            if (e == null) errors.remove(f) else errors[f] = e
        }
        s.copy(input = input, errors = errors, message = null)
    }

    fun onType(v: OpportunityType) = _state.update { it.copy(type = v) }
    fun onPriority(v: Priority) = _state.update { it.copy(priority = v) }
    fun onStatus(v: OpportunityStatus) = _state.update { it.copy(status = v) }
    fun onPotential(v: Int) = _state.update { it.copy(potential = v) }
    fun onAffinity(v: Int) = _state.update { it.copy(affinity = v) }
    fun onOwner(v: String) = _state.update { it.copy(owner = v) }
    fun onOrg(v: Int) = _state.update { it.copy(orgId = v, orgError = null) }
    fun onPhotoCaptured(path: String?) = _state.update { it.copy(photoPath = path) }
    fun onMessageShown() = _state.update { it.copy(message = null) }

    fun onComuna(name: String) {
        val c = Geo.byName(name) ?: return
        _state.update {
            it.copy(input = it.input.copy(comuna = c.name), region = c.region, errors = it.errors - FormField.COMUNA, message = null)
        }
    }

    /** Recurso nativo (ubicación): solo sugiere la comuna de referencia más cercana; no se guarda la posición. */
    fun onLocationDetected(lat: Double, lon: Double) {
        val c = Geo.nearest(lat, lon)
        onComuna(c.name)
        _state.update { it.copy(message = "Comuna sugerida por ubicación aproximada: ${c.name}") }
    }

    fun onLocationUnavailable(reason: String) = _state.update { it.copy(message = reason) }

    fun save() {
        val s = _state.value
        val errors = OpportunityValidator.validateAll(s.input)
        val orgError = if (s.orgId == null) "Selecciona la organización responsable" else null
        if (errors.isNotEmpty() || orgError != null) {
            val total = errors.size + (if (orgError != null) 1 else 0)
            _state.update { it.copy(errors = errors, orgError = orgError, message = "Revisa los $total campo(s) marcados en rojo") }
            return
        }
        val i = s.input
        val geo = Geo.byName(i.comuna)
        val opportunity = Opportunity(
            id = s.id, name = i.name.trim(), type = s.type, description = i.description.trim(),
            source = i.source.trim(), detectedDate = DateUtils.parse(i.detectedDate)!!,
            owner = s.owner, priority = s.priority, potential = s.potential, affinity = s.affinity,
            status = s.status, lastUpdate = System.currentTimeMillis(), notes = i.notes.trim(),
            orgId = s.orgId!!, eventStart = DateUtils.parse(i.eventStart)!!, eventEnd = DateUtils.parse(i.eventEnd)!!,
            schedule = i.schedule.trim(), address = i.address.trim(), comuna = i.comuna,
            region = geo?.region ?: s.region, targetAudience = i.targetAudience.trim(),
            estimatedAttendees = i.attendees.toInt(), conditions = i.conditions.trim(),
            referenceCost = i.referenceCost.toInt(), photoPath = s.photoPath
        )
        _state.update { it.copy(saving = true, errors = emptyMap(), orgError = null) }
        viewModelScope.launch {
            val user = auth.correoActual()
            val id = if (s.isEdit) {
                repo.update(opportunity, user); s.id
            } else {
                repo.create(opportunity, user)
            }
            _state.update { it.copy(saving = false, savedId = id) }
        }
    }
}
