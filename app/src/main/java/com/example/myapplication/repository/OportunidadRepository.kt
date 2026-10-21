
package com.example.myapplication.repository

import com.example.myapplication.model.Oportunidad

class OportunidadRepository {

    private val oportunidades = mutableListOf<Oportunidad>()

    fun obtenerTodas(): List<Oportunidad> {
        return oportunidades.toList()
    }

    fun agregar(oportunidad: Oportunidad) {
        oportunidades.add(oportunidad)
    }

    fun actualizar(oportunidadActualizada: Oportunidad): Boolean {
        val indice = oportunidades.indexOfFirst {
            it.id == oportunidadActualizada.id
        }

        if (indice == -1) {
            return false
        }

        oportunidades[indice] = oportunidadActualizada
        return true
    }

    fun eliminar(id: Int): Boolean {
        return oportunidades.removeAll {
            it.id == id
        }
    }

    fun buscarPorId(id: Int): Oportunidad? {
        return oportunidades.find {
            it.id == id
        }
    }
}
