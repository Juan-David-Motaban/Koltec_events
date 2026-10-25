package com.example.myapplication.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.ui.login.LoginUiState
import com.example.myapplication.ui.login.LoginViewModel

// Colores principales de Koltec_events
private val KoltecBlue = Color(0xFF0785F9)
private val KoltecNavy = Color(0xFF102746)
private val KoltecGray = Color(0xFF65758B)
private val KoltecBorder = Color(0xFFD7E3F0)
private val KoltecBackground = Color(0xFFFFFFFF)

// Pantalla conectada al ViewModel y a Navigation
@Composable
fun HomeScreen(
    navController: NavController,
    vm: LoginViewModel = viewModel()
) {
    HomeScreenContent(
        state = vm.uiState,
        onUsernameChange = vm::onUsernameChange,
        onPasswordChange = vm::onPasswordChange,
        onLoginClick = {
            vm.submit { user ->
                navController.navigate("DrawerMenu/$user") {
                    popUpTo("login") {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    )
}

// Interfaz visual de inicio de sesión
@Composable
fun HomeScreenContent(
    state: LoginUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    var showPass by remember { mutableStateOf(false) }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = KoltecBlue,
            onPrimary = Color.White,
            background = KoltecBackground,
            surface = Color.White,
            onSurface = KoltecNavy,
            error = Color(0xFFD32F2F)
        )
    ) {
        Scaffold(
            containerColor = KoltecBackground
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(KoltecBackground)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 26.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Spacer(modifier = Modifier.height(12.dp))

                // Logo de la aplicación
                Image(
                    painter = painterResource(id = R.drawable.koltec_logo),
                    contentDescription = "Logo de Koltec_events",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(155.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Gestiona tus actividades\nempresariales de forma simple\ny eficiente.",
                    fontSize = 15.sp,
                    lineHeight = 23.sp,
                    color = KoltecGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(34.dp))

                // Campo de usuario o correo
                OutlinedTextField(
                    value = state.username,
                    onValueChange = onUsernameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Correo electrónico")
                    },
                    placeholder = {
                        Text("Ingresa tu correo")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = null,
                            tint = KoltecNavy
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KoltecBlue,
                        unfocusedBorderColor = KoltecBorder,
                        focusedLabelColor = KoltecBlue,
                        unfocusedLabelColor = KoltecGray,
                        focusedTextColor = KoltecNavy,
                        unfocusedTextColor = KoltecNavy,
                        cursorColor = KoltecBlue
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de contraseña
                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Contraseña")
                    },
                    placeholder = {
                        Text("Ingresa tu contraseña")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = KoltecNavy
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                showPass = !showPass
                            }
                        ) {
                            Icon(
                                imageVector = if (showPass) {
                                    Icons.Outlined.VisibilityOff
                                } else {
                                    Icons.Outlined.Visibility
                                },
                                contentDescription = if (showPass) {
                                    "Ocultar contraseña"
                                } else {
                                    "Mostrar contraseña"
                                },
                                tint = KoltecGray
                            )
                        }
                    },
                    visualTransformation = if (showPass) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KoltecBlue,
                        unfocusedBorderColor = KoltecBorder,
                        focusedLabelColor = KoltecBlue,
                        unfocusedLabelColor = KoltecGray,
                        focusedTextColor = KoltecNavy,
                        unfocusedTextColor = KoltecNavy,
                        cursorColor = KoltecBlue
                    )
                )

                // Mensaje de error del ViewModel
                if (state.error != null) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Botón para iniciar sesión
                Button(
                    onClick = onLoginClick,
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KoltecBlue,
                        contentColor = Color.White,
                        disabledContainerColor = KoltecBlue.copy(
                            alpha = 0.5f
                        )
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "Iniciar sesión     →",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Acceso al registro
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "¿No tienes una cuenta?",
                        color = KoltecGray,
                        fontSize = 14.sp
                    )

                    TextButton(
                        onClick = {
                            // Ruta futura para registro
                        }
                    ) {
                        Text(
                            text = "Regístrate",
                            color = KoltecBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// Vista previa en Android Studio
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreenContent(
        state = LoginUiState(
            username = "admin@koltec.com",
            password = "password123"
        ),
        onUsernameChange = {},
        onPasswordChange = {},
        onLoginClick = {} // En la preview no ejecutamos navegación
    )
}