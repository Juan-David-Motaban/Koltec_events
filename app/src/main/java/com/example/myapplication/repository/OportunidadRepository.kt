package com.example.myapplication.repository

import com.example.myapplication.data.local.AppDao
import com.example.myapplication.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Fuente de datos de oportunidades, contactos, seguimientos, historial y resultados.
 * Cada escritura incrementa [version] y los flujos de lectura se recalculan:
 * así todas las pantallas se actualizan solas.
 */
class OportunidadRepository(private val dao: AppDao) {

    private val version = MutableStateFlow(0)

    private fun <T> observe(query: () -> T): Flow<T> =
        version.map { withContext(Dispatchers.IO) { query() } }

    private suspend fun <T> write(block: () -> T): T {
        val result = withContext(Dispatchers.IO) { block() }
        version.value = version.value + 1
        return result
    }

    fun observeOpportunities(): Flow<List<Opportunity>> = observe { dao.getOpportunities() }
    fun observeOpportunity(id: Long): Flow<Opportunity?> = observe { dao.getOpportunity(id) }
    fun observeContacts(id: Long): Flow<List<Contact>> = observe { dao.getContacts(id) }
    fun observeFollowUps(id: Long): Flow<List<FollowUp>> = observe { dao.getFollowUps(id) }
    fun observeAllFollowUps(): Flow<List<FollowUp>> = observe { dao.getAllFollowUps() }
    fun observeHistory(id: Long): Flow<List<StatusChange>> = observe { dao.getStatusHistory(id) }
    fun observeResult(id: Long): Flow<ActivationResult?> = observe { dao.getResult(id) }

    suspend fun getOpportunity(id: Long): Opportunity? = withContext(Dispatchers.IO) { dao.getOpportunity(id) }
    suspend fun organizaciones(): List<Organizacion> = withContext(Dispatchers.IO) { dao.getOrganizations() }
    suspend fun usuarios(): List<Usuario> = withContext(Dispatchers.IO) { dao.getUsers() }

    /** Crea la oportunidad y registra el estado inicial en el historial. */
    suspend fun create(o: Opportunity, user: String): Long = write {
        val now = System.currentTimeMillis()
        val id = dao.insertOpportunity(o.copy(lastUpdate = now))
        dao.insertStatusChange(StatusChange(opportunityId = id, from = null, to = o.status, date = now, user = user))
        id
    }

    /** Actualiza la oportunidad; si cambió el estado deja constancia en el historial. */
    suspend fun update(o: Opportunity, user: String) = write {
        val now = System.currentTimeMillis()
        val previous = dao.getOpportunity(o.id)
        dao.updateOpportunity(o.copy(lastUpdate = now))
        if (previous != null && previous.status != o.status) {
            dao.insertStatusChange(StatusChange(opportunityId = o.id, from = previous.status, to = o.status, date = now, user = user))
        }
    }

    suspend fun changeStatus(id: Long, newStatus: OpportunityStatus, user: String) = write {
        val current = dao.getOpportunity(id) ?: return@write
        if (current.status == newStatus) return@write
        val now = System.currentTimeMillis()
        dao.updateOpportunity(current.copy(status = newStatus, lastUpdate = now))
        dao.insertStatusChange(StatusChange(opportunityId = id, from = current.status, to = newStatus, date = now, user = user))
    }

    suspend fun delete(id: Long) = write { dao.deleteOpportunity(id) }

    suspend fun addContact(c: Contact) = write { dao.insertContact(c); touch(c.opportunityId) }

    suspend fun addFollowUp(f: FollowUp) = write { dao.insertFollowUp(f); touch(f.opportunityId) }

    suspend fun saveResult(r: ActivationResult, user: String) = write {
        dao.upsertResult(r)
        val current = dao.getOpportunity(r.opportunityId) ?: return@write
        val now = System.currentTimeMillis()
        if (current.status != OpportunityStatus.ACTIVACION_REALIZADA) {
            dao.updateOpportunity(current.copy(status = OpportunityStatus.ACTIVACION_REALIZADA, lastUpdate = now))
            dao.insertStatusChange(
                StatusChange(opportunityId = r.opportunityId, from = current.status, to = OpportunityStatus.ACTIVACION_REALIZADA, date = now, user = user)
            )
        }
    }

    private fun touch(opportunityId: Long) {
        dao.getOpportunity(opportunityId)?.let { dao.updateOpportunity(it.copy(lastUpdate = System.currentTimeMillis())) }
    }
}
