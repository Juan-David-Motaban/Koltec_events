package com.example.myapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.device.QrScanner
import com.example.myapplication.domain.DatePreset
import com.example.myapplication.model.*
import com.example.myapplication.ui.components.DropdownField
import com.example.myapplication.ui.components.EmptyState
import com.example.myapplication.ui.components.OpportunityCard
import com.example.myapplication.viewmodel.ListaOportunidadesViewModel

/** Pestaña "Oportunidades": búsqueda por nombre y filtros por fecha, estado, tipo, ubicación, prioridad, organización y responsable. */
@Composable
fun OportunidadesTab(
    onOpen: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ListaOportunidadesViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val f = state.filters
    var showFilters by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Oportunidades", fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
            Text("${state.items.size} de ${state.total} oportunidades", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = f.query,
                    onValueChange = viewModel::onQuery,
                    label = { Text("Buscar por nombre o empresa") },
                    leadingIcon = { Icon(Icons.Filled.Search, null) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                )
                FilledTonalButton(
                    onClick = {
                        QrScanner.scan(
                            context,
                            onResult = { raw -> viewModel.onQrEscaneado(raw, onOpen) },
                            onError = viewModel::onQrError
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(56.dp)
                ) { Icon(Icons.Filled.QrCodeScanner, contentDescription = "Escanear QR") }
                FilledTonalButton(onClick = { showFilters = !showFilters }, shape = RoundedCornerShape(14.dp), modifier = Modifier.height(56.dp)) {
                    Icon(Icons.Filled.FilterList, null)
                    Text(if (f.activeCount > 0) " ${f.activeCount}" else "")
                }
            }
        }

        if (state.mensaje != null) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Text(state.mensaje.orEmpty(), color = MaterialTheme.colorScheme.error, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    TextButton(onClick = viewModel::cerrarMensaje) { Text("OK") }
                }
            }
        }

        item {
            AnimatedVisibility(visible = showFilters, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    DropdownField("Estado", f.status, OpportunityStatus.values().toList(), { it.label }, viewModel::onStatus, Icons.Filled.Flag, onClear = { viewModel.onStatus(null) })
                    DropdownField("Tipo de oportunidad", f.type, OpportunityType.values().toList(), { it.label }, viewModel::onType, Icons.Filled.Category, onClear = { viewModel.onType(null) })
                    DropdownField("Prioridad", f.priority, Priority.values().toList(), { it.label }, viewModel::onPriority, Icons.Filled.PriorityHigh, onClear = { viewModel.onPriority(null) })
                    DropdownField("Fecha del evento", f.datePreset, DatePreset.values().toList(), { it.label }, viewModel::onDatePreset, Icons.Filled.CalendarMonth)
                    DropdownField("Región", f.region, Geo.regions, { it }, viewModel::onRegion, Icons.Filled.Public, onClear = { viewModel.onRegion(null) })
                    DropdownField(
                        "Comuna", f.comuna,
                        Geo.comunas.filter { f.region == null || it.region == f.region }.map { it.name },
                        { it }, viewModel::onComuna, Icons.Filled.Place, onClear = { viewModel.onComuna(null) }
                    )
                    DropdownField(
                        "Organización", state.organizations.firstOrNull { it.id == f.orgId }, state.organizations,
                        { it.nombre }, { viewModel.onOrg(it.id) }, Icons.Filled.Business, onClear = { viewModel.onOrg(null) }
                    )
                    DropdownField(
                        "Responsable", state.owners.firstOrNull { it.correo == f.owner }, state.owners,
                        { it.nombre }, { viewModel.onOwner(it.correo) }, Icons.Filled.Person, onClear = { viewModel.onOwner(null) }
                    )
                    if (f.activeCount > 0) {
                        TextButton(onClick = viewModel::clearFilters) { Text("Limpiar filtros") }
                    }
                }
            }
        }

        if (state.items.isEmpty() && !state.loading) {
            item {
                EmptyState(
                    if (state.total == 0) "Aún no hay oportunidades" else "Sin resultados",
                    if (state.total == 0) "Toca el botón + para registrar la primera." else "Prueba cambiando la búsqueda o los filtros."
                )
            }
        }

        items(state.items, key = { it.id }) { o ->
            OpportunityCard(o, onClick = { onOpen(o.id) })
        }
    }
}
