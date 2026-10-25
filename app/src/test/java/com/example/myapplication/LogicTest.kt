package com.example.myapplication

import com.example.myapplication.domain.*
import com.example.myapplication.model.*
import org.junit.Assert.*
import org.junit.Test

class LogicTest {
    private val today = DateUtils.startOfDay()
    private fun day(n: Int) = DateUtils.addDays(today, n)

    private fun opp(
        id: Long, status: OpportunityStatus = OpportunityStatus.CONTACTO_REALIZADO,
        startIn: Int = 100, lastUpdateAgo: Int = 0, priority: Priority = Priority.MEDIA,
        comuna: String = "Santiago", owner: String = "a@demo.cl", orgId: Int = 1, name: String = "Evento $id"
    ) = Opportunity(
        id = id, name = name, type = OpportunityType.EVENTO_CORPORATIVO, detectedDate = day(-10),
        owner = owner, priority = priority, status = status, lastUpdate = day(-lastUpdateAgo),
        orgId = orgId, orgName = "Org", eventStart = day(startIn), eventEnd = day(startIn),
        comuna = comuna, region = "Metropolitana"
    )

    private fun follow(oppId: Long, nextIn: Int?, daysAgo: Int = 1) = FollowUp(
        opportunityId = oppId, user = "a@demo.cl", type = ActionType.LLAMADA,
        dateTime = day(-daysAgo), result = "ok", nextAction = "volver a llamar",
        nextDate = nextIn?.let { day(it) }
    )

    @Test fun alertas_eventos_proximos_y_sin_contacto() {
        val list = listOf(
            opp(1, startIn = 10),
            opp(2, status = OpportunityStatus.CONTACTO_PENDIENTE, startIn = 90),
            opp(3, status = OpportunityStatus.DESCARTADA, startIn = 5)
        )
        val a = AlertEngine.build(list, emptyList(), today)
        assertEquals(listOf(1L), a.upcomingEvents.map { it.id })   // la descartada no cuenta
        assertEquals(listOf(2L), a.noContact.map { it.id })
    }

    @Test fun alertas_seguimientos_atrasados_y_proximos() {
        val list = listOf(opp(1), opp(2))
        val f = listOf(follow(1, nextIn = -2), follow(2, nextIn = 3))
        val a = AlertEngine.build(list, f, today)
        assertEquals(listOf(1L), a.overdueFollowUps.map { it.opportunity.id })
        assertEquals(listOf(2L), a.upcomingFollowUps.map { it.opportunity.id })
    }

    @Test fun solo_el_ultimo_seguimiento_define_la_proxima_accion() {
        val list = listOf(opp(1))
        // El seguimiento antiguo estaba atrasado, pero el más reciente reprogramó a futuro.
        val f = listOf(follow(1, nextIn = -5, daysAgo = 10), follow(1, nextIn = 4, daysAgo = 1))
        val a = AlertEngine.build(list, f, today)
        assertTrue(a.overdueFollowUps.isEmpty())
        assertEquals(1, a.upcomingFollowUps.size)
    }

    @Test fun propuesta_sin_respuesta_tras_7_dias() {
        val list = listOf(
            opp(1, status = OpportunityStatus.PROPUESTA_ENVIADA, lastUpdateAgo = 9),
            opp(2, status = OpportunityStatus.PROPUESTA_ENVIADA, lastUpdateAgo = 2)
        )
        val a = AlertEngine.build(list, emptyList(), today)
        assertEquals(listOf(1L), a.unansweredProposals.map { it.id })
    }

    @Test fun filtros_combinados_y_orden_por_prioridad() {
        val list = listOf(
            opp(1, priority = Priority.BAJA, comuna = "Santiago", name = "Feria Verde"),
            opp(2, priority = Priority.ALTA, comuna = "Santiago", name = "Feria Azul"),
            opp(3, priority = Priority.ALTA, comuna = "Maipú", name = "Otra")
        )
        val r = OpportunityFilter.apply(list, ListFilters(query = "feria"), today)
        assertEquals(listOf(2L, 1L), r.map { it.id })          // ALTA primero
        val r2 = OpportunityFilter.apply(list, ListFilters(comuna = "Maipú"), today)
        assertEquals(listOf(3L), r2.map { it.id })
        val r3 = OpportunityFilter.apply(list, ListFilters(priority = Priority.ALTA, query = "azul"), today)
        assertEquals(listOf(2L), r3.map { it.id })
    }

    @Test fun filtro_por_fecha() {
        val list = listOf(opp(1, startIn = 3), opp(2, startIn = 20), opp(3, startIn = -4))
        assertEquals(listOf(1L), OpportunityFilter.apply(list, ListFilters(datePreset = DatePreset.NEXT_7), today).map { it.id })
        assertEquals(listOf(3L), OpportunityFilter.apply(list, ListFilters(datePreset = DatePreset.PAST), today).map { it.id })
    }

    @Test fun comuna_mas_cercana() {
        assertEquals("Santiago", Geo.nearest(-33.45, -70.66).name)
        assertEquals("Concepción", Geo.nearest(-36.8, -73.0).name)
    }

    @Test fun fechas_formato_y_dias() {
        assertEquals(5, DateUtils.daysBetween(today, day(5)))
        assertNull(DateUtils.parse("32/13/2026"))
        assertNotNull(DateUtils.parse("01/02/2026"))
    }

    @Test fun hash_estable() {
        assertEquals(Hashing.sha256("abc"), Hashing.sha256("abc"))
        assertNotEquals(Hashing.sha256("abc"), Hashing.sha256("abd"))
    }
}
