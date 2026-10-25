package com.example.myapplication.domain

import com.example.myapplication.model.FollowUp
import com.example.myapplication.model.Opportunity
import com.example.myapplication.model.OpportunityStatus

data class PendingAction(val opportunity: Opportunity, val followUp: FollowUp)

data class AlertSummary(
    val upcomingEvents: List<Opportunity> = emptyList(),
    val noContact: List<Opportunity> = emptyList(),
    val unansweredProposals: List<Opportunity> = emptyList(),
    val incomplete: List<Opportunity> = emptyList(),
    val overdueFollowUps: List<PendingAction> = emptyList(),
    val upcomingFollowUps: List<PendingAction> = emptyList()
) {
    val total: Int
        get() = upcomingEvents.size + noContact.size + unansweredProposals.size +
            incomplete.size + overdueFollowUps.size + upcomingFollowUps.size
}

data class Kpis(
    val total: Int = 0,
    val open: Int = 0,
    val confirmed: Int = 0,
    val overdue: Int = 0
)

/** Calcula alertas y recordatorios internos. Lógica pura: no depende de Android. */
object AlertEngine {
    const val UPCOMING_EVENT_DAYS = 30
    const val UPCOMING_FOLLOWUP_DAYS = 7
    const val UNANSWERED_AFTER_DAYS = 7

    fun build(
        opportunities: List<Opportunity>,
        followUps: List<FollowUp>,
        today: Long = DateUtils.startOfDay()
    ): AlertSummary {
        val open = opportunities.filter { it.status.isOpen }
        val byId = opportunities.associateBy { it.id }

        // Solo el último seguimiento de cada oportunidad define la "próxima acción" vigente.
        val pending = followUps
            .groupBy { it.opportunityId }
            .mapNotNull { (_, list) -> list.maxByOrNull { it.dateTime } }
            .filter { it.nextDate != null }
            .mapNotNull { f -> byId[f.opportunityId]?.takeIf { it.status.isOpen }?.let { PendingAction(it, f) } }

        return AlertSummary(
            upcomingEvents = open
                .filter { DateUtils.daysBetween(today, it.eventStart) in 0..UPCOMING_EVENT_DAYS }
                .sortedBy { it.eventStart },
            noContact = open.filter { it.status.needsContact },
            unansweredProposals = open.filter {
                it.status.awaitingAnswer && DateUtils.daysBetween(it.lastUpdate, today) >= UNANSWERED_AFTER_DAYS
            },
            incomplete = open.filter { it.status == OpportunityStatus.INFORMACION_INCOMPLETA },
            overdueFollowUps = pending
                .filter { DateUtils.daysBetween(today, it.followUp.nextDate!!) < 0 }
                .sortedBy { it.followUp.nextDate },
            upcomingFollowUps = pending
                .filter { DateUtils.daysBetween(today, it.followUp.nextDate!!) in 0..UPCOMING_FOLLOWUP_DAYS }
                .sortedBy { it.followUp.nextDate }
        )
    }

    fun kpis(opportunities: List<Opportunity>, alerts: AlertSummary): Kpis = Kpis(
        total = opportunities.size,
        open = opportunities.count { it.status.isOpen },
        confirmed = opportunities.count { it.status == OpportunityStatus.PARTICIPACION_CONFIRMADA },
        overdue = alerts.overdueFollowUps.size
    )
}
