package com.example.myapplication.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.R

private val BluePrimary = Color(0xFF007AFF)
private val TextDark = Color(0xFF102746)
private val TextGray = Color(0xFF65758B)
private val BorderGray = Color(0xFFE2E8F0)

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    val state = viewModel.uiState
    var showPass by remember { mutableStateOf(false) }

    Scaffold(containerColor = Color.White) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Logo de Koltec_events
            Image(
                painter = painterResource(id = R.drawable.koltec_logo),
                contentDescription = "Koltec events",
                modifier = Modifier
                    .height(130.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Gestiona tus actividades\nempresariales de forma simple\ny eficiente.",
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = TextGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Campo de Correo
            OutlinedTextField(
                value = state.username,
                onValueChange = viewModel::onUsernameChange,
                placeholder = { Text("Correo electrónico", color = TextGray) },
                leadingIcon = {
                    Icon(Icons.Outlined.Email, contentDescription = null, tint = TextDark)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = BorderGray
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Campo de Contraseña
            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = { Text("Contraseña", color = TextGray) },
                leadingIcon = {
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = TextDark)
                },
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = null,
                            tint = TextGray
                        )
                    }
                },
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = BorderGray
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Fila de "Recordar sesión" y "¿Olvidaste tu contraseña?"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = state.recordar,
                        onCheckedChange = viewModel::onRecordarChange,
                        colors = CheckboxDefaults.colors(checkedColor = BluePrimary)
                    )
                    Text(text = "Recordar sesión", fontSize = 13.sp, color = TextDark)
                }

                TextButton(onClick = { /* Acción recuperar contraseña */ }) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        fontSize = 13.sp,
                        color = BluePrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (state.error != null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Iniciar Sesión con Flecha
            Button(
                onClick = {
                    viewModel.submit { ruta ->
                        navController.navigate(ruta) {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Iniciar sesión",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divisor "o"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderGray)
                Text(
                    text = "  o  ",
                    color = TextGray,
                    fontSize = 13.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderGray)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón Continuar con Google
            OutlinedButton(
                onClick = { /* Acción Google Login */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "G ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEA4335)
                    )
                    Text(
                        text = "Continuar con Google",
                        color = TextDark,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Registro
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "¿No tienes una cuenta? ", color = TextGray, fontSize = 14.sp)
                TextButton(onClick = {
                    // Navegamos a la pantalla de registro
                    navController.navigate("register")
                }) {
                    Text(
                        text = "Regístrate",
                        color = BluePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}