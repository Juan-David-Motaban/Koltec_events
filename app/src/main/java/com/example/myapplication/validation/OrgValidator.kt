package com.example.myapplication.validation

enum class OrgField { NOMBRE, TIPO, RUBRO, COMUNA, SITIO_WEB, PUBLICO, OBSERVACIONES }

data class OrgInput(
    val nombre: String = "",
    val tipo: String = "Empresa",
    val rubro: String = "",
    val comuna: String = "",
    val sitioWeb: String = "",
    val publico: String = "",
    val observaciones: String = ""
)

object OrgValidator {
    fun validar(campo: OrgField, i: OrgInput): String? = when (campo) {
        OrgField.NOMBRE -> Validators.text(i.nombre, "El nombre", 3, 80)
        OrgField.TIPO -> Validators.required(i.tipo, "El tipo de organización")
        OrgField.RUBRO -> Validators.text(i.rubro, "El rubro", 3, 60)
        OrgField.COMUNA -> Validators.required(i.comuna, "La comuna")
        OrgField.SITIO_WEB -> Validators.optionalUrl(i.sitioWeb)
        OrgField.PUBLICO -> Validators.optionalText(i.publico, "El público asociado", 100)
        OrgField.OBSERVACIONES -> Validators.optionalText(i.observaciones, "Las observaciones", 300)
    }

    fun validarTodo(i: OrgInput): Map<OrgField, String> =
        OrgField.values().mapNotNull { f -> validar(f, i)?.let { f to it } }.toMap()
}
