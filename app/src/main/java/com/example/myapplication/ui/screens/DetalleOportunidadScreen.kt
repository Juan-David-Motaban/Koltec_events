package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.domain.DateUtils
import com.example.myapplication.domain.formatClp
import com.example.myapplication.model.*
import com.example.myapplication.ui.components.*
import com.example.myapplication.validation.*
import com.example.myapplication.viewmodel.DetalleOportunidadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleOportunidadScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: DetalleOportunidadViewModel = viewModel()
) {
    val s by viewModel.state.collectAsState()
    val o = s.opportunity
    var showContact by remember { mutableStateOf(false) }
    var showFollowUp by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }
    var showQr by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
                actions = {
                    if (o != null) {
                        IconButton(onClick = { showQr = true }) { Icon(Icons.Filled.QrCode, "Mostrar QR") }
                        IconButton(onClick = { onEdit(o.id) }) { Icon(Icons.Filled.Edit, "Editar") }
                        IconButton(onClick = { showDelete = true }) { Icon(Icons.Filled.Delete, "Eliminar") }
                    }
                }
            )
        }
    ) { inner ->
        if (o == null) {
            Box(Modifier.padding(inner).fillMaxSize().padding(20.dp)) {
                if (!s.loading) EmptyState("No encontrada", "La oportunidad ya no existe.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier.padding(inner).fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (showQr) {
                QrDialog(
                    titulo = o.name,
                    descripcion = "Escanea este código desde la pestaña Oportunidades para abrir esta ficha.",
                    contenido = viewModel.qrContenido,
                    onDismiss = { showQr = false }
                )
            }

            Text(o.name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Text(o.orgName, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(o.status)
                PriorityChip(o.priority)
            }

            o.photoPath?.let { PhotoThumb(it) }

            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    InfoRow(Icons.Filled.Category, "Tipo", o.type.label)
                    InfoRow(Icons.Filled.Description, "Descripción", o.description)
                    InfoRow(Icons.Filled.CalendarMonth, "Fechas", "${DateUtils.format(o.eventStart)} al ${DateUtils.format(o.eventEnd)} · ${eventCountdown(o)}")
                    InfoRow(Icons.Filled.Schedule, "Horario", o.schedule)
                    InfoRow(Icons.Filled.Place, "Ubicación", "${o.comuna}, ${o.region}. ${o.address}")
                    InfoRow(Icons.Filled.Groups, "Público objetivo", "${o.targetAudience} · ${o.estimatedAttendees} asistentes estimados")
                    InfoRow(Icons.Filled.AttachMoney, "Costo referencial", formatClp(o.referenceCost))
                    InfoRow(Icons.Filled.Rule, "Condiciones", o.conditions)
                    InfoRow(Icons.Filled.Info, "Fuente / detectada", "${o.source} · ${DateUtils.format(o.detectedDate)}")
                    InfoRow(Icons.Filled.Person, "Responsable", o.owner)
                    InfoRow(Icons.Filled.Notes, "Observaciones", o.notes)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column { Text("Potencial", fontSize = 11.sp); StarsText(o.potential) }
                        Column { Text("Afinidad", fontSize = 11.sp); StarsText(o.affinity) }
                    }
                }
            }

            SectionTitle("Estado de la gestión")
            DropdownField("Cambiar estado", o.status, OpportunityStatus.values().toList(), { it.label }, viewModel::changeStatus, Icons.Filled.Flag)

            // --- Contactos ---
            SectionTitle("Contactos (${s.contacts.size})")
            if (s.contacts.isEmpty()) EmptyState("Sin contactos", "Registra a la persona de la organización con quien gestionar.")
            s.contacts.forEach { c ->
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.fillMaxWidth().padding(14.dp)) {
                        Text(c.name, fontWeight = FontWeight.Bold)
                        Text("${c.role} · ${c.area.label}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${c.email} · ${c.phone}", fontSize = 12.sp)
                        c.lastContact?.let { Text("Último contacto: ${DateUtils.format(it)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                }
            }
            OutlinedButton(onClick = { showContact = true }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.PersonAdd, null); Spacer(Modifier.width(8.dp)); Text("Agregar contacto")
            }

            // --- Seguimiento ---
            SectionTitle("Seguimiento (${s.followUps.size})")
            if (s.followUps.isEmpty()) EmptyState("Sin acciones", "Registra llamadas, reuniones o el envío de propuestas.")
            s.followUps.forEach { f ->
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.fillMaxWidth().padding(14.dp)) {
                        Text("${f.type.label} · ${DateUtils.formatDateTime(f.dateTime)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(f.result, fontSize = 13.sp)
                        if (f.nextDate != null) {
                            Text("Próxima acción (${DateUtils.format(f.nextDate)}): ${f.nextAction}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Text("Por ${f.user}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            OutlinedButton(onClick = { showFollowUp = true }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.AddTask, null); Spacer(Modifier.width(8.dp)); Text("Registrar acción de seguimiento")
            }

            // --- Resultado ---
            SectionTitle("Resultado de la activación")
            s.result?.let { r ->
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Realizada el ${DateUtils.format(r.date)}", fontWeight = FontWeight.Bold)
                        Text("Asistentes: ${r.attendees} · Contactos: ${r.contactsGenerated}", fontSize = 13.sp)
                        Text("Interesados: ${r.interested} · Inscripciones: ${r.enrollments}", fontSize = 13.sp)
                        StarsText(r.evaluation)
                        if (r.teamNotes.isNotBlank()) Text("Equipo: ${r.teamNotes}", fontSize = 12.sp)
                        if (r.learnings.isNotBlank()) Text("Aprendizajes: ${r.learnings}", fontSize = 12.sp)
                    }
                }
            } ?: EmptyState("Aún sin resultado", "Cuando la activación se realice, registra los datos (ficticios) del resultado.")
            Button(onClick = { showResult = true }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Assessment, null); Spacer(Modifier.width(8.dp))
                Text(if (s.result == null) "Registrar resultado" else "Actualizar resultado")
            }

            // --- Historial ---
            SectionTitle("Historial de estados")
            s.history.forEach { h ->
                Text(
                    "${DateUtils.formatDateTime(h.date)} · ${h.from?.label ?: "Creada"} → ${h.to.label} (${h.user})",
                    fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showContact) ContactDialog(
        onDismiss = { showContact = false },
        onSave = { input, area, medium -> viewModel.addContact(input, area, medium) }
    )
    if (showFollowUp) FollowUpDialog(
        onDismiss = { showFollowUp = false },
        onSave = { input, type -> viewModel.addFollowUp(input, type) }
    )
    if (showResult) ResultDialog(
        initial = s.result,
        onDismiss = { showResult = false },
        onSave = { input, eval, learn -> viewModel.saveResult(input, eval, learn) }
    )
    if (showDelete) AlertDialog(
        onDismissRequest = { showDelete = false },
        title = { Text("Eliminar oportunidad") },
        text = { Text("Se eliminarán también sus contactos, seguimientos e historial.") },
        confirmButton = { TextButton(onClick = { showDelete = false; viewModel.delete(onBack) }) { Text("Eliminar") } },
        dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Cancelar") } }
    )
}

@Composable
private fun ContactDialog(
    onDismiss: () -> Unit,
    onSave: (ContactInput, ContactArea, String) -> Map<ContactField, String>
) {
    var input by remember { mutableStateOf(ContactInput()) }
    var area by remember { mutableStateOf(ContactArea.EVENTOS) }
    var medium by remember { mutableStateOf("Correo") }
    var errors by remember { mutableStateOf<Map<ContactField, String>>(emptyMap()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo contacto (ficticio)") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                AppTextField("Nombre *", input.name, { input = input.copy(name = it); errors = errors - ContactField.NAME }, Icons.Filled.Person, error = errors[ContactField.NAME])
                AppTextField("Cargo *", input.role, { input = input.copy(role = it); errors = errors - ContactField.ROLE }, Icons.Filled.Badge, error = errors[ContactField.ROLE])
                DropdownField("Área", area, ContactArea.values().toList(), { it.label }, { area = it }, Icons.Filled.Apartment)
                AppTextField("Correo *", input.email, { input = input.copy(email = it); errors = errors - ContactField.EMAIL }, Icons.Filled.Email, error = errors[ContactField.EMAIL], keyboardType = KeyboardType.Email)
                AppTextField("Teléfono *", input.phone, { input = input.copy(phone = it); errors = errors - ContactField.PHONE }, Icons.Filled.Phone, error = errors[ContactField.PHONE], keyboardType = KeyboardType.Phone, helper = "Ej: +56 9 5555 0000")
                DropdownField("Medio preferente", medium, listOf("Correo", "Teléfono", "Reunión"), { it }, { medium = it }, Icons.Filled.Chat)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                errors = onSave(input, area, medium)
                if (errors.isEmpty()) onDismiss()
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun FollowUpDialog(
    onDismiss: () -> Unit,
    onSave: (FollowUpInput, ActionType) -> Map<FollowUpField, String>
) {
    var input by remember { mutableStateOf(FollowUpInput()) }
    var type by remember { mutableStateOf(ActionType.SEGUIMIENTO) }
    var errors by remember { mutableStateOf<Map<FollowUpField, String>>(emptyMap()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Acción de seguimiento") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DropdownField("Tipo de acción", type, ActionType.values().toList(), { it.label }, { type = it }, Icons.Filled.Flag)
                AppTextField("Resultado *", input.result, { input = input.copy(result = it); errors = errors - FollowUpField.RESULT }, Icons.Filled.Description, error = errors[FollowUpField.RESULT], singleLine = false, minLines = 2)
                AppTextField("Próxima acción", input.nextAction, { input = input.copy(nextAction = it); errors = errors - FollowUpField.NEXT_ACTION }, Icons.Filled.Event, error = errors[FollowUpField.NEXT_ACTION])
                DateField("Fecha próximo seguimiento", input.nextDate, { input = input.copy(nextDate = it); errors = errors - FollowUpField.NEXT_DATE }, error = errors[FollowUpField.NEXT_DATE])
            }
        },
        confirmButton = {
            TextButton(onClick = {
                errors = onSave(input, type)
                if (errors.isEmpty()) onDismiss()
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun ResultDialog(
    initial: ActivationResult?,
    onDismiss: () -> Unit,
    onSave: (ResultInput, Int, String) -> Map<ResultField, String>
) {
    var input by remember {
        mutableStateOf(
            ResultInput(
                date = DateUtils.format(initial?.date ?: System.currentTimeMillis()),
                attendees = initial?.attendees?.toString().orEmpty(),
                contacts = initial?.contactsGenerated?.toString().orEmpty(),
                interested = initial?.interested?.toString().orEmpty(),
                enrollments = initial?.enrollments?.toString().orEmpty(),
                notes = initial?.teamNotes.orEmpty()
            )
        )
    }
    var evaluation by remember { mutableIntStateOf(initial?.evaluation ?: 3) }
    var learnings by remember { mutableStateOf(initial?.learnings.orEmpty()) }
    var errors by remember { mutableStateOf<Map<ResultField, String>>(emptyMap()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Resultado de la activación") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DateField("Fecha de realización *", input.date, { input = input.copy(date = it); errors = errors - ResultField.DATE }, error = errors[ResultField.DATE])
                AppTextField("Asistentes *", input.attendees, { input = input.copy(attendees = it.filter(Char::isDigit)); errors = errors - ResultField.ATTENDEES }, Icons.Filled.Groups, error = errors[ResultField.ATTENDEES], keyboardType = KeyboardType.Number)
                AppTextField("Contactos generados *", input.contacts, { input = input.copy(contacts = it.filter(Char::isDigit)); errors = errors - ResultField.CONTACTS }, Icons.Filled.Contacts, error = errors[ResultField.CONTACTS], keyboardType = KeyboardType.Number)
                AppTextField("Personas interesadas *", input.interested, { input = input.copy(interested = it.filter(Char::isDigit)); errors = errors - ResultField.INTERESTED }, Icons.Filled.ThumbUp, error = errors[ResultField.INTERESTED], keyboardType = KeyboardType.Number)
                AppTextField("Inscripciones *", input.enrollments, { input = input.copy(enrollments = it.filter(Char::isDigit)); errors = errors - ResultField.ENROLLMENTS }, Icons.Filled.HowToReg, error = errors[ResultField.ENROLLMENTS], keyboardType = KeyboardType.Number)
                AppTextField("Observaciones del equipo", input.notes, { input = input.copy(notes = it); errors = errors - ResultField.NOTES }, Icons.Filled.Notes, error = errors[ResultField.NOTES], singleLine = false, minLines = 2)
                RatingRow("Evaluación general", evaluation) { evaluation = it }
                AppTextField("Aprendizajes para futuras activaciones", learnings, { learnings = it }, Icons.Filled.Lightbulb, singleLine = false, minLines = 2)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                errors = onSave(input, evaluation, learnings)
                if (errors.isEmpty()) onDismiss()
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
