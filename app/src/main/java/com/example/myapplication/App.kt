package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.local.AppDao
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.SessionStore
import com.example.myapplication.repository.OportunidadRepository

/** Contenedor simple de dependencias (sin librerías externas). */
class AppContainer(app: Application) {
    val dao = AppDao(AppDatabase(app))
    val session = SessionStore(app)
    val oportunidades = OportunidadRepository(dao)
}

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }

    companion object {
        lateinit var container: AppContainer
            private set
    }
}
