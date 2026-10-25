package com.example.myapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.DateUtils
import com.example.myapplication.model.Opportunity
import com.example.myapplication.ui.components.EmptyState
import com.example.myapplication.ui.components.SectionTitle
import com.example.myapplication.ui.components.eventCountdown
import com.example.myapplication.ui.theme.*
import com.example.myapplication.viewmodel.InicioViewModel

/** Pestaña "Inicio": indicadores generales y alertas / recordatorios internos. */
@Composable
fun InicioTab(
    onOpen: (Long) -> Unit,
    onMiEmpresa: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InicioViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val a = state.alerts

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("KOLTEC EVENTS", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp, color = Violeta)
                Text(
                    "¡Hola, ${state.usuario?.nombre?.substringBefore(" ") ?: ""}!",
                    fontSize = 27.sp, fontWeight = FontWeight.ExtraBold
                )
                Text(
                    state.usuario?.tipo?.etiqueta.orEmpty(),
                    fontSize = 13.sp, color = TextoSuave, fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Gestiona tu empresa  ›",
                    fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Violeta,
                    modifier = Modifier.clickable { onMiEmpresa() }.padding(vertical = 4.dp)
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Kpi("Oportunidades", state.kpis.total, Icons.Filled.Event, Violeta, Modifier.weight(1f))
                Kpi("Abiertas", state.kpis.open, Icons.Filled.Flag, Naranja, Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Kpi("Confirmadas", state.kpis.confirmed, Icons.Filled.CheckCircle, VerdeEstado, Modifier.weight(1f))
                Kpi("Atrasadas", state.kpis.overdue, Icons.Filled.Warning, Rojo, Modifier.weight(1f))
            }
        }

        item { SectionTitle("Alertas y recordatorios (${a.total})") }

        if (a.total == 0 && !state.loading) {
            item { EmptyState("Todo al día", "No hay alertas ni recordatorios pendientes.") }
        }

        item {
            AlertGroup("Seguimientos atrasados", Icons.Filled.Warning, Rojo, a.overdueFollowUps.size) {
                a.overdueFollowUps.forEach {
                    AlertRow(it.opportunity.name, "Debía hacerse el ${DateUtils.format(it.followUp.nextDate ?: 0L)}: ${it.followUp.nextAction.ifBlank { "seguimiento" }}") { onOpen(it.opportunity.id) }
                }
            }
        }
        item {
            AlertGroup("Seguimientos próximos (7 días)", Icons.Filled.Notifications, Naranja, a.upcomingFollowUps.size) {
                a.upcomingFollowUps.forEach {
                    AlertRow(it.opportunity.name, "${DateUtils.format(it.followUp.nextDate ?: 0L)}: ${it.followUp.nextAction.ifBlank { "seguimiento" }}") { onOpen(it.opportunity.id) }
                }
            }
        }
        item {
            AlertGroup("Eventos próximos (30 días)", Icons.Filled.Event, Violeta, a.upcomingEvents.size) {
                a.upcomingEvents.forEach { AlertRow(it.name, "${DateUtils.format(it.eventStart)} · ${eventCountdown(it)} · ${it.comuna}") { onOpen(it.id) } }
            }
        }
        item {
            AlertGroup("Oportunidades sin contacto", Icons.Filled.PhoneDisabled, Naranja, a.noContact.size) {
                a.noContact.forEach { OpportunityAlert(it, "Aún no se contacta a ${it.orgName}", onOpen) }
            }
        }
        item {
            AlertGroup("Propuestas sin respuesta", Icons.Filled.HourglassEmpty, Ambar, a.unansweredProposals.size) {
                a.unansweredProposals.forEach { OpportunityAlert(it, "Sin respuesta de ${it.orgName} hace ${DateUtils.daysBetween(it.lastUpdate, DateUtils.startOfDay())} días", onOpen) }
            }
        }
        item {
            AlertGroup("Información incompleta", Icons.Filled.Info, Gris, a.incomplete.size) {
                a.incomplete.forEach { OpportunityAlert(it, "Completa los datos del evento", onOpen) }
            }
        }
    }
}

@Composable
private fun OpportunityAlert(o: Opportunity, text: String, onOpen: (Long) -> Unit) =
    AlertRow(o.name, text) { onOpen(o.id) }

@Composable
private fun Kpi(label: String, value: Int, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    // Animación: el número sube hasta su valor.
    val animated by animateIntAsState(targetValue = value, animationSpec = tween(700), label = "kpi")
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(Modifier.size(40.dp), shape = RoundedCornerShape(12.dp), color = color.copy(alpha = 0.14f)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = color) }
            }
            Column {
                Text(animated.toString(), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text(label, fontSize = 11.sp, color = TextoSuave)
            }
        }
    }
}

@Composable
private fun AlertGroup(
    title: String,
    icon: ImageVector,
    color: Color,
    count: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    if (count == 0) return
    var open by remember { mutableStateOf(true) }
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth().clickable { open = !open },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(icon, null, tint = color)
                Text(title, Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = 0.14f)) {
                    Text("$count", Modifier.padding(horizontal = 10.dp, vertical = 2.dp), color = color, fontWeight = FontWeight.Bold)
                }
                Icon(if (open) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, null)
            }
            AnimatedVisibility(visible = open, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                Column(Modifier.padding(top = 8.dp), content = content)
            }
        }
    }
}

@Composable
private fun AlertRow(title: String, subtitle: String, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 6.dp)) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Text(subtitle, fontSize = 12.sp, color = TextoSuave)
        HorizontalDivider(Modifier.padding(top = 6.dp), color = Lavanda)
    }
}
