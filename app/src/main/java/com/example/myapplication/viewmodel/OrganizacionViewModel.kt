
package com.example.myapplication.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.Organizacion
import com.example.myapplication.repository.OrganizacionRepository
import com.example.myapplication.util.ValidadorFormulario

class OrganizacionViewModel : ViewModel() {

    private val repository = OrganizacionRepository()

    private val _organizaciones = mutableStateListOf<Organizacion>()

    val organizaciones: List<Organizacion>
        get() = _organizaciones

    init {
        _organizaciones.addAll(repository.obtenerTodas())
    }

    fun registrarOrganizacion(
        nombre: String,
        tipoOrganizacion: String,
        rubro: String,
        comuna: String,
        region: String,
        sitioWeb: String = "",
        publicoAsociado: String = "",
        observaciones: String = ""
    ): String {

        val error = ValidadorFormulario.validarOrganizacion(
            nombre = nombre,
            tipoOrganizacion = tipoOrganizacion,
            rubro = rubro,
            comuna = comuna,
            region = region
        )

        if (error != null) {
            return error
        }

        val nuevaOrganizacion = Organizacion(
            id = (_organizaciones.maxOfOrNull { it.id } ?: 0) + 1,
            nombre = nombre.trim(),
            tipoOrganizacion = tipoOrganizacion.trim(),
            rubro = rubro.trim(),
            comuna = comuna.trim(),
            region = region.trim(),
            sitioWeb = sitioWeb.trim(),
            publicoAsociado = publicoAsociado.trim(),
            observaciones = observaciones.trim()
        )

        repository.agregar(nuevaOrganizacion)
        _organizaciones.add(nuevaOrganizacion)

        return "Organización registrada correctamente."
    }

    fun actualizarOrganizacion(
        organizacionActualizada: Organizacion
    ): String {

        val indice = _organizaciones.indexOfFirst {
            it.id == organizacionActualizada.id
        }

        if (indice == -1) {
            return "No se encontró la organización."
        }

        if (!repository.actualizar(organizacionActualizada)) {
            return "No se pudieron guardar los cambios."
        }

        _organizaciones[indice] = organizacionActualizada

        return "Organización actualizada correctamente."
    }

    fun eliminarOrganizacion(id: Int): String {

        val organizacion = _organizaciones.find {
            it.id == id
        } ?: return "No se encontró la organización."

        repository.eliminar(id)
        _organizaciones.remove(organizacion)

        return "Organización eliminada correctamente."
    }

    fun buscarOrganizacion(id: Int): Organizacion? {
        return _organizaciones.find {
            it.id == id
        }
    }
}
