package com.example.myapplication.data.local

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.myapplication.model.*

/** Acceso a datos SQLite. Funciones bloqueantes: los repositorios las llaman desde IO cuando corresponde. */
class AppDao(private val helper: AppDatabase) {

    // ---------- Usuarios ----------
    fun findUser(correo: String): Usuario? =
        helper.readableDatabase.rawQuery(
            "SELECT * FROM usuarios WHERE LOWER(correo) = LOWER(?)", arrayOf(correo.trim())
        ).use { if (it.moveToFirst()) it.toUsuario() else null }

    fun getUsers(): List<Usuario> =
        helper.readableDatabase.rawQuery("SELECT * FROM usuarios ORDER BY nombre", null)
            .use { c -> c.collect { it.toUsuario() } }

    fun insertUser(u: Usuario): Long {
        val v = ContentValues().apply {
            put("nombre", u.nombre); put("correo", u.correo); put("password_hash", u.passwordHash)
            put("tipo", u.tipo.name)
            if (u.empresaId != null) put("empresa_id", u.empresaId) else putNull("empresa_id")
        }
        return helper.writableDatabase.insertOrThrow("usuarios", null, v)
    }

    fun updateUserCompany(correo: String, empresaId: Int?): Int {
        val v = ContentValues().apply {
            if (empresaId != null) put("empresa_id", empresaId) else putNull("empresa_id")
        }
        return helper.writableDatabase.update("usuarios", v, "LOWER(correo) = LOWER(?)", arrayOf(correo.trim()))
    }

    // ---------- Organizaciones ----------
    fun getOrganizations(): List<Organizacion> =
        helper.readableDatabase.rawQuery("SELECT * FROM organizaciones ORDER BY nombre", null)
            .use { c -> c.collect { it.toOrganizacion() } }

    fun getOrganization(id: Int): Organizacion? =
        helper.readableDatabase.rawQuery("SELECT * FROM organizaciones WHERE id = ?", arrayOf(id.toString()))
            .use { if (it.moveToFirst()) it.toOrganizacion() else null }

    fun insertOrganization(o: Organizacion): Long {
        val v = ContentValues().apply {
            put("nombre", o.nombre); put("tipo_organizacion", o.tipoOrganizacion); put("rubro", o.rubro)
            put("comuna", o.comuna); put("region", o.region); put("sitio_web", o.sitioWeb)
            put("publico_asociado", o.publicoAsociado); put("observaciones", o.observaciones)
        }
        return helper.writableDatabase.insertOrThrow("organizaciones", null, v)
    }

    // ---------- Oportunidades ----------
    private val selectOpportunity =
        "SELECT o.*, g.nombre AS org_name FROM opportunities o JOIN organizaciones g ON g.id = o.org_id"

    fun getOpportunities(): List<Opportunity> =
        helper.readableDatabase.rawQuery("$selectOpportunity ORDER BY o.event_start ASC", null)
            .use { c -> c.collect { it.toOpportunity() } }

    fun getOpportunity(id: Long): Opportunity? =
        helper.readableDatabase.rawQuery("$selectOpportunity WHERE o.id = ?", arrayOf(id.toString()))
            .use { if (it.moveToFirst()) it.toOpportunity() else null }

    fun insertOpportunity(o: Opportunity): Long =
        helper.writableDatabase.insertOrThrow("opportunities", null, o.toValues())

    fun updateOpportunity(o: Opportunity) {
        helper.writableDatabase.update("opportunities", o.toValues(), "id = ?", arrayOf(o.id.toString()))
    }

    fun deleteOpportunity(id: Long) {
        helper.writableDatabase.delete("opportunities", "id = ?", arrayOf(id.toString()))
    }

    // ---------- Contactos ----------
    fun getContacts(opportunityId: Long): List<Contact> =
        helper.readableDatabase.rawQuery(
            "SELECT * FROM contacts WHERE opportunity_id = ? ORDER BY name", arrayOf(opportunityId.toString())
        ).use { c -> c.collect { it.toContact() } }

    fun insertContact(c: Contact): Long {
        val v = ContentValues().apply {
            put("opportunity_id", c.opportunityId); put("name", c.name); put("role", c.role)
            put("area", c.area.name); put("email", c.email); put("phone", c.phone)
            put("preferred_medium", c.preferredMedium)
            if (c.lastContact != null) put("last_contact", c.lastContact) else putNull("last_contact")
            put("notes", c.notes)
        }
        return helper.writableDatabase.insertOrThrow("contacts", null, v)
    }

    // ---------- Seguimientos ----------
    fun getFollowUps(opportunityId: Long): List<FollowUp> =
        helper.readableDatabase.rawQuery(
            "SELECT * FROM follow_ups WHERE opportunity_id = ? ORDER BY date_time DESC",
            arrayOf(opportunityId.toString())
        ).use { c -> c.collect { it.toFollowUp() } }

    fun getAllFollowUps(): List<FollowUp> =
        helper.readableDatabase.rawQuery("SELECT * FROM follow_ups ORDER BY date_time DESC", null)
            .use { c -> c.collect { it.toFollowUp() } }

    fun insertFollowUp(f: FollowUp): Long {
        val v = ContentValues().apply {
            put("opportunity_id", f.opportunityId); put("user", f.user); put("type", f.type.name)
            put("date_time", f.dateTime); put("result", f.result); put("next_action", f.nextAction)
            if (f.nextDate != null) put("next_date", f.nextDate) else putNull("next_date")
            put("notes", f.notes)
        }
        return helper.writableDatabase.insertOrThrow("follow_ups", null, v)
    }

    // ---------- Historial de estados ----------
    fun getStatusHistory(opportunityId: Long): List<StatusChange> =
        helper.readableDatabase.rawQuery(
            "SELECT * FROM status_history WHERE opportunity_id = ? ORDER BY date DESC, id DESC",
            arrayOf(opportunityId.toString())
        ).use { c ->
            c.collect {
                StatusChange(
                    id = it.long("id"), opportunityId = it.long("opportunity_id"),
                    from = it.stringOrNull("from_status")?.let { n -> enumFrom(n, OpportunityStatus.EVENTO_IDENTIFICADO) },
                    to = enumFrom(it.string("to_status"), OpportunityStatus.EVENTO_IDENTIFICADO),
                    date = it.long("date"), user = it.string("user")
                )
            }
        }

    fun insertStatusChange(s: StatusChange) {
        val v = ContentValues().apply {
            put("opportunity_id", s.opportunityId)
            if (s.from != null) put("from_status", s.from.name) else putNull("from_status")
            put("to_status", s.to.name); put("date", s.date); put("user", s.user)
        }
        helper.writableDatabase.insertOrThrow("status_history", null, v)
    }

    // ---------- Resultado de activación ----------
    fun getResult(opportunityId: Long): ActivationResult? =
        helper.readableDatabase.rawQuery(
            "SELECT * FROM activation_results WHERE opportunity_id = ?", arrayOf(opportunityId.toString())
        ).use {
            if (!it.moveToFirst()) null else ActivationResult(
                opportunityId = it.long("opportunity_id"), date = it.long("date"),
                attendees = it.int("attendees"), contactsGenerated = it.int("contacts_generated"),
                interested = it.int("interested"), enrollments = it.int("enrollments"),
                teamNotes = it.string("team_notes"), evaluation = it.int("evaluation"),
                learnings = it.string("learnings")
            )
        }

    fun upsertResult(r: ActivationResult) {
        val v = ContentValues().apply {
            put("opportunity_id", r.opportunityId); put("date", r.date); put("attendees", r.attendees)
            put("contacts_generated", r.contactsGenerated); put("interested", r.interested)
            put("enrollments", r.enrollments); put("team_notes", r.teamNotes)
            put("evaluation", r.evaluation); put("learnings", r.learnings)
        }
        helper.writableDatabase.insertWithOnConflict("activation_results", null, v, SQLiteDatabase.CONFLICT_REPLACE)
    }

    // ---------- Mapeos ----------
    private fun Opportunity.toValues() = ContentValues().apply {
        put("name", name); put("type", type.name); put("description", description); put("source", source)
        put("detected_date", detectedDate); put("owner", owner); put("priority", priority.name)
        put("potential", potential); put("affinity", affinity); put("status", status.name)
        put("last_update", lastUpdate); put("notes", notes); put("org_id", orgId)
        put("event_start", eventStart); put("event_end", eventEnd); put("schedule", schedule)
        put("address", address); put("comuna", comuna); put("region", region)
        put("target_audience", targetAudience); put("attendees", estimatedAttendees)
        put("conditions", conditions); put("reference_cost", referenceCost)
        if (photoPath != null) put("photo_path", photoPath) else putNull("photo_path")
    }

    private fun Cursor.toOpportunity() = Opportunity(
        id = long("id"), name = string("name"),
        type = enumFrom(string("type"), OpportunityType.OTRA),
        description = string("description"), source = string("source"),
        detectedDate = long("detected_date"), owner = string("owner"),
        priority = enumFrom(string("priority"), Priority.MEDIA),
        potential = int("potential"), affinity = int("affinity"),
        status = enumFrom(string("status"), OpportunityStatus.EVENTO_IDENTIFICADO),
        lastUpdate = long("last_update"), notes = string("notes"),
        orgId = int("org_id"), orgName = string("org_name"),
        eventStart = long("event_start"), eventEnd = long("event_end"), schedule = string("schedule"),
        address = string("address"), comuna = string("comuna"), region = string("region"),
        targetAudience = string("target_audience"), estimatedAttendees = int("attendees"),
        conditions = string("conditions"), referenceCost = int("reference_cost"),
        photoPath = stringOrNull("photo_path")
    )

    private fun Cursor.toUsuario() = Usuario(
        id = int("id"), nombre = string("nombre"), correo = string("correo"),
        passwordHash = string("password_hash"),
        tipo = enumFrom(string("tipo"), TipoUsuario.NORMAL),
        empresaId = intOrNull("empresa_id")
    )

    private fun Cursor.toOrganizacion() = Organizacion(
        id = int("id"), nombre = string("nombre"), tipoOrganizacion = string("tipo_organizacion"),
        rubro = string("rubro"), comuna = string("comuna"), region = string("region"),
        sitioWeb = string("sitio_web"), publicoAsociado = string("publico_asociado"),
        observaciones = string("observaciones")
    )

    private fun Cursor.toContact() = Contact(
        id = long("id"), opportunityId = long("opportunity_id"), name = string("name"),
        role = string("role"), area = enumFrom(string("area"), ContactArea.EVENTOS),
        email = string("email"), phone = string("phone"), preferredMedium = string("preferred_medium"),
        lastContact = longOrNull("last_contact"), notes = string("notes")
    )

    private fun Cursor.toFollowUp() = FollowUp(
        id = long("id"), opportunityId = long("opportunity_id"), user = string("user"),
        type = enumFrom(string("type"), ActionType.SEGUIMIENTO), dateTime = long("date_time"),
        result = string("result"), nextAction = string("next_action"),
        nextDate = longOrNull("next_date"), notes = string("notes")
    )

    private fun Cursor.string(col: String): String = getString(getColumnIndexOrThrow(col)) ?: ""
    private fun Cursor.stringOrNull(col: String): String? =
        getColumnIndexOrThrow(col).let { if (isNull(it)) null else getString(it) }
    private fun Cursor.long(col: String): Long = getLong(getColumnIndexOrThrow(col))
    private fun Cursor.longOrNull(col: String): Long? =
        getColumnIndexOrThrow(col).let { if (isNull(it)) null else getLong(it) }
    private fun Cursor.int(col: String): Int = getInt(getColumnIndexOrThrow(col))
    private fun Cursor.intOrNull(col: String): Int? =
        getColumnIndexOrThrow(col).let { if (isNull(it)) null else getInt(it) }

    private inline fun <T> Cursor.collect(map: (Cursor) -> T): List<T> {
        val out = ArrayList<T>()
        while (moveToNext()) out.add(map(this))
        return out
    }
}
