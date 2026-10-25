package com.example.myapplication.model

/** Convierte el nombre guardado en SQLite a su enum, con valor por defecto si no existe. */
inline fun <reified T : Enum<T>> enumFrom(name: String?, default: T): T =
    enumValues<T>().firstOrNull { it.name == name } ?: default

enum class OpportunityType(val label: String) {
    EVENTO_CORPORATIVO("Evento corporativo"),
    FERIA_EMPRESARIAL("Feria empresarial"),
    ACTIVIDAD_MUNICIPAL("Actividad municipal"),
    JORNADA_BENEFICIOS("Jornada de beneficios"),
    FERIA_INSTITUCIONAL("Feria institucional"),
    ACTIVIDAD_TRABAJADORES("Actividad de trabajadores"),
    ACTIVACION_COMERCIAL("Activación comercial"),
    OTRA("Otra instancia relevante")
}

enum class OpportunityStatus(val label: String, val isOpen: Boolean = true) {
    EVENTO_IDENTIFICADO("Evento identificado"),
    CONTACTO_PENDIENTE("Contacto pendiente"),
    CONTACTO_REALIZADO("Contacto realizado"),
    PROPUESTA_ENVIADA("Propuesta enviada"),
    EN_ESPERA_RESPUESTA("En espera de respuesta"),
    EN_EVALUACION("En evaluación"),
    REQUIERE_SEGUIMIENTO("Requiere seguimiento"),
    INFORMACION_INCOMPLETA("Información incompleta"),
    PARTICIPACION_CONFIRMADA("Participación confirmada"),
    ACTIVACION_REALIZADA("Activación realizada", isOpen = false),
    ACTIVACION_CANCELADA("Activación cancelada", isOpen = false),
    DESCARTADA("Oportunidad descartada", isOpen = false),
    CERRADA("Oportunidad cerrada", isOpen = false);

    /** Aún no se ha contactado a la organización. */
    val needsContact: Boolean
        get() = this == EVENTO_IDENTIFICADO || this == CONTACTO_PENDIENTE

    /** Se envió propuesta y no hay respuesta final. */
    val awaitingAnswer: Boolean
        get() = this == PROPUESTA_ENVIADA || this == EN_ESPERA_RESPUESTA
}

enum class Priority(val label: String, val weight: Int) {
    BAJA("Baja", 1),
    MEDIA("Media", 2),
    ALTA("Alta", 3)
}

enum class ActionType(val label: String) {
    INVESTIGACION_INICIAL("Investigación inicial"),
    BUSQUEDA_CONTACTO("Búsqueda de contacto"),
    PRIMER_CONTACTO("Primer contacto"),
    ENVIO_PRESENTACION("Envío de presentación"),
    ENVIO_PROPUESTA("Envío de propuesta"),
    SOLICITUD_ANTECEDENTES("Solicitud de antecedentes"),
    REUNION("Reunión"),
    LLAMADA("Llamada"),
    SEGUIMIENTO("Seguimiento"),
    RECEPCION_RESPUESTA("Recepción de respuesta"),
    CONFIRMACION("Confirmación"),
    CIERRE("Cierre")
}

enum class OrgType(val label: String) {
    EMPRESA("Empresa"),
    MUNICIPALIDAD("Municipalidad"),
    INSTITUCION_PUBLICA("Institución pública"),
    INSTITUCION_PRIVADA("Institución privada"),
    ORGANIZADOR_EVENTOS("Organizador de eventos"),
    CENTRO_COMERCIAL("Centro comercial"),
    ASOCIACION("Asociación"),
    OTRA("Otra entidad")
}

enum class ContactArea(val label: String) {
    EVENTOS("Eventos"),
    ACTIVACIONES("Activaciones"),
    MARKETING("Marketing"),
    COMUNICACIONES("Comunicaciones"),
    RECURSOS_HUMANOS("Recursos Humanos"),
    BIENESTAR("Bienestar"),
    BENEFICIOS("Beneficios"),
    ADMINISTRACION("Administración")
}
