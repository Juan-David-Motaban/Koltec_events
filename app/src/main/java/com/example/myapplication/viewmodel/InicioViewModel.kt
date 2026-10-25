package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.App
import com.example.myapplication.domain.AlertEngine
import com.example.myapplication.domain.AlertSummary
import com.example.myapplication.domain.Kpis
import com.example.myapplication.model.Usuario
import com.example.myapplication.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class InicioUiState(
    val usuario: Usuario? = null,
    val kpis: Kpis = Kpis(),
    val alerts: AlertSummary = AlertSummary(),
    val loading: Boolean = true
)

/** Panel de inicio: indicadores generales y alertas / recordatorios internos. */
class InicioViewModel : ViewModel() {
    private val repo = App.container.oportunidades
    private val usuario = AuthRepository().usuarioActual()

    val state: StateFlow<InicioUiState> = combine(
        repo.observeOpportunities(), repo.observeAllFollowUps()
    ) { opportunities, followUps ->
        val alerts = AlertEngine.build(opportunities, followUps)
        InicioUiState(usuario, AlertEngine.kpis(opportunities, alerts), alerts, loading = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InicioUiState(usuario))
}
