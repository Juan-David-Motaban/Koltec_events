package com.example.myapplication.domain

import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtils {
    const val DAY_MS = 86_400_000L
    private val locale = Locale("es", "CL")

    fun startOfDay(millis: Long = System.currentTimeMillis()): Long {
        val c = Calendar.getInstance()
        c.timeInMillis = millis
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    fun addDays(base: Long, days: Int): Long {
        val c = Calendar.getInstance()
        c.timeInMillis = base
        c.add(Calendar.DAY_OF_YEAR, days)
        return c.timeInMillis
    }

    /** Días enteros entre dos fechas (inicio de día). Positivo si [to] es posterior. */
    fun daysBetween(from: Long, to: Long): Int =
        Math.round((startOfDay(to) - startOfDay(from)).toDouble() / DAY_MS).toInt()

    fun format(millis: Long): String = SimpleDateFormat("dd/MM/yyyy", locale).format(millis)

    fun formatDateTime(millis: Long): String =
        SimpleDateFormat("dd/MM/yyyy HH:mm", locale).format(millis)

    /** Interpreta dd/MM/yyyy de forma estricta. Devuelve null si el texto no es una fecha válida. */
    fun parse(text: String): Long? {
        val trimmed = text.trim()
        if (!Regex("""\d{2}/\d{2}/\d{4}""").matches(trimmed)) return null
        return try {
            val f = SimpleDateFormat("dd/MM/yyyy", locale)
            f.isLenient = false
            f.parse(trimmed)?.time?.let { startOfDay(it) }
        } catch (e: Exception) {
            null
        }
    }
}

object Hashing {
    fun sha256(text: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(text.toByteArray())
            .joinToString("") { "%02x".format(it) }
}

fun formatClp(value: Int): String {
    val s = value.toString().reversed().chunked(3).joinToString(".").reversed()
    return "$$s"
}
