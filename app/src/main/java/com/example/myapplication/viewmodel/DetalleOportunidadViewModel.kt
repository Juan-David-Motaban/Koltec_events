package com.example.myapplication.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.App
import com.example.myapplication.domain.DateUtils
import com.example.myapplication.domain.QrPayload
import com.example.myapplication.model.*
import com.example.myapplication.repository.AuthRepository
import com.example.myapplication.validation.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetalleUiState(
    val opportunity: Opportunity? = null,
    val contacts: List<Contact> = emptyList(),
    val followUps: List<FollowUp> = emptyList(),
    val history: List<StatusChange> = emptyList(),
    val result: ActivationResult? = null,
    val loading: Boolean = true
)

/** Detalle: contactos, seguimientos, historial, cambio de estado y resultado de la activación. */
class DetalleOportunidadViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val repo = App.container.oportunidades
    private val auth = AuthRepository()
    private val id: Long = savedStateHandle.get<Long>("id") ?: 0L

    private val core = combine(
        repo.observeOpportunity(id), repo.observeContacts(id), repo.observeFollowUps(id)
    ) { o, c, f -> Triple(o, c, f) }

    private val extra = combine(repo.observeHistory(id), repo.observeResult(id)) { h, r -> h to r }

    val state: StateFlow<DetalleUiState> = combine(core, extra) { (o, c, f), (h, r) ->
        DetalleUiState(o, c, f, h, r, loading = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetalleUiState())

    /** Contenido del QR de esta oportunidad. */
    val qrContenido: String = QrPayload.oportunidad(id)

    private fun user() = auth.correoActual()

    fun changeStatus(newStatus: OpportunityStatus) {
        viewModelScope.launch { repo.changeStatus(id, newStatus, user()) }
    }

    /** Devuelve los errores de validación; si no hay, guarda el contacto. */
    fun addContact(input: ContactInput, area: ContactArea, medium: String): Map<ContactField, String> {
        val errors = ContactValidator.validate(input)
        if (errors.isEmpty()) {
            viewModelScope.launch {
                repo.addContact(
                    Contact(
                        opportunityId = id, name = input.name.trim(), role = input.role.trim(), area = area,
                        email = input.email.trim(), phone = input.phone.trim(), preferredMedium = medium,
                        lastContact = DateUtils.startOfDay()
                    )
                )
            }
        }
        return errors
    }

    fun addFollowUp(input: FollowUpInput, type: ActionType): Map<FollowUpField, String> {
        val errors = FollowUpValidator.validate(input)
        if (errors.isEmpty()) {
            viewModelScope.launch {
                repo.addFollowUp(
                    FollowUp(
                        opportunityId = id, user = user(), type = type,
                        dateTime = System.currentTimeMillis(), result = input.result.trim(),
                        nextAction = input.nextAction.trim(),
                        nextDate = if (input.nextDate.isBlank()) null else DateUtils.parse(input.nextDate)
                    )
                )
            }
        }
        return errors
    }

    fun saveResult(input: ResultInput, evaluation: Int, learnings: String): Map<ResultField, String> {
        val errors = ResultValidator.validate(input)
        if (errors.isEmpty()) {
            viewModelScope.launch {
                repo.saveResult(
                    ActivationResult(
                        opportunityId = id, date = DateUtils.parse(input.date)!!,
                        attendees = input.attendees.toInt(), contactsGenerated = input.contacts.toInt(),
                        interested = input.interested.toInt(), enrollments = input.enrollments.toInt(),
                        teamNotes = input.notes.trim(), evaluation = evaluation, learnings = learnings.trim()
                    ),
                    user()
                )
            }
        }
        return errors
    }

    fun delete(onDone: () -> Unit) {
        viewModelScope.launch {
            repo.delete(id)
            onDone()
        }
    }
}
