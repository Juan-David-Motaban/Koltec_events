package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.model.Geo
import com.example.myapplication.model.OrgType
import com.example.myapplication.ui.components.AppTextField
import com.example.myapplication.ui.components.DropdownField
import com.example.myapplication.ui.components.SectionTitle
import com.example.myapplication.ui.theme.VerdeEstado
import com.example.myapplication.validation.OrgField
import com.example.myapplication.viewmodel.OrganizacionViewModel

/** Registro de empresa. Cuando se guarda se informa el id para asociarla al usuario. */
@Composable
fun OrganizacionScreen(
    username: String,
    onVolver: () -> Unit,
    onRegistrada: (Int) -> Unit = {},
    viewModel: OrganizacionViewModel = viewModel()
) {
    val s = viewModel.uiState

    LaunchedEffect(s.exito) {
        if (s.exito) {
            viewModel.ultimaRegistradaId?.let { id ->
                viewModel.vincularConUsuario(username, id)
                onRegistrada(id)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Registrar empresa", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Text("Registra una empresa u organización para asociarla a tus oportunidades.", color = MaterialTheme.colorScheme.onSurfaceVariant)

        SectionTitle("Datos de la organización")
        AppTextField("Nombre de la organización *", s.input.nombre, { viewModel.onCampo(OrgField.NOMBRE, it) }, Icons.Filled.Business, error = s.errores[OrgField.NOMBRE])

        DropdownField(
            label = "Tipo de organización *",
            selected = OrgType.values().firstOrNull { it.label == s.input.tipo },
            options = OrgType.values().toList(),
            optionLabel = { it.label },
            onSelected = { viewModel.onCampo(OrgField.TIPO, it.label) },
            icon = Icons.Filled.Category,
            error = s.errores[OrgField.TIPO]
        )

        AppTextField("Rubro *", s.input.rubro, { viewModel.onCampo(OrgField.RUBRO, it) }, Icons.Filled.Work, error = s.errores[OrgField.RUBRO])

        DropdownField(
            label = "Comuna *",
            selected = s.input.comuna.ifBlank { null },
            options = Geo.comunas.map { it.name },
            optionLabel = { it },
            onSelected = { viewModel.onCampo(OrgField.COMUNA, it) },
            icon = Icons.Filled.Place,
            error = s.errores[OrgField.COMUNA],
            helper = Geo.byName(s.input.comuna)?.let { "Región: ${it.region}" }
        )

        AppTextField("Sitio web (opcional)", s.input.sitioWeb, { viewModel.onCampo(OrgField.SITIO_WEB, it) }, Icons.Filled.Language, error = s.errores[OrgField.SITIO_WEB])
        AppTextField("Público asociado", s.input.publico, { viewModel.onCampo(OrgField.PUBLICO, it) }, Icons.Filled.Groups, error = s.errores[OrgField.PUBLICO])
        AppTextField("Observaciones", s.input.observaciones, { viewModel.onCampo(OrgField.OBSERVACIONES, it) }, Icons.Filled.Description, error = s.errores[OrgField.OBSERVACIONES], singleLine = false, minLines = 2)

        if (s.mensaje != null) {
            Text(
                s.mensaje,
                color = if (s.exito) VerdeEstado else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(onClick = viewModel::registrar, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(15.dp)) {
            Text("GUARDAR EMPRESA", fontWeight = FontWeight.Bold)
        }
        OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(15.dp)) {
            Text("VOLVER")
        }
    }
}
