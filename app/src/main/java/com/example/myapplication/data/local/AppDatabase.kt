package com.example.myapplication.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Base de datos local SQLite. Contiene solo datos ficticios:
 * usuarios, organizaciones, oportunidades, contactos, seguimientos, historial y resultados.
 */
class AppDatabase(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DB_NAME, null, DB_VERSION) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE usuarios(
                id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL,
                correo TEXT NOT NULL UNIQUE, password_hash TEXT NOT NULL,
                tipo TEXT NOT NULL, empresa_id INTEGER)"""
        )
        db.execSQL(
            """CREATE TABLE organizaciones(
                id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL,
                tipo_organizacion TEXT NOT NULL, rubro TEXT NOT NULL, comuna TEXT NOT NULL,
                region TEXT NOT NULL, sitio_web TEXT NOT NULL, publico_asociado TEXT NOT NULL,
                observaciones TEXT NOT NULL)"""
        )
        db.execSQL(
            """CREATE TABLE opportunities(
                id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, type TEXT NOT NULL,
                description TEXT NOT NULL, source TEXT NOT NULL, detected_date INTEGER NOT NULL,
                owner TEXT NOT NULL, priority TEXT NOT NULL, potential INTEGER NOT NULL,
                affinity INTEGER NOT NULL, status TEXT NOT NULL, last_update INTEGER NOT NULL,
                notes TEXT NOT NULL, org_id INTEGER NOT NULL REFERENCES organizaciones(id),
                event_start INTEGER NOT NULL, event_end INTEGER NOT NULL, schedule TEXT NOT NULL,
                address TEXT NOT NULL, comuna TEXT NOT NULL, region TEXT NOT NULL,
                target_audience TEXT NOT NULL, attendees INTEGER NOT NULL, conditions TEXT NOT NULL,
                reference_cost INTEGER NOT NULL, photo_path TEXT)"""
        )
        db.execSQL(
            """CREATE TABLE contacts(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                opportunity_id INTEGER NOT NULL REFERENCES opportunities(id) ON DELETE CASCADE,
                name TEXT NOT NULL, role TEXT NOT NULL, area TEXT NOT NULL, email TEXT NOT NULL,
                phone TEXT NOT NULL, preferred_medium TEXT NOT NULL, last_contact INTEGER,
                notes TEXT NOT NULL)"""
        )
        db.execSQL(
            """CREATE TABLE follow_ups(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                opportunity_id INTEGER NOT NULL REFERENCES opportunities(id) ON DELETE CASCADE,
                user TEXT NOT NULL, type TEXT NOT NULL, date_time INTEGER NOT NULL,
                result TEXT NOT NULL, next_action TEXT NOT NULL, next_date INTEGER,
                notes TEXT NOT NULL)"""
        )
        db.execSQL(
            """CREATE TABLE status_history(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                opportunity_id INTEGER NOT NULL REFERENCES opportunities(id) ON DELETE CASCADE,
                from_status TEXT, to_status TEXT NOT NULL, date INTEGER NOT NULL,
                user TEXT NOT NULL)"""
        )
        db.execSQL(
            """CREATE TABLE activation_results(
                opportunity_id INTEGER PRIMARY KEY REFERENCES opportunities(id) ON DELETE CASCADE,
                date INTEGER NOT NULL, attendees INTEGER NOT NULL, contacts_generated INTEGER NOT NULL,
                interested INTEGER NOT NULL, enrollments INTEGER NOT NULL, team_notes TEXT NOT NULL,
                evaluation INTEGER NOT NULL, learnings TEXT NOT NULL)"""
        )
        SeedData.populate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        listOf(
            "activation_results", "status_history", "follow_ups", "contacts",
            "opportunities", "organizaciones", "usuarios"
        ).forEach { db.execSQL("DROP TABLE IF EXISTS $it") }
        onCreate(db)
    }

    companion object {
        const val DB_NAME = "koltec_events.db"
        const val DB_VERSION = 1
    }
}
