package com.example.myapplication.validation

import com.example.myapplication.domain.DateUtils

/**
 * Reglas de validación puras (sin Android ni Compose). Devuelven el mensaje de error
 * o null si el valor es válido. Los ViewModels las usan; la UI solo muestra el resultado.
 */
object Validators {
    private val emailRegex = Regex("""^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$""")
    private val phoneRegex = Regex("""^\+?[0-9 ]{8,15}$""")
    private val urlRegex = Regex("""^(https?://)?[A-Za-z0-9.-]+\.[A-Za-z]{2,}(/\S*)?$""")

    fun required(value: String, label: String): String? =
        if (value.isBlank()) "$label es obligatorio" else null

    fun text(value: String, label: String, min: Int = 3, max: Int = 80): String? {
        val v = value.trim()
        return when {
            v.isEmpty() -> "$label es obligatorio"
            v.length < min -> "$label debe tener al menos $min caracteres"
            v.length > max -> "$label no puede superar $max caracteres"
            else -> null
        }
    }

    fun optionalText(value: String, label: String, max: Int): String? =
        if (value.trim().length > max) "$label no puede superar $max caracteres" else null

    fun email(value: String): String? = when {
        value.isBlank() -> "El correo es obligatorio"
        !emailRegex.matches(value.trim()) -> "Correo inválido (ej: nombre@ejemplo.cl)"
        else -> null
    }

    fun phone(value: String): String? = when {
        value.isBlank() -> "El teléfono es obligatorio"
        !phoneRegex.matches(value.trim()) -> "Teléfono inválido (8 a 15 dígitos)"
        else -> null
    }

    fun optionalUrl(value: String): String? =
        if (value.isBlank() || urlRegex.matches(value.trim())) null else "Sitio web inválido (ej: www.ejemplo.cl)"

    /** Fecha dd/MM/yyyy obligatoria. */
    fun date(value: String, label: String): String? = when {
        value.isBlank() -> "$label es obligatoria"
        DateUtils.parse(value) == null -> "$label inválida (use dd/MM/aaaa)"
        else -> null
    }

    fun optionalFutureDate(value: String, label: String, today: Long = DateUtils.startOfDay()): String? {
        if (value.isBlank()) return null
        val parsed = DateUtils.parse(value) ?: return "$label inválida (use dd/MM/aaaa)"
        return if (parsed < today) "$label no puede ser anterior a hoy" else null
    }

    fun dateOrder(start: String, end: String): String? {
        val s = DateUtils.parse(start) ?: return null
        val e = DateUtils.parse(end) ?: return null
        return if (e < s) "La fecha de término no puede ser anterior al inicio" else null
    }

    fun nonNegativeInt(value: String, label: String, max: Int = 1_000_000): String? {
        if (value.isBlank()) return "$label es obligatorio"
        val n = value.trim().toIntOrNull() ?: return "$label debe ser un número entero"
        return when {
            n < 0 -> "$label no puede ser negativo"
            n > max -> "$label es demasiado alto"
            else -> null
        }
    }

    fun username(value: String): String? = text(value, "El usuario", min = 3, max = 30)

    fun password(value: String): String? = when {
        value.isBlank() -> "La contraseña es obligatoria"
        value.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
        else -> null
    }
}
