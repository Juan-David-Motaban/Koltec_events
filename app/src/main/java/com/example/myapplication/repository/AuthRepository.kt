package com.example.myapplication.repository

import com.example.myapplication.App
import com.example.myapplication.model.Usuario

class AuthRepository(
    private val usuarios: UsuarioRepository = UsuarioRepository()
) {
    private val session get() = App.container.session

    /** Valida las credenciales y abre la sesión. */
    fun login(correo: String, password: String, recordar: Boolean): Usuario? {
        val u = usuarios.autenticar(correo.trim(), password) ?: return null
        session.login(u.correo, recordar)
        return u
    }

    fun logout() = session.logout()

    /** Sesión recordada de una ejecución anterior. */
    fun restaurarSesion(): Usuario? = session.restore()?.let { usuarios.buscarPorCorreo(it) }

    fun usuarioActual(): Usuario? = session.currentUser?.let { usuarios.buscarPorCorreo(it) }

    fun correoActual(): String = session.currentUser.orEmpty()

    /** Sin empresa se pasa primero por "Mi empresa"; con empresa, directo al panel. */
    fun rutaInicial(u: Usuario): String =
        if (u.empresaId == null) "MiEmpresa/${u.correo}" else "Dashboard/${u.correo}"
}
