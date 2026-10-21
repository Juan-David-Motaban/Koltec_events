
package com.example.myapplication.repository

import com.example.myapplication.model.Organizacion

class OrganizacionRepository {

    private val organizaciones = mutableListOf<Organizacion>()

    fun obtenerTodas(): List<Organizacion> {
        return organizaciones.toList()
    }

    fun agregar(organizacion: Organizacion) {
        organizaciones.add(organizacion)
    }

    fun actualizar(organizacionActualizada: Organizacion): Boolean {
        val indice = organizaciones.indexOfFirst {
            it.id == organizacionActualizada.id
        }

        if (indice == -1) {
            return false
        }

        organizaciones[indice] = organizacionActualizada
        return true
    }

    fun eliminar(id: Int): Boolean {
        return organizaciones.removeAll {
            it.id == id
        }
    }

    fun buscarPorId(id: Int): Organizacion? {
        return organizaciones.find {
            it.id == id
        }
    }
}
