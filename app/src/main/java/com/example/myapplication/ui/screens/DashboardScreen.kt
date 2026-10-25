package com.example.myapplication.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier

/** Contenedor principal con barra inferior: Inicio (alertas), Oportunidades y Perfil. */
@Composable
fun DashboardScreen(
    onOpen: (Long) -> Unit,
    onNueva: () -> Unit,
    onMiEmpresa: () -> Unit,
    onLogout: () -> Unit
) {
    var tab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(tab == 0, { tab = 0 }, { Icon(Icons.Outlined.Home, "Inicio") }, label = { Text("Inicio") })
                NavigationBarItem(tab == 1, { tab = 1 }, { Icon(Icons.Outlined.Event, "Oportunidades") }, label = { Text("Oportunidades") })
                NavigationBarItem(tab == 2, { tab = 2 }, { Icon(Icons.Outlined.Person, "Perfil") }, label = { Text("Perfil") })
            }
        },
        floatingActionButton = {
            if (tab != 2) {
                FloatingActionButton(onClick = onNueva, containerColor = MaterialTheme.colorScheme.secondary) {
                    Icon(Icons.Filled.Add, contentDescription = "Nueva oportunidad")
                }
            }
        }
    ) { inner ->
        Crossfade(targetState = tab, label = "tabs") { current ->
            val m = Modifier.padding(inner)
            when (current) {
                0 -> InicioTab(onOpen = onOpen, onMiEmpresa = onMiEmpresa, modifier = m)
                1 -> OportunidadesTab(onOpen = onOpen, modifier = m)
                else -> PerfilTab(onMiEmpresa = onMiEmpresa, onLogout = onLogout, modifier = m)
            }
        }
    }
}
