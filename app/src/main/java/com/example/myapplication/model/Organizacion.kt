
package com.example.myapplication.model

data class Organizacion(
    val id: Int = 0,
    val nombre: String,
    val tipoOrganizacion: String,
    val rubro: String,
    val comuna: String,
    val region: String,
    val sitioWeb: String = "",
    val publicoAsociado: String = "",
    val observaciones: String = ""
)
