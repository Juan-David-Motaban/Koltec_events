package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.components.InfoRow
import com.example.myapplication.viewmodel.PerfilViewModel

/** Pestaña "Perfil": datos de la cuenta, empresa y cierre de sesión. */
@Composable
fun PerfilTab(
    onMiEmpresa: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PerfilViewModel = viewModel()
) {
    val s = viewModel.uiState
    LaunchedEffect(Unit) { viewModel.cargar() }

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Mi perfil", fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoRow(Icons.Filled.Person, "Nombre", s.usuario?.nombre.orEmpty())
                InfoRow(Icons.Filled.Email, "Correo", s.usuario?.correo.orEmpty())
                InfoRow(Icons.Filled.Badge, "Tipo de usuario", s.usuario?.tipo?.etiqueta.orEmpty())
                InfoRow(Icons.Filled.Business, "Empresa", s.empresa ?: "Sin empresa asociada")
            }
        }
        Button(onClick = onMiEmpresa, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(15.dp)) {
            Icon(Icons.Filled.Business, null)
            Spacer(Modifier.width(8.dp))
            Text("MI EMPRESA", fontWeight = FontWeight.Bold)
        }
        OutlinedButton(
            onClick = { viewModel.cerrarSesion(); onLogout() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(15.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
            Spacer(Modifier.width(8.dp))
            Text("CERRAR SESIÓN", fontWeight = FontWeight.Bold)
        }
    }
}
