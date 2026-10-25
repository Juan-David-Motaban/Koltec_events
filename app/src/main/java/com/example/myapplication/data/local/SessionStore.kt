package com.example.myapplication.data.local

import android.content.Context

/** Control de sesión: usuario activo en memoria y, si pidió "Recordar sesión", también guardado. */
class SessionStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("session", Context.MODE_PRIVATE)

    var currentUser: String? = null
        private set

    fun login(correo: String, remember: Boolean) {
        currentUser = correo
        prefs.edit().apply { if (remember) putString(KEY, correo) else remove(KEY) }.apply()
    }

    /** Devuelve el usuario recordado (o null) y lo deja como sesión activa. */
    fun restore(): String? {
        currentUser = prefs.getString(KEY, null)
        return currentUser
    }

    fun logout() {
        currentUser = null
        prefs.edit().remove(KEY).apply()
    }

    private companion object {
        const val KEY = "correo"
    }
}
