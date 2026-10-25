package com.example.myapplication.model

import kotlin.math.cos
import kotlin.math.sqrt

data class Comuna(val name: String, val region: String, val lat: Double, val lon: Double)

/**
 * Comunas de referencia. Se usa solo la ubicación general (comuna / región),
 * nunca domicilios ni recorridos de personas, tal como pide el caso.
 */
object Geo {
    val comunas = listOf(
        Comuna("Antofagasta", "Antofagasta", -23.65, -70.40),
        Comuna("La Serena", "Coquimbo", -29.90, -71.25),
        Comuna("Valparaíso", "Valparaíso", -33.05, -71.62),
        Comuna("Viña del Mar", "Valparaíso", -33.02, -71.55),
        Comuna("Santiago", "Metropolitana", -33.45, -70.66),
        Comuna("Providencia", "Metropolitana", -33.43, -70.61),
        Comuna("Maipú", "Metropolitana", -33.51, -70.76),
        Comuna("Puente Alto", "Metropolitana", -33.61, -70.58),
        Comuna("Rancagua", "O'Higgins", -34.17, -70.74),
        Comuna("Concepción", "Biobío", -36.83, -73.05),
        Comuna("Temuco", "La Araucanía", -38.74, -72.60),
        Comuna("Puerto Montt", "Los Lagos", -41.47, -72.94)
    )

    val regions: List<String> get() = comunas.map { it.region }.distinct()

    fun byName(name: String): Comuna? = comunas.firstOrNull { it.name == name }

    /** Comuna de referencia más cercana a unas coordenadas (distancia plana aproximada). */
    fun nearest(lat: Double, lon: Double): Comuna {
        fun dist(c: Comuna): Double {
            val dx = (c.lon - lon) * cos(Math.toRadians(lat))
            val dy = c.lat - lat
            return sqrt(dx * dx + dy * dy)
        }
        return comunas.minByOrNull { dist(it) } ?: comunas.first()
    }
}
