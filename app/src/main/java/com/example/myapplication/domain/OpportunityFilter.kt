package com.example.myapplication.domain

import com.example.myapplication.model.*

enum class DatePreset(val label: String) {
    ALL("Todas las fechas"),
    NEXT_7("Próximos 7 días"),
    NEXT_30("Próximos 30 días"),
    PAST("Ya realizados")
}

data class ListFilters(
    val query: String = "",
    val status: OpportunityStatus? = null,
    val type: OpportunityType? = null,
    val priority: Priority? = null,
    val comuna: String? = null,
    val region: String? = null,
    val owner: String? = null,
    val orgId: Int? = null,
    val datePreset: DatePreset = DatePreset.ALL
) {
    val activeCount: Int
        get() = listOfNotNull(status, type, priority, comuna, region, owner, orgId).size +
            (if (datePreset != DatePreset.ALL) 1 else 0)
}

object OpportunityFilter {
    fun apply(list: List<Opportunity>, f: ListFilters, today: Long = DateUtils.startOfDay()): List<Opportunity> {
        val q = f.query.trim().lowercase()
        return list.filter { o ->
            (q.isEmpty() || o.name.lowercase().contains(q) || o.orgName.lowercase().contains(q)) &&
                (f.status == null || o.status == f.status) &&
                (f.type == null || o.type == f.type) &&
                (f.priority == null || o.priority == f.priority) &&
                (f.comuna == null || o.comuna == f.comuna) &&
                (f.region == null || o.region == f.region) &&
                (f.owner == null || o.owner == f.owner) &&
                (f.orgId == null || o.orgId == f.orgId) &&
                matchesDate(o, f.datePreset, today)
        }.sortedWith(compareByDescending<Opportunity> { it.priority.weight }.thenBy { it.eventStart })
    }

    private fun matchesDate(o: Opportunity, preset: DatePreset, today: Long): Boolean {
        val days = DateUtils.daysBetween(today, o.eventStart)
        return when (preset) {
            DatePreset.ALL -> true
            DatePreset.NEXT_7 -> days in 0..7
            DatePreset.NEXT_30 -> days in 0..30
            DatePreset.PAST -> days < 0
        }
    }
}
