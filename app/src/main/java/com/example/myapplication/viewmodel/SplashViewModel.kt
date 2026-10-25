package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.repository.AuthRepository

/** Decide la pantalla inicial según la sesión recordada. */
class SplashViewModel : ViewModel() {
    private val auth = AuthRepository()

    val ruta: String = auth.restaurarSesion()?.let { auth.rutaInicial(it) } ?: "login"
}
