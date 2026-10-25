package com.example.myapplication.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.device.LocationHelper
import com.example.myapplication.device.PhotoStorage
import com.example.myapplication.model.*
import com.example.myapplication.ui.components.*
import com.example.myapplication.validation.FormField
import com.example.myapplication.viewmodel.FormularioOportunidadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioOportunidadScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: FormularioOportunidadViewModel = viewModel()
) {
    val s by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(s.savedId) { if (s.savedId != null) onSaved() }
    LaunchedEffect(s.message) {
        s.message?.let {
            snackbar.showSnackbar(it)
            viewModel.onMessageShown()
        }
    }

    // Recurso nativo 1: cámara (app de cámara del sistema, sin permiso CAMERA).
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val path = PhotoStorage.save(context, bitmap)
            if (path != null) viewModel.onPhotoCaptured(path)
            else viewModel.onLocationUnavailable("No se pudo guardar la foto")
        }
    }

    // Recurso nativo 2: ubicación aproximada, con permiso en tiempo de ejecución y uso opcional.
    fun usarUbicacion() {
        val pos = LocationHelper.lastKnown(context)
        if (pos != null) viewModel.onLocationDetected(pos.first, pos.second)
        else viewModel.onLocationUnavailable("No hay ubicación disponible. Elige la comuna manualmente.")
    }
    val locationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) usarUbicacion()
        else viewModel.onLocationUnavailable("Permiso de ubicación denegado. Elige la comuna manualmente.")
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text(if (s.isEdit) "Editar oportunidad" else "Nueva oportunidad") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier.padding(inner).fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SectionTitle("Datos generales")
            AppTextField("Nombre de la oportunidad *", s.input.name, { viewModel.onFieldChange(FormField.NAME, it) }, Icons.Filled.Event, error = s.errors[FormField.NAME])
            DropdownField("Tipo de oportunidad", s.type, OpportunityType.values().toList(), { it.label }, viewModel::onType, Icons.Filled.Category)
            AppTextField("Descripción *", s.input.description, { viewModel.onFieldChange(FormField.DESCRIPTION, it) }, Icons.Filled.Description, error = s.errors[FormField.DESCRIPTION], singleLine = false, minLines = 2)
            AppTextField("Fuente de información *", s.input.source, { viewModel.onFieldChange(FormField.SOURCE, it) }, Icons.Filled.Info, error = s.errors[FormField.SOURCE], helper = "Ej: sitio web, red social, contacto directo")
            DateField("Fecha de detección *", s.input.detectedDate, { viewModel.onFieldChange(FormField.DETECTED_DATE, it) }, error = s.errors[FormField.DETECTED_DATE])

            SectionTitle("Gestión")
            DropdownField("Prioridad", s.priority, Priority.values().toList(), { it.label }, viewModel::onPriority, Icons.Filled.Flag)
            DropdownField("Estado", s.status, OpportunityStatus.values().toList(), { it.label }, viewModel::onStatus, Icons.Filled.Timeline)
            DropdownField(
                "Responsable interno", s.owners.firstOrNull { it.correo == s.owner }, s.owners,
                { it.nombre }, { viewModel.onOwner(it.correo) }, Icons.Filled.Person
            )
            RatingRow("Potencial comercial estimado", s.potential, viewModel::onPotential)
            RatingRow("Afinidad con el público objetivo", s.affinity, viewModel::onAffinity)

            SectionTitle("Organización responsable")
            DropdownField(
                "Organización *", s.orgs.firstOrNull { it.id == s.orgId }, s.orgs,
                { it.nombre }, { viewModel.onOrg(it.id) }, Icons.Filled.Business, error = s.orgError
            )

            SectionTitle("Evento")
            DateField("Fecha de inicio *", s.input.eventStart, { viewModel.onFieldChange(FormField.EVENT_START, it) }, error = s.errors[FormField.EVENT_START])
            DateField("Fecha de término *", s.input.eventEnd, { viewModel.onFieldChange(FormField.EVENT_END, it) }, error = s.errors[FormField.EVENT_END])
            AppTextField("Horario *", s.input.schedule, { viewModel.onFieldChange(FormField.SCHEDULE, it) }, Icons.Filled.Schedule, error = s.errors[FormField.SCHEDULE], helper = "Ej: 10:00 a 18:00")

            DropdownField(
                "Comuna *", s.input.comuna.ifBlank { null }, Geo.comunas.map { it.name }, { it },
                { viewModel.onComuna(it) }, Icons.Filled.Place, error = s.errors[FormField.COMUNA],
                helper = if (s.region.isNotBlank()) "Región: ${s.region}" else null
            )
            OutlinedButton(
                onClick = {
                    val ok = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    if (ok) usarUbicacion() else locationPermission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.MyLocation, null)
                Spacer(Modifier.width(8.dp))
                Text("Sugerir comuna con mi ubicación (opcional)")
            }

            AppTextField("Dirección o ubicación general", s.input.address, { viewModel.onFieldChange(FormField.ADDRESS, it) }, Icons.Filled.LocationOn, error = s.errors[FormField.ADDRESS])
            AppTextField("Público objetivo *", s.input.targetAudience, { viewModel.onFieldChange(FormField.TARGET, it) }, Icons.Filled.Groups, error = s.errors[FormField.TARGET])
            AppTextField("Cantidad estimada de asistentes *", s.input.attendees, { viewModel.onFieldChange(FormField.ATTENDEES, it) }, Icons.Filled.People, error = s.errors[FormField.ATTENDEES], keyboardType = KeyboardType.Number)
            AppTextField("Costo referencial (ficticio, CLP) *", s.input.referenceCost, { viewModel.onFieldChange(FormField.COST, it) }, Icons.Filled.AttachMoney, error = s.errors[FormField.COST], keyboardType = KeyboardType.Number)
            AppTextField("Condiciones de participación", s.input.conditions, { viewModel.onFieldChange(FormField.CONDITIONS, it) }, Icons.Filled.Rule, error = s.errors[FormField.CONDITIONS], singleLine = false, minLines = 2)
            AppTextField("Observaciones", s.input.notes, { viewModel.onFieldChange(FormField.NOTES, it) }, Icons.Filled.Notes, error = s.errors[FormField.NOTES], singleLine = false, minLines = 2)

            SectionTitle("Foto del lugar o afiche (opcional)")
            s.photoPath?.let { PhotoThumb(it) }
            OutlinedButton(onClick = { camera.launch(null) }, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.PhotoCamera, null)
                Spacer(Modifier.width(8.dp))
                Text(if (s.photoPath == null) "Tomar foto" else "Tomar otra foto")
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = viewModel::save,
                enabled = !s.saving,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(15.dp)
            ) {
                if (s.saving) CircularProgressIndicator(Modifier.size(22.dp), color = MaterialTheme.colorScheme.onPrimary)
                else {
                    Icon(Icons.Filled.Save, null)
                    Spacer(Modifier.width(8.dp))
                    Text("GUARDAR OPORTUNIDAD", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
