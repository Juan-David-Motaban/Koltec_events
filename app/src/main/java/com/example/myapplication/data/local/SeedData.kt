package com.example.myapplication.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.myapplication.domain.DateUtils
import com.example.myapplication.domain.Hashing
import com.example.myapplication.model.*

/** Datos de demostración 100 % ficticios (nombres, correos, teléfonos, costos y resultados). */
object SeedData {
    const val DEMO_PASSWORD = "Demo1234"
    const val ADMIN_CORREO = "admin"
    const val ADMIN_PASSWORD = "123"

    fun populate(db: SQLiteDatabase) {
        val today = DateUtils.startOfDay()
        fun day(offset: Int) = DateUtils.addDays(today, offset)

        // ---- Organizaciones ficticias (ids 1..6) ----
        data class Org(val nombre: String, val tipo: String, val rubro: String, val comuna: String)
        val orgs = listOf(
            Org("Corporación Andina Demo", "Empresa", "Servicios", "Santiago"),
            Org("Municipalidad Ejemplo", "Municipalidad", "Sector público", "Puente Alto"),
            Org("Empresa Demo Sur", "Empresa", "Retail", "Concepción"),
            Org("Instituto Ficticio del Norte", "Institución privada", "Educación", "Antofagasta"),
            Org("Centro Comercial Demo Plaza", "Centro comercial", "Comercio", "Viña del Mar"),
            Org("Asociación Gremial Demo", "Asociación", "Gremial", "Maipú")
        )
        orgs.forEach { o ->
            val g = Geo.byName(o.comuna)!!
            db.insert("organizaciones", null, ContentValues().apply {
                put("nombre", o.nombre); put("tipo_organizacion", o.tipo); put("rubro", o.rubro)
                put("comuna", g.name); put("region", g.region)
                put("sitio_web", "www.ejemplo-demo.cl"); put("publico_asociado", "Trabajadores y familias")
                put("observaciones", "Dato ficticio")
            })
        }

        // ---- Usuarios ficticios ----
        fun user(nombre: String, correo: String, pass: String, tipo: TipoUsuario, empresa: Int?) {
            db.insert("usuarios", null, ContentValues().apply {
                put("nombre", nombre); put("correo", correo); put("password_hash", Hashing.sha256(pass))
                put("tipo", tipo.name)
                if (empresa != null) put("empresa_id", empresa) else putNull("empresa_id")
            })
        }
        user("Administrador", ADMIN_CORREO, ADMIN_PASSWORD, TipoUsuario.ADMINISTRADOR, null)
        user("Ejecutivo Demo", "ejecutivo@demo.cl", DEMO_PASSWORD, TipoUsuario.NORMAL, 1)
        user("Ejecutiva Demo Dos", "ejecutiva2@demo.cl", DEMO_PASSWORD, TipoUsuario.NORMAL, 3)

        // ---- Oportunidades ----
        data class Seed(
            val name: String, val type: OpportunityType, val desc: String, val source: String,
            val detectedAgo: Int, val owner: String, val priority: Priority, val potential: Int,
            val affinity: Int, val status: OpportunityStatus, val orgId: Int,
            val startIn: Int, val days: Int, val comuna: String, val audience: String,
            val attendees: Int, val cost: Int, val conditions: String
        )

        val e1 = "ejecutivo@demo.cl"
        val e2 = "ejecutiva2@demo.cl"
        val seeds = listOf(
            Seed("Feria de Bienestar Laboral", OpportunityType.FERIA_EMPRESARIAL,
                "Feria anual de bienestar para trabajadores de empresas del sector.", "Sitio web (ficticio)",
                12, e1, Priority.ALTA, 5, 5, OpportunityStatus.EVENTO_IDENTIFICADO, 1, 14, 2, "Santiago",
                "Trabajadores y sus familias", 1200, 150000, "Stand de 3x3 m, inscripción previa"),
            Seed("Jornada de Beneficios Municipales", OpportunityType.JORNADA_BENEFICIOS,
                "Jornada para funcionarios municipales con stands de convenios.", "Contacto directo",
                20, e1, Priority.ALTA, 4, 4, OpportunityStatus.CONTACTO_REALIZADO, 2, 25, 1, "Puente Alto",
                "Funcionarios municipales", 600, 0, "Participación sin costo, aviso 10 días antes"),
            Seed("Aniversario Empresa Demo Sur", OpportunityType.EVENTO_CORPORATIVO,
                "Celebración de aniversario con familias de colaboradores.", "Red social (ficticia)",
                30, e2, Priority.MEDIA, 4, 3, OpportunityStatus.PROPUESTA_ENVIADA, 3, 40, 1, "Concepción",
                "Colaboradores y familias", 800, 90000, "Máximo 2 personas por stand"),
            Seed("Feria Institucional de Servicios", OpportunityType.FERIA_INSTITUCIONAL,
                "Feria de servicios para estudiantes y funcionarios.", "Correo electrónico",
                45, e1, Priority.MEDIA, 3, 3, OpportunityStatus.EN_ESPERA_RESPUESTA, 4, 8, 1, "Antofagasta",
                "Estudiantes y funcionarios", 500, 60000, "Requiere carta de presentación"),
            Seed("Expo Familias Centro Comercial", OpportunityType.ACTIVACION_COMERCIAL,
                "Activación en espacio de alta afluencia durante fin de semana.", "Referencia",
                5, e2, Priority.BAJA, 2, 3, OpportunityStatus.CONTACTO_PENDIENTE, 5, 60, 3, "Viña del Mar",
                "Público general", 3000, 250000, "Espacio sujeto a disponibilidad"),
            Seed("Día del Trabajador Gremial", OpportunityType.ACTIVIDAD_TRABAJADORES,
                "Actividad recreativa para trabajadores asociados.", "Comunicación comercial",
                18, e1, Priority.ALTA, 4, 4, OpportunityStatus.PARTICIPACION_CONFIRMADA, 6, 5, 1, "Maipú",
                "Trabajadores asociados", 450, 40000, "Confirmar montaje el día anterior"),
            Seed("Feria Verde Municipal", OpportunityType.ACTIVIDAD_MUNICIPAL,
                "Feria de vida saludable con servicios veterinarios y mascotas.", "Redes sociales",
                60, e2, Priority.MEDIA, 3, 4, OpportunityStatus.ACTIVACION_REALIZADA, 2, -15, 1, "Rancagua",
                "Familias con mascotas", 900, 0, "Sin costo de participación"),
            Seed("Encuentro de Proveedores", OpportunityType.EVENTO_CORPORATIVO,
                "Encuentro anual con información aún incompleta.", "Contacto directo",
                3, e1, Priority.BAJA, 2, 2, OpportunityStatus.INFORMACION_INCOMPLETA, 1, 21, 1, "Providencia",
                "Proveedores y clientes", 300, 0, "Por definir")
        )

        seeds.forEachIndexed { index, s ->
            val comuna = Geo.byName(s.comuna)!!
            val id = db.insert("opportunities", null, ContentValues().apply {
                put("name", s.name); put("type", s.type.name); put("description", s.desc)
                put("source", s.source); put("detected_date", day(-s.detectedAgo))
                put("owner", s.owner); put("priority", s.priority.name); put("potential", s.potential)
                put("affinity", s.affinity); put("status", s.status.name)
                put("last_update", day(if (s.status.awaitingAnswer) -s.detectedAgo / 2 else -2))
                put("notes", ""); put("org_id", s.orgId)
                put("event_start", day(s.startIn)); put("event_end", day(s.startIn + s.days - 1))
                put("schedule", "10:00 a 18:00"); put("address", "Recinto general (ficticio)")
                put("comuna", comuna.name); put("region", comuna.region)
                put("target_audience", s.audience); put("attendees", s.attendees)
                put("conditions", s.conditions); put("reference_cost", s.cost)
            })

            db.insert("status_history", null, ContentValues().apply {
                put("opportunity_id", id); putNull("from_status"); put("to_status", s.status.name)
                put("date", day(-s.detectedAgo)); put("user", s.owner)
            })

            if (index != 0 && index != 4) {
                db.insert("contacts", null, ContentValues().apply {
                    put("opportunity_id", id); put("name", "Contacto Ficticio ${index + 1}")
                    put("role", "Encargado(a) de eventos"); put("area", ContactArea.EVENTOS.name)
                    put("email", "contacto${index + 1}@ejemplo-demo.cl")
                    put("phone", "+56 9 5555 01${index + 10}"); put("preferred_medium", "Correo")
                    put("last_contact", day(-s.detectedAgo / 2)); put("notes", "Dato ficticio")
                })
            }

            if (!s.status.needsContact && s.status != OpportunityStatus.EVENTO_IDENTIFICADO) {
                // Seguimientos vencidos y próximos para poder demostrar las alertas.
                val next: Int? = when (index) {
                    1 -> -3
                    2 -> -1
                    3 -> 2
                    5 -> 1
                    else -> null
                }
                db.insert("follow_ups", null, ContentValues().apply {
                    put("opportunity_id", id); put("user", s.owner)
                    put("type", ActionType.PRIMER_CONTACTO.name)
                    put("date_time", day(-s.detectedAgo / 2) + 10 * 3_600_000L)
                    put("result", "Se conversó con la organización (dato ficticio)")
                    put("next_action", "Enviar presentación")
                    if (next != null) put("next_date", day(next)) else putNull("next_date")
                    put("notes", "")
                })
            }

            if (s.status == OpportunityStatus.ACTIVACION_REALIZADA) {
                db.insert("activation_results", null, ContentValues().apply {
                    put("opportunity_id", id); put("date", day(s.startIn)); put("attendees", 780)
                    put("contacts_generated", 96); put("interested", 54); put("enrollments", 21)
                    put("team_notes", "Buena afluencia en la tarde (dato ficticio)")
                    put("evaluation", 4); put("learnings", "Llegar con material impreso adicional")
                })
            }
        }
    }
}
