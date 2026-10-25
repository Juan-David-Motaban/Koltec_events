package com.example.myapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.*
import com.example.myapplication.model.TipoUsuario
import com.example.myapplication.viewmodel.RegistroUsuarioViewModel


@Composable
fun RegistroUsuarioScreen(
    onVolver: () -> Unit,
    onIrALogin: () -> Unit,
    viewModel: RegistroUsuarioViewModel = viewModel()
) {
    val s = viewModel.uiState
    var verPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registrar usuario", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MoradoOscuro)
        Text("Elige el tipo de cuenta y completa tus datos.", fontSize = 14.sp, color = TextoSuave)

        Text("TIPO DE USUARIO", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp, color = Violeta)

        TipoUsuario.values().forEach { tipo ->
            OpcionTipo(
                tipo = tipo,
                seleccionado = s.tipo == tipo,
                icono = if (tipo == TipoUsuario.NORMAL) Icons.Outlined.Person else Icons.Outlined.AdminPanelSettings,
                onClick = { viewModel.onTipo(tipo) }
            )
        }

        CampoRegistro("Nombre completo", s.nombre, viewModel::onNombre, Icons.Outlined.Badge, s.errores["nombre"])
        CampoRegistro(
            "Correo electrónico", s.correo, viewModel::onCorreo, Icons.Outlined.Email,
            s.errores["correo"], KeyboardType.Email
        )
        CampoRegistro(
            "Contraseña", s.password, viewModel::onPassword, Icons.Outlined.Lock, s.errores["password"],
            KeyboardType.Password,
            if (verPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailing = {
                IconButton(onClick = { verPassword = !verPassword }) {
                    Icon(
                        if (verPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (verPassword) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            }
        )
        CampoRegistro(
            "Repetir contraseña", s.confirmacion, viewModel::onConfirmacion, Icons.Outlined.Lock,
            s.errores["confirmacion"], KeyboardType.Password,
            if (verPassword) VisualTransformation.None else PasswordVisualTransformation()
        )

        AnimatedVisibility(
            visible = s.tipo == TipoUsuario.ADMINISTRADOR,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            CampoRegistro(
                "Código de administración", s.codigoAdmin, viewModel::onCodigoAdmin,
                Icons.Outlined.AdminPanelSettings, s.errores["codigoAdmin"]
            )
        }

        AnimatedVisibility(visible = s.mensaje != null, enter = fadeIn(), exit = fadeOut()) {
            val ok = s.registrado
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    if (ok) Icons.Filled.CheckCircle else Icons.Filled.Error,
                    contentDescription = null,
                    tint = if (ok) VerdeEstado else MaterialTheme.colorScheme.error
                )
                Text(
                    s.mensaje.orEmpty(),
                    color = if (ok) VerdeEstado else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (s.registrado) {
            Button(
                onClick = onIrALogin,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Violeta)
            ) { Text("IR A INICIAR SESIÓN", fontWeight = FontWeight.Bold) }
        } else {
            Button(
                onClick = viewModel::registrar,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Violeta)
            ) { Text("CREAR CUENTA", fontWeight = FontWeight.Bold) }
        }

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(15.dp)
        ) { Text("VOLVER", fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun OpcionTipo(tipo: TipoUsuario, seleccionado: Boolean, icono: ImageVector, onClick: () -> Unit) {
    val borde = if (seleccionado) Violeta else Color(0xFFD9D2F5)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .border(if (seleccionado) 2.dp else 1.dp, borde, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (seleccionado) Color(0xFFF1EEFF) else Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icono, contentDescription = null, tint = Violeta)
            Column(Modifier.weight(1f)) {
                Text(tipo.etiqueta, fontWeight = FontWeight.Bold, color = MoradoOscuro)
                Text(tipo.descripcion, fontSize = 12.sp, color = TextoSuave)
            }
            RadioButton(selected = seleccionado, onClick = onClick)
        }
    }
}

@Composable
private fun CampoRegistro(
    etiqueta: String,
    valor: String,
    alCambiar: (String) -> Unit,
    icono: ImageVector,
    error: String? = null,
    teclado: KeyboardType = KeyboardType.Text,
    transformacion: VisualTransformation = VisualTransformation.None,
    trailing: @Composable (() -> Unit)? = null
) {
    val iconoError: @Composable () -> Unit = {
        Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
    }
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, contentDescription = null) },
        trailingIcon = trailing ?: if (error != null) iconoError else null,
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        singleLine = true,
        visualTransformation = transformacion,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    )
}
