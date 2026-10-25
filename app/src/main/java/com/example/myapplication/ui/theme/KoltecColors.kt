package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.myapplication.model.OpportunityStatus
import com.example.myapplication.model.Priority

// Paleta Koltec Events
val Naranja = Color(0xFFFF713F)
val NaranjaClaro = Color(0xFFFFE2D7)
val Violeta = Color(0xFF7757E8)
val MoradoOscuro = Color(0xFF211653)
val TextoSuave = Color(0xFF6C6B9C)
val Lavanda = Color(0xFFF1EEFF)
val LavandaIcono = Color(0xFFE8E1FF)
val VerdeEstado = Color(0xFF18865B)
val Ambar = Color(0xFFB77900)
val Rojo = Color(0xFFBA1A1A)
val Gris = Color(0xFF6E6E7A)

fun statusColor(s: OpportunityStatus): Color = when (s) {
    OpportunityStatus.EVENTO_IDENTIFICADO, OpportunityStatus.CONTACTO_PENDIENTE -> Naranja
    OpportunityStatus.CONTACTO_REALIZADO, OpportunityStatus.PROPUESTA_ENVIADA,
    OpportunityStatus.EN_ESPERA_RESPUESTA, OpportunityStatus.EN_EVALUACION,
    OpportunityStatus.REQUIERE_SEGUIMIENTO -> Violeta
    OpportunityStatus.INFORMACION_INCOMPLETA -> Ambar
    OpportunityStatus.PARTICIPACION_CONFIRMADA, OpportunityStatus.ACTIVACION_REALIZADA -> VerdeEstado
    OpportunityStatus.ACTIVACION_CANCELADA, OpportunityStatus.DESCARTADA -> Rojo
    OpportunityStatus.CERRADA -> Gris
}

fun priorityColor(p: Priority): Color = when (p) {
    Priority.ALTA -> Rojo
    Priority.MEDIA -> Ambar
    Priority.BAJA -> VerdeEstado
}
