package com.example.myapplication

import com.example.myapplication.validation.*
import org.junit.Assert.*
import org.junit.Test

class ValidatorsTest {

    @Test fun email_invalido_y_valido() {
        assertNotNull(Validators.email("sin-arroba"))
        assertNotNull(Validators.email(""))
        assertNull(Validators.email("nombre@empresa.cl"))
    }

    @Test fun telefono_valido() {
        assertNull(Validators.phone("+56 9 5555 0000"))
        assertNotNull(Validators.phone("123"))
    }

    @Test fun fecha_estricta() {
        assertNull(Validators.date("15/03/2027", "La fecha"))
        assertNotNull(Validators.date("31/02/2027", "La fecha"))
        assertNotNull(Validators.date("2027-03-15", "La fecha"))
    }

    @Test fun fecha_termino_no_puede_ser_anterior() {
        assertNotNull(Validators.dateOrder("10/05/2027", "09/05/2027"))
        assertNull(Validators.dateOrder("10/05/2027", "10/05/2027"))
    }

    @Test fun enteros_no_negativos() {
        assertNull(Validators.nonNegativeInt("0", "El valor"))
        assertNotNull(Validators.nonNegativeInt("-1", "El valor"))
        assertNotNull(Validators.nonNegativeInt("abc", "El valor"))
        assertNotNull(Validators.nonNegativeInt("", "El valor"))
    }

    @Test fun formulario_oportunidad_vacio_marca_campos_obligatorios() {
        val errores = OpportunityValidator.validateAll(OpportunityInput())
        assertTrue(errores.containsKey(FormField.NAME))
        assertTrue(errores.containsKey(FormField.EVENT_START))
        assertTrue(errores.containsKey(FormField.COMUNA))
        assertFalse(errores.containsKey(FormField.NOTES)) // opcional
    }

    @Test fun formulario_oportunidad_valido_no_tiene_errores() {
        val ok = OpportunityInput(
            name = "Feria de bienestar", description = "Feria anual para trabajadores",
            source = "Sitio web", detectedDate = "01/01/2020", eventStart = "10/10/2030",
            eventEnd = "11/10/2030", schedule = "10:00 a 18:00", comuna = "Santiago",
            attendees = "500", targetAudience = "Trabajadores", referenceCost = "0"
        )
        assertTrue(OpportunityValidator.validateAll(ok).isEmpty())
    }

    @Test fun seguimiento_con_fecha_exige_accion() {
        val errores = FollowUpValidator.validate(
            FollowUpInput(result = "Se llamó al contacto", nextAction = "", nextDate = "10/10/2099")
        )
        assertTrue(errores.containsKey(FollowUpField.NEXT_ACTION))
    }

    @Test fun contacto_valido() {
        val i = ContactInput("Contacto Ficticio", "Encargado", "c@ejemplo.cl", "+56 9 5555 0000")
        assertTrue(ContactValidator.validate(i).isEmpty())
        assertTrue(ContactValidator.validate(i.copy(email = "x")).containsKey(ContactField.EMAIL))
    }

    @Test fun resultado_interesados_no_superan_asistentes() {
        val e = ResultValidator.validate(
            ResultInput(date = "10/10/2026", attendees = "10", contacts = "1", interested = "20", enrollments = "0")
        )
        assertTrue(e.containsKey(ResultField.INTERESTED))
    }

    @Test fun organizacion_valida_y_url() {
        val ok = OrgInput(nombre = "Empresa Demo", rubro = "Servicios", comuna = "Santiago")
        assertTrue(OrgValidator.validarTodo(ok).isEmpty())
        assertTrue(OrgValidator.validarTodo(ok.copy(sitioWeb = "no es url")).containsKey(OrgField.SITIO_WEB))
    }
}
