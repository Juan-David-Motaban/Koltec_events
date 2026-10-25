package com.example.myapplication.repository

import com.example.myapplication.App
import com.example.myapplication.model.Organizacion

/** Empresas / organizaciones guardadas en SQLite. */
class OrganizacionRepository {
    private val dao get() = App.container.dao

    fun obtenerTodas(): List<Organizacion> = dao.getOrganizations()

    fun buscarPorId(id: Int): Organizacion? = dao.getOrganization(id)

    /** Guarda la organización y devuelve su id. */
    fun agregar(organizacion: Organizacion): Int = dao.insertOrganization(organizacion).toInt()
}
