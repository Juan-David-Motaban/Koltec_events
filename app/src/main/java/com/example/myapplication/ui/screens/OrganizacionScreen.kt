
package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.viewmodel.OrganizacionViewModel

@Composable
fun OrganizacionScreen(
    viewModel: OrganizacionViewModel,
    onVolver: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Empresa") }
    var rubro by remember { mutableStateOf("") }
    var comuna by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var sitioWeb by remember { mutableStateOf("") }
    var publico by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Organizaciones",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text("Registra empresas e instituciones para tus oportunidades.")

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de la organización *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = tipo,
            onValueChange = { tipo = it },
            label = { Text("Tipo de organización *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = rubro,
            onValueChange = { rubro = it },
            label = { Text("Rubro *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = comuna,
            onValueChange = { comuna = it },
            label = { Text("Comuna *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = region,
            onValueChange = { region = it },
            label = { Text("Región *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = sitioWeb,
            onValueChange = { sitioWeb = it },
            label = { Text("Sitio web (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = publico,
            onValueChange = { publico = it },
            label = { Text("Público asociado") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = observaciones,
            onValueChange = { observaciones = it },
            label = { Text("Observaciones") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        if (mensaje.isNotBlank()) {
            Text(
                mensaje,
                color = if (mensaje.startsWith("Organización registrada")) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }

        Button(
            onClick = {
                mensaje = viewModel.registrarOrganizacion(
                    nombre = nombre,
                    tipoOrganizacion = tipo,
                    rubro = rubro,
                    comuna = comuna,
                    region = region,
                    sitioWeb = sitioWeb,
                    publicoAsociado = publico,
                    observaciones = observaciones
                )

                if (mensaje == "Organización registrada correctamente.") {
                    nombre = ""
                    tipo = "Empresa"
                    rubro = ""
                    comuna = ""
                    region = ""
                    sitioWeb = ""
                    publico = ""
                    observaciones = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar organización")
        }

        Text(
            "Organizaciones registradas: ${viewModel.organizaciones.size}",
            fontWeight = FontWeight.Bold
        )

        if (viewModel.organizaciones.isEmpty()) {
            Text("Todavía no hay organizaciones registradas.")
        } else {
            viewModel.organizaciones.forEach { organizacion ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            organizacion.nombre,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text("Tipo: ${organizacion.tipoOrganizacion}")
                        Text("Rubro: ${organizacion.rubro}")
                        Text("Ubicación: ${organizacion.comuna}, ${organizacion.region}")

                        if (organizacion.sitioWeb.isNotBlank()) {
                            Text("Web: ${organizacion.sitioWeb}")
                        }

                        if (organizacion.publicoAsociado.isNotBlank()) {
                            Text("Público: ${organizacion.publicoAsociado}")
                        }

                        if (organizacion.observaciones.isNotBlank()) {
                            Text("Observaciones: ${organizacion.observaciones}")
                        }
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio")
        }
    }
}
