package com.example.myapplication.ui.login

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val recordar: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null
)
