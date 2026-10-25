package com.example.myapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.device.QrScanner
import com.example.myapplication.ui.components.QrDialog
import com.example.myapplication.ui.theme.*
import com.example.myapplication.model.Organizacion
import com.example.myapplication.viewmodel.MiEmpresaViewModel


/**
 * Empresa de la cuenta del usuario.
 * Sin empresa: ofrece "Unirme a una empresa" o "Registrar empresa".
 * Con empresa: muestra la empresa y permite salir de ella.
 */
@Composable
fun MiEmpresaScreen(
    onRegistrarEmpresa: () -> Unit,
    onContinuar: () -> Unit,
    viewModel: MiEmpresaViewModel = viewModel()
) {
    val s = viewModel.uiState
    var mostrarLista by remember { mutableStateOf(false) }
    var mostrarQr by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Al volver de "Registrar empresa" se vuelve a leer la información.
    LaunchedEffect(Unit) { viewModel.cargar() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        Text("Mi empresa", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MoradoOscuro)
        Text(
            "Hola, ${s.usuario?.nombre ?: ""}. " +
                if (s.empresaActual == null) "Aún no perteneces a ninguna empresa."
                else "Esta es la empresa asociada a tu cuenta.",
            fontSize = 14.sp, color = TextoSuave
        )

        AnimatedVisibility(visible = s.mensaje != null, enter = fadeIn(), exit = fadeOut()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val color = if (s.mensajeEsError) Rojo else VerdeEstado
                Icon(if (s.mensajeEsError) Icons.Filled.Error else Icons.Filled.CheckCircle, contentDescription = null, tint = color)
                Text(s.mensaje.orEmpty(), color = color, fontWeight = FontWeight.SemiBold)
            }
        }

        if (s.empresaActual != null) {
            EmpresaActualCard(s.empresaActual)

            OutlinedButton(
                onClick = { mostrarQr = true },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(15.dp)
            ) {
                Icon(Icons.Filled.QrCode, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("MOSTRAR QR DE LA EMPRESA", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = viewModel::salir,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(15.dp)
            ) { Text("SALIR DE ESTA EMPRESA", fontWeight = FontWeight.Bold) }
        } else {
            OpcionEmpresa(
                titulo = "Unirme a una empresa",
                descripcion = if (s.empresas.isEmpty()) "Aún no hay empresas registradas."
                else "Elige una de las ${s.empresas.size} empresa(s) ya registradas.",
                icono = Icons.Filled.Groups,
                colorIcono = Violeta,
                fondoIcono = LavandaIcono,
                onClick = { mostrarLista = !mostrarLista }
            )

            OpcionEmpresa(
                titulo = "Escanear QR de empresa",
                descripcion = "Apunta la cámara al QR de una empresa registrada para unirte al instante.",
                icono = Icons.Filled.QrCodeScanner,
                colorIcono = VerdeEstado,
                fondoIcono = Color(0xFFE3F5E9),
                onClick = {
                    QrScanner.scan(context, onResult = viewModel::onQrEscaneado, onError = viewModel::onQrError)
                }
            )

            AnimatedVisibility(
                visible = mostrarLista,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = s.busqueda,
                        onValueChange = viewModel::onBusqueda,
                        label = { Text("Buscar por nombre, rubro o comuna") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (s.empresasFiltradas.isEmpty()) {
                        Text(
                            if (s.empresas.isEmpty()) "No hay empresas todavía. Registra la primera."
                            else "Ninguna empresa coincide con la búsqueda.",
                            color = TextoSuave, fontSize = 13.sp
                        )
                    }

                    s.empresasFiltradas.forEach { empresa ->
                        EmpresaItem(empresa, onUnirse = { viewModel.unirse(empresa) })
                    }
                }
            }

            OpcionEmpresa(
                titulo = "Registrar empresa",
                descripcion = "Da de alta tu empresa; quedará asociada a tu cuenta.",
                icono = Icons.Filled.AddBusiness,
                colorIcono = Naranja,
                fondoIcono = NaranjaClaro,
                onClick = onRegistrarEmpresa
            )
        }

        val qr = s.qrEmpresa
        if (mostrarQr && qr != null) {
            QrDialog(
                titulo = s.empresaActual?.nombre.orEmpty(),
                descripcion = "Otra persona puede escanear este código desde “Mi empresa” para unirse.",
                contenido = qr,
                onDismiss = { mostrarQr = false }
            )
        }

        Spacer(Modifier.weight(1f, fill = false))

        Button(
            onClick = onContinuar,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Violeta)
        ) {
            Text(
                if (s.empresaActual != null) "CONTINUAR AL PANEL" else "OMITIR POR AHORA",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun OpcionEmpresa(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    colorIcono: Color,
    fondoIcono: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(Modifier.size(56.dp), shape = RoundedCornerShape(50), color = fondoIcono) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(28.dp))
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(titulo, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = MoradoOscuro)
                Text(descripcion, fontSize = 13.sp, lineHeight = 18.sp, color = TextoSuave)
            }
        }
    }
}

@Composable
private fun EmpresaItem(empresa: Organizacion, onUnirse: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(empresa.nombre, fontWeight = FontWeight.Bold, color = MoradoOscuro)
                Text("${empresa.rubro} · ${empresa.comuna}, ${empresa.region}", fontSize = 12.sp, color = TextoSuave)
            }
            Button(
                onClick = onUnirse,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Violeta)
            ) { Text("Unirme") }
        }
    }
}

@Composable
private fun EmpresaActualCard(empresa: Organizacion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.Apartment, contentDescription = null, tint = Violeta)
                Text(empresa.nombre, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = MoradoOscuro)
            }
            Text("Tipo: ${empresa.tipoOrganizacion}", color = TextoSuave)
            Text("Rubro: ${empresa.rubro}", color = TextoSuave)
            Text("Ubicación: ${empresa.comuna}, ${empresa.region}", color = TextoSuave)
            if (empresa.sitioWeb.isNotBlank()) Text("Web: ${empresa.sitioWeb}", color = TextoSuave)
        }
    }
}
