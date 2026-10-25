package com.example.myapplication.model

/** Oportunidad de activación corporativa asociada a una organización registrada. */
data class Opportunity(
    val id: Long = 0,
    val name: String,
    val type: OpportunityType,
    val description: String = "",
    val source: String = "",
    val detectedDate: Long,
    /** Correo del responsable interno. */
    val owner: String,
    val priority: Priority = Priority.MEDIA,
    /** Potencial comercial estimado (1 a 5). */
    val potential: Int = 3,
    /** Afinidad con el público objetivo (1 a 5). */
    val affinity: Int = 3,
    val status: OpportunityStatus = OpportunityStatus.EVENTO_IDENTIFICADO,
    val lastUpdate: Long,
    val notes: String = "",
    // Organización responsable (id) y su nombre (solo lectura, viene del JOIN)
    val orgId: Int,
    val orgName: String = "",
    // Evento
    val eventStart: Long,
    val eventEnd: Long,
    val schedule: String = "",
    val address: String = "",
    val comuna: String,
    val region: String,
    val targetAudience: String = "",
    val estimatedAttendees: Int = 0,
    val conditions: String = "",
    val referenceCost: Int = 0,
    val photoPath: String? = null
)

data class Contact(
    val id: Long = 0,
    val opportunityId: Long,
    val name: String,
    val role: String,
    val area: ContactArea,
    val email: String,
    val phone: String,
    val preferredMedium: String = "Correo",
    val lastContact: Long? = null,
    val notes: String = ""
)

data class FollowUp(
    val id: Long = 0,
    val opportunityId: Long,
    val user: String,
    val type: ActionType,
    val dateTime: Long,
    val result: String,
    val nextAction: String = "",
    /** Fecha del próximo seguimiento (inicio del día) o null si no hay. */
    val nextDate: Long? = null,
    val notes: String = ""
)

data class StatusChange(
    val id: Long = 0,
    val opportunityId: Long,
    val from: OpportunityStatus?,
    val to: OpportunityStatus,
    val date: Long,
    val user: String
)

data class ActivationResult(
    val opportunityId: Long,
    val date: Long,
    val attendees: Int,
    val contactsGenerated: Int,
    val interested: Int,
    val enrollments: Int,
    val teamNotes: String = "",
    val evaluation: Int = 3,
    val learnings: String = ""
)
