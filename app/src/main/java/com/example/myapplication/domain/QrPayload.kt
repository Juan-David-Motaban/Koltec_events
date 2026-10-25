package com.example.myapplication.domain

/** Contenido que puede traer un QR de Koltec Events. */
sealed interface QrContent {
    data class Empresa(val id: Int) : QrContent
    data class Oportunidad(val id: Long) : QrContent
}

/**
 * Formato de los QR de la app (texto plano, sin dependencias de Android):
 *  - empresa:      KOLTEC:EMP:<id>
 *  - oportunidad:  KOLTEC:OPP:<id>
 */
object QrPayload {
    private const val PREFIJO = "KOLTEC"
    private const val EMPRESA = "EMP"
    private const val OPORTUNIDAD = "OPP"

    fun empresa(id: Int): String = "$PREFIJO:$EMPRESA:$id"
    fun oportunidad(id: Long): String = "$PREFIJO:$OPORTUNIDAD:$id"

    /** Devuelve el contenido del QR o null si no es un QR válido de Koltec Events. */
    fun parse(raw: String?): QrContent? {
        val partes = raw?.trim()?.split(":") ?: return null
        if (partes.size != 3 || !partes[0].equals(PREFIJO, ignoreCase = true)) return null
        val id = partes[2].toLongOrNull()?.takeIf { it > 0 } ?: return null
        return when (partes[1].uppercase()) {
            EMPRESA -> if (id <= Int.MAX_VALUE) QrContent.Empresa(id.toInt()) else null
            OPORTUNIDAD -> QrContent.Oportunidad(id)
            else -> null
        }
    }
}
