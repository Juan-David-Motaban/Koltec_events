package com.example.myapplication.validation

import com.example.myapplication.domain.DateUtils

enum class FormField {
    NAME, DESCRIPTION, SOURCE, DETECTED_DATE, EVENT_START, EVENT_END,
    SCHEDULE, COMUNA, ADDRESS, ATTENDEES, TARGET, COST, CONDITIONS, NOTES
}

/** Valores de texto del formulario de oportunidad (lo que escribe el usuario). */
data class OpportunityInput(
    val name: String = "",
    val description: String = "",
    val source: String = "",
    val detectedDate: String = "",
    val eventStart: String = "",
    val eventEnd: String = "",
    val schedule: String = "",
    val comuna: String = "",
    val address: String = "",
    val attendees: String = "",
    val targetAudience: String = "",
    val referenceCost: String = "",
    val conditions: String = "",
    val notes: String = ""
)

object OpportunityValidator {

    fun validateField(field: FormField, input: OpportunityInput): String? = when (field) {
        FormField.NAME -> Validators.text(input.name, "El nombre", 4, 80)
        FormField.DESCRIPTION -> Validators.text(input.description, "La descripción", 10, 300)
        FormField.SOURCE -> Validators.text(input.source, "La fuente de información", 3, 80)
        FormField.DETECTED_DATE -> Validators.date(input.detectedDate, "La fecha de detección")
            ?: if ((DateUtils.parse(input.detectedDate) ?: 0L) > DateUtils.startOfDay())
                "La fecha de detección no puede ser futura" else null
        FormField.EVENT_START -> Validators.date(input.eventStart, "La fecha de inicio")
        FormField.EVENT_END -> Validators.date(input.eventEnd, "La fecha de término")
            ?: Validators.dateOrder(input.eventStart, input.eventEnd)
        FormField.SCHEDULE -> Validators.text(input.schedule, "El horario", 3, 40)
        FormField.COMUNA -> Validators.required(input.comuna, "La comuna")
        FormField.ADDRESS -> Validators.optionalText(input.address, "La dirección", 120)
        FormField.ATTENDEES -> Validators.nonNegativeInt(input.attendees, "La cantidad de asistentes", 500_000)
        FormField.TARGET -> Validators.text(input.targetAudience, "El público objetivo", 3, 80)
        FormField.COST -> Validators.nonNegativeInt(input.referenceCost, "El costo referencial", 100_000_000)
        FormField.CONDITIONS -> Validators.optionalText(input.conditions, "Las condiciones", 200)
        FormField.NOTES -> Validators.optionalText(input.notes, "Las observaciones", 300)
    }

    /** Valida todos los campos y devuelve solo los que tienen error. */
    fun validateAll(input: OpportunityInput): Map<FormField, String> =
        FormField.values().mapNotNull { f -> validateField(f, input)?.let { f to it } }.toMap()
}

enum class ContactField { NAME, ROLE, EMAIL, PHONE }

data class ContactInput(
    val name: String = "", val role: String = "", val email: String = "", val phone: String = ""
)

object ContactValidator {
    fun validate(i: ContactInput): Map<ContactField, String> = buildMap<ContactField, String> {
        Validators.text(i.name, "El nombre", 3, 60)?.let { put(ContactField.NAME, it) }
        Validators.text(i.role, "El cargo", 3, 60)?.let { put(ContactField.ROLE, it) }
        Validators.email(i.email)?.let { put(ContactField.EMAIL, it) }
        Validators.phone(i.phone)?.let { put(ContactField.PHONE, it) }
    }
}

enum class FollowUpField { RESULT, NEXT_ACTION, NEXT_DATE }

data class FollowUpInput(
    val result: String = "", val nextAction: String = "", val nextDate: String = ""
)

object FollowUpValidator {
    fun validate(i: FollowUpInput, today: Long = DateUtils.startOfDay()): Map<FollowUpField, String> = buildMap<FollowUpField, String> {
        Validators.text(i.result, "El resultado", 5, 200)?.let { put(FollowUpField.RESULT, it) }
        Validators.optionalText(i.nextAction, "La próxima acción", 120)?.let { put(FollowUpField.NEXT_ACTION, it) }
        Validators.optionalFutureDate(i.nextDate, "La fecha del próximo seguimiento", today)
            ?.let { put(FollowUpField.NEXT_DATE, it) }
        if (i.nextDate.isNotBlank() && i.nextAction.isBlank() && !containsKey(FollowUpField.NEXT_DATE)) {
            put(FollowUpField.NEXT_ACTION, "Indique qué acción realizará en la fecha programada")
        }
    }
}

enum class ResultField { DATE, ATTENDEES, CONTACTS, INTERESTED, ENROLLMENTS, NOTES }

data class ResultInput(
    val date: String = "", val attendees: String = "", val contacts: String = "",
    val interested: String = "", val enrollments: String = "", val notes: String = ""
)

object ResultValidator {
    fun validate(i: ResultInput): Map<ResultField, String> = buildMap<ResultField, String> {
        Validators.date(i.date, "La fecha de realización")?.let { put(ResultField.DATE, it) }
        Validators.nonNegativeInt(i.attendees, "Los asistentes", 500_000)?.let { put(ResultField.ATTENDEES, it) }
        Validators.nonNegativeInt(i.contacts, "Los contactos generados", 500_000)?.let { put(ResultField.CONTACTS, it) }
        Validators.nonNegativeInt(i.interested, "Las personas interesadas", 500_000)?.let { put(ResultField.INTERESTED, it) }
        Validators.nonNegativeInt(i.enrollments, "Las inscripciones", 500_000)?.let { put(ResultField.ENROLLMENTS, it) }
        Validators.optionalText(i.notes, "Las observaciones", 300)?.let { put(ResultField.NOTES, it) }
        val att = i.attendees.toIntOrNull()
        val inter = i.interested.toIntOrNull()
        if (att != null && inter != null && inter > att && !containsKey(ResultField.INTERESTED)) {
            put(ResultField.INTERESTED, "Los interesados no pueden superar a los asistentes")
        }
    }
}
