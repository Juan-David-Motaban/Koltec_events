package com.example.myapplication.model

enum class TipoUsuario(val etiqueta: String, val descripcion: String) {
    NORMAL("Usuario normal", "Registra y consulta oportunidades de su equipo."),
    ADMINISTRADOR("Usuario administración", "Gestiona usuarios, empresas y todas las oportunidades.")
}

data class Usuario(
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val passwordHash: String,
    val tipo: TipoUsuario = TipoUsuario.NORMAL,
    val empresaId: Int? = null
)
