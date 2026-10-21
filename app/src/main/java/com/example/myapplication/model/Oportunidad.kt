
package com.example.myapplication.model

data class Oportunidad(
    val id: Int = 0,
    val nombreEvento: String,
    val organizacion: String,
    val fecha: String,
    val ubicacion: String,
    val tipoEvento: String,
    val publicoEsperado: Int,
    val estado: String = "Pendiente",
    val observaciones: String = "",
    val contactoNombre: String = "",
    val contactoArea: String = "",
    val contactoCorreo: String = "",
    val contactoTelefono: String = "",
    val propuestaEnviada: Boolean = false,
    val condicionesParticipacion: String = "",
    val costoEstimado: String = "",
    val fechaSeguimiento: String = "",
    val notasSeguimiento: String = "",
    val prioridad: String = "Media",
    val potencialComercial: String = "Medio",
    val resultadoActivacion: String = ""
)
