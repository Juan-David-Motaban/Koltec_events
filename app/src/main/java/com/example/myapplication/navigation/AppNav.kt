package com.example.myapplication.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.login.LoginScreen
import com.example.myapplication.ui.screens.DashboardScreen
import com.example.myapplication.ui.screens.DetalleOportunidadScreen
import com.example.myapplication.ui.screens.FormularioOportunidadScreen
import com.example.myapplication.ui.screens.MiEmpresaScreen
import com.example.myapplication.ui.screens.OrganizacionScreen
import com.example.myapplication.ui.screens.RegistroUsuarioScreen
import com.example.myapplication.viewmodel.SplashViewModel

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val splash: SplashViewModel = viewModel()
    SetupNavGraph(navController = navController, startDestination = splash.ruta)
}

/** Solo define rutas y navegación: no accede a repositorios ni contiene lógica de negocio. */
@Composable
fun SetupNavGraph(navController: NavHostController, startDestination: String) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(tween(250)) + slideInHorizontally(tween(250)) { it / 10 } },
        exitTransition = { fadeOut(tween(200)) },
        popEnterTransition = { fadeIn(tween(250)) },
        popExitTransition = { fadeOut(tween(200)) }
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("register") {
            RegistroUsuarioScreen(
                onVolver = { navController.popBackStack() },
                onIrALogin = {
                    navController.navigate("login") { popUpTo("login") { inclusive = true } }
                }
            )
        }

        composable(
            route = "MiEmpresa/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { entry ->
            val username = entry.arguments?.getString("username").orEmpty()
            MiEmpresaScreen(
                onRegistrarEmpresa = { navController.navigate("EmpresaNueva/$username") },
                onContinuar = {
                    navController.navigate("Dashboard/$username") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "EmpresaNueva/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { entry ->
            val username = entry.arguments?.getString("username").orEmpty()
            OrganizacionScreen(
                username = username,
                onVolver = { navController.popBackStack() },
                onRegistrada = { navController.popBackStack() }
            )
        }

        composable(
            route = "Dashboard/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { entry ->
            val username = entry.arguments?.getString("username").orEmpty()
            DashboardScreen(
                onOpen = { id -> navController.navigate("Oportunidad/$id") },
                onNueva = { navController.navigate("OportunidadForm/0") },
                onMiEmpresa = { navController.navigate("MiEmpresa/$username") },
                onLogout = {
                    navController.navigate("login") { popUpTo(navController.graph.id) { inclusive = true } }
                }
            )
        }

        composable(
            route = "Oportunidad/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) {
            DetalleOportunidadScreen(
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate("OportunidadForm/$id") }
            )
        }

        composable(
            route = "OportunidadForm/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) {
            FormularioOportunidadScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}
