package com.example.myapplication.repository

import com.example.myapplication.App
import com.example.myapplication.domain.Hashing
import com.example.myapplication.model.TipoUsuario
import com.example.myapplication.model.Usuario

/** Usuarios guardados en SQLite. */
class UsuarioRepository {
    private val dao get() = App.container.dao

    fun obtenerTodos(): List<Usuario> = dao.getUsers()

    fun buscarPorCorreo(correo: String): Usuario? = dao.findUser(correo)

    fun existeCorreo(correo: String): Boolean = buscarPorCorreo(correo) != null

    fun registrar(nombre: String, correo: String, password: String, tipo: TipoUsuario): Usuario {
        val nuevo = Usuario(
            nombre = nombre.trim(),
            correo = correo.trim().lowercase(),
            passwordHash = Hashing.sha256(password),
            tipo = tipo
        )
        return nuevo.copy(id = dao.insertUser(nuevo).toInt())
    }

    fun autenticar(correo: String, password: String): Usuario? =
        buscarPorCorreo(correo)?.takeIf { it.passwordHash == Hashing.sha256(password) }

    /** Asocia (o desasocia, con null) al usuario a una empresa. */
    fun asignarEmpresa(correo: String, empresaId: Int?): Boolean =
        dao.updateUserCompany(correo, empresaId) > 0

    /** Para registrar un usuario de administración se exige un código interno. */
    fun codigoAdminValido(codigo: String): Boolean = codigo.trim() == CODIGO_ADMIN

    companion object {
        const val CODIGO_ADMIN = "KOLTEC-ADMIN"
    }
}
