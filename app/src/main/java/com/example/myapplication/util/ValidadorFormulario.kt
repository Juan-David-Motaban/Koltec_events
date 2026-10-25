
package com.example.myapplication.util

import java.text.SimpleDateFormat
import java.util.Locale

object ValidadorFormulario {

    fun erroresOportunidad(
        nombreEvento: String,
        organizacion: String,
        fecha: String,
        ubicacion: String,
        tipoEvento: String,
        publicoEsperado: String
    ): Map<String, String> {

        val errores = mutableMapOf<String, String>()

        if (nombreEvento.isBlank()) {
            errores["nombreEvento"] =
                "Ingresa el nombre del evento."
        }

        if (organizacion.isBlank()) {
            errores["organizacion"] =
                "Ingresa la organización."
        }

        if (fecha.isBlank()) {
            errores["fecha"] =
                "Selecciona la fecha del evento."
        } else if (!fechaValida(fecha)) {
            errores["fecha"] =
                "Ingresa una fecha válida en formato dd/MM/yyyy."
        }

        if (ubicacion.isBlank()) {
            errores["ubicacion"] =
                "Ingresa la ubicación."
        }

        if (tipoEvento.isBlank()) {
            errores["tipoEvento"] =
                "Selecciona el tipo de evento."
        }

        val cantidad = publicoEsperado.toIntOrNull()

        if (cantidad == null || cantidad <= 0) {
            errores["publicoEsperado"] =
                "Ingresa una cantidad mayor que cero."
        }

        return errores
    }

    fun validarOportunidad(
        nombreEvento: String,
        organizacion: String,
        fecha: String,
        ubicacion: String,
        tipoEvento: String,
        publicoEsperado: String
    ): String? {

        return erroresOportunidad(
            nombreEvento = nombreEvento,
            organizacion = organizacion,
            fecha = fecha,
            ubicacion = ubicacion,
            tipoEvento = tipoEvento,
            publicoEsperado = publicoEsperado
        ).values.firstOrNull()
    }

    fun erroresUsuario(
        nombre: String,
        correo: String,
        password: String,
        confirmacion: String,
        correoExiste: Boolean,
        esAdministrador: Boolean,
        codigoAdminValido: Boolean
    ): Map<String, String> {

        val errores = mutableMapOf<String, String>()

        if (nombre.isBlank()) {
            errores["nombre"] = "Ingresa tu nombre."
        } else if (nombre.trim().length < 3) {
            errores["nombre"] = "El nombre debe tener al menos 3 caracteres."
        }

        if (correo.isBlank()) {
            errores["correo"] = "Ingresa tu correo electrónico."
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()) {
            errores["correo"] = "Ingresa un correo válido (ej: nombre@empresa.cl)."
        } else if (correoExiste) {
            errores["correo"] = "Ya existe un usuario con ese correo."
        }

        if (password.length < 6) {
            errores["password"] = "La contraseña debe tener al menos 6 caracteres."
        } else if (password.none { it.isDigit() } || password.none { it.isLetter() }) {
            errores["password"] = "Debe incluir letras y números."
        }

        if (confirmacion.isBlank()) {
            errores["confirmacion"] = "Repite la contraseña."
        } else if (confirmacion != password) {
            errores["confirmacion"] = "Las contraseñas no coinciden."
        }

        if (esAdministrador && !codigoAdminValido) {
            errores["codigoAdmin"] = "Código de administración incorrecto."
        }

        return errores
    }

    fun validarOrganizacion(
        nombre: String,
        tipoOrganizacion: String,
        rubro: String,
        comuna: String,
        region: String
    ): String? {

        if (nombre.isBlank()) {
            return "Ingresa el nombre de la organización."
        }

        if (tipoOrganizacion.isBlank()) {
            return "Selecciona el tipo de organización."
        }

        if (rubro.isBlank()) {
            return "Ingresa el rubro de la organización."
        }

        if (comuna.isBlank()) {
            return "Ingresa la comuna."
        }

        if (region.isBlank()) {
            return "Selecciona la región."
        }

        return null
    }

    fun validarSeguimiento(
        correo: String,
        costoEstimado: String
    ): String? {

        if (
            correo.isNotBlank() &&
            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(correo)
                .matches()
        ) {
            return "Ingresa un correo electrónico válido."
        }

        if (costoEstimado.isNotBlank()) {
            val costo = costoEstimado
                .replace("$", "")
                .replace(".", "")
                .replace(",", ".")
                .trim()
                .toDoubleOrNull()

            if (costo == null || costo < 0) {
                return "Ingresa un costo válido, igual o mayor que cero."
            }
        }

        return null
    }

    private fun fechaValida(fecha: String): Boolean {
        return try {
            val formato = SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.ROOT
            )

            formato.isLenient = false

            val fechaParseada = formato.parse(fecha)

            fechaParseada != null &&
                    formato.format(fechaParseada) == fecha

        } catch (_: Exception) {
            false
        }
    }
}
