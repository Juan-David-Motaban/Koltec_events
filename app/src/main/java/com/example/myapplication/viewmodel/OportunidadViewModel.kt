
package com.example.myapplication.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.Oportunidad
import com.example.myapplication.repository.OportunidadRepository
import com.example.myapplication.util.ValidadorFormulario

class OportunidadViewModel : ViewModel() {

    private val repository = OportunidadRepository()

    private val _oportunidades = mutableStateListOf<Oportunidad>()

    val oportunidades: List<Oportunidad>
        get() = _oportunidades

    init {
        _oportunidades.addAll(repository.obtenerTodas())
    }

    fun registrarOportunidad(
        nombreEvento: String,
        organizacion: String,
        fecha: String,
        ubicacion: String,
        tipoEvento: String,
        publicoEsperado: String,
        observaciones: String
    ): String {

        val error = ValidadorFormulario.validarOportunidad(
            nombreEvento = nombreEvento,
            organizacion = organizacion,
            fecha = fecha,
            ubicacion = ubicacion,
            tipoEvento = tipoEvento,
            publicoEsperado = publicoEsperado
        )

        if (error != null) {
            return error
        }

        val cantidad = publicoEsperado.toIntOrNull()
            ?: return "Ingresa una cantidad válida de asistentes."

        val nuevaOportunidad = Oportunidad(
            id = (_oportunidades.maxOfOrNull { it.id } ?: 0) + 1,
            nombreEvento = nombreEvento.trim(),
            organizacion = organizacion.trim(),
            fecha = fecha.trim(),
            ubicacion = ubicacion.trim(),
            tipoEvento = tipoEvento.trim(),
            publicoEsperado = cantidad,
            observaciones = observaciones.trim()
        )

        repository.agregar(nuevaOportunidad)
        _oportunidades.add(nuevaOportunidad)

        return "Oportunidad registrada correctamente."
    }

    fun actualizarOportunidad(
        oportunidadActualizada: Oportunidad
    ): String {

        val indice = _oportunidades.indexOfFirst {
            it.id == oportunidadActualizada.id
        }

        if (indice == -1) {
            return "No se encontró la oportunidad."
        }

        val actualizada = oportunidadActualizada.copy(
            nombreEvento = oportunidadActualizada.nombreEvento.trim(),
            organizacion = oportunidadActualizada.organizacion.trim(),
            ubicacion = oportunidadActualizada.ubicacion.trim(),
            tipoEvento = oportunidadActualizada.tipoEvento.trim(),
            observaciones = oportunidadActualizada.observaciones.trim(),
            contactoNombre = oportunidadActualizada.contactoNombre.trim(),
            contactoArea = oportunidadActualizada.contactoArea.trim(),
            contactoCorreo = oportunidadActualizada.contactoCorreo.trim(),
            contactoTelefono = oportunidadActualizada.contactoTelefono.trim(),
            condicionesParticipacion =
                oportunidadActualizada.condicionesParticipacion.trim(),
            costoEstimado = oportunidadActualizada.costoEstimado.trim(),
            fechaSeguimiento = oportunidadActualizada.fechaSeguimiento.trim(),
            notasSeguimiento = oportunidadActualizada.notasSeguimiento.trim(),
            resultadoActivacion =
                oportunidadActualizada.resultadoActivacion.trim()
        )

        val actualizado = repository.actualizar(actualizada)

        if (!actualizado) {
            return "No se pudieron guardar los cambios."
        }

        _oportunidades[indice] = actualizada

        return "Seguimiento actualizado correctamente."
    }

    fun eliminarOportunidad(id: Int): String {

        val oportunidad = _oportunidades.find {
            it.id == id
        } ?: return "No se encontró la oportunidad."

        repository.eliminar(id)
        _oportunidades.remove(oportunidad)

        return "Oportunidad eliminada correctamente."
    }

    fun buscarOportunidad(id: Int): Oportunidad? {
        return _oportunidades.find {
            it.id == id
        }
    }

    fun cambiarEstado(
        id: Int,
        nuevoEstado: String
    ): String {

        val oportunidad = buscarOportunidad(id)
            ?: return "No se encontró la oportunidad."

        return actualizarOportunidad(
            oportunidad.copy(estado = nuevoEstado)
        )
    }
}
