package com.app.test.repository

import com.partygallery.domain.model.PartyEvent
import com.partygallery.domain.model.PartyStatus
import com.partygallery.domain.model.UserSummary
import com.partygallery.domain.repository.PartyAttendee
import com.partygallery.domain.repository.PartyRepository
import com.partygallery.domain.repository.RsvpStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock

/**
 * Fake PartyRepository for testing.
 *
 * This fake allows you to control party data behavior in tests
 * by setting up success/failure scenarios before each test.
 */
class FakePartyRepository : PartyRepository {

    // ============================================
    // Test State
    // ============================================

    private val parties = mutableMapOf<String, PartyEvent>()
    private val attendees = mutableMapOf<String, MutableList<PartyAttendee>>()
    private val rsvpStatuses = mutableMapOf<String, MutableMap<String, RsvpStatus>>()
    private val partyFlows = mutableMapOf<String, MutableStateFlow<PartyEvent?>>()
    private val livePartiesStateFlow = MutableStateFlow<List<PartyEvent>>(emptyList())
    private val attendeeCountFlows = mutableMapOf<String, MutableStateFlow<Int>>()
    private val rsvpStatusFlows = mutableMapOf<String, MutableStateFlow<RsvpStatus?>>()

    // Configurable behaviors
    private var shouldFail: Boolean = false
    private var failureError: Exception = Exception("Test error")

    // ============================================
    // Test Setup Methods
    // ============================================

    fun setParties(vararg partyList: PartyEvent) {
        parties.clear()
        partyList.forEach { parties[it.id] = it }
        updateLivePartiesFlow()
    }

    fun addParty(party: PartyEvent) {
        parties[party.id] = party
        partyFlows[party.id]?.value = party
        updateLivePartiesFlow()
    }

    fun setShouldFail(fail: Boolean, error: Exception = Exception("Test error")) {
        shouldFail = fail
        failureError = error
    }

    fun setAttendees(partyId: String, attendeeList: List<PartyAttendee>) {
        attendees[partyId] = attendeeList.toMutableList()
        attendeeCountFlows[partyId]?.value = attendeeList.size
    }

    fun setRsvpStatus(partyId: String, userId: String, status: RsvpStatus) {
        rsvpStatuses.getOrPut(partyId) { mutableMapOf() }[userId] = status
        rsvpStatusFlows["$partyId:$userId"]?.value = status
    }

    fun reset() {
        parties.clear()
        attendees.clear()
        rsvpStatuses.clear()
        partyFlows.clear()
        livePartiesStateFlow.value = emptyList()
        attendeeCountFlows.clear()
        rsvpStatusFlows.clear()
        shouldFail = false
    }

    private fun updateLivePartiesFlow() {
        livePartiesStateFlow.value = parties.values.filter { it.status == PartyStatus.LIVE }
    }

    // ============================================
    // PartyRepository Implementation
    // ============================================

    override suspend fun createParty(party: PartyEvent): Result<PartyEvent> {
        if (shouldFail) return Result.failure(failureError)
        parties[party.id] = party
        partyFlows[party.id]?.value = party
        updateLivePartiesFlow()
        return Result.success(party)
    }

    override suspend fun getPartyById(partyId: String): Result<PartyEvent?> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(parties[partyId])
    }

    override suspend fun updateParty(party: PartyEvent): Result<PartyEvent> {
        if (shouldFail) return Result.failure(failureError)
        parties[party.id] = party
        partyFlows[party.id]?.value = party
        updateLivePartiesFlow()
        return Result.success(party)
    }

    override suspend fun deleteParty(partyId: String): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        parties.remove(partyId)
        partyFlows[partyId]?.value = null
        updateLivePartiesFlow()
        return Result.success(Unit)
    }

    override suspend fun getPartiesByHost(hostId: String, limit: Int): Result<List<PartyEvent>> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(
            parties.values
                .filter { it.hostId == hostId }
                .take(limit),
        )
    }

    override suspend fun getUpcomingParties(limit: Int): Result<List<PartyEvent>> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(
            parties.values
                .filter { it.status == PartyStatus.PLANNED }
                .take(limit),
        )
    }

    override suspend fun getLiveParties(limit: Int): Result<List<PartyEvent>> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(
            parties.values
                .filter { it.status == PartyStatus.LIVE }
                .take(limit),
        )
    }

    override suspend fun getPastParties(userId: String, limit: Int): Result<List<PartyEvent>> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(
            parties.values
                .filter { it.status == PartyStatus.ENDED && it.hostId == userId }
                .take(limit),
        )
    }

    override suspend fun searchParties(query: String, limit: Int): Result<List<PartyEvent>> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(
            parties.values
                .filter {
                    it.title.contains(query, ignoreCase = true) ||
                        it.description?.contains(query, ignoreCase = true) == true
                }
                .take(limit),
        )
    }

    override suspend fun getPartiesByTags(tags: List<String>, limit: Int): Result<List<PartyEvent>> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(
            parties.values
                .filter { party -> tags.any { tag -> party.tags.contains(tag) } }
                .take(limit),
        )
    }

    override suspend fun getNearbyParties(
        latitude: Double,
        longitude: Double,
        radiusKm: Double,
        limit: Int,
    ): Result<List<PartyEvent>> {
        if (shouldFail) return Result.failure(failureError)
        // Simplified: return all parties with coordinates
        return Result.success(
            parties.values
                .filter { it.venue.hasCoordinates }
                .take(limit),
        )
    }

    override suspend fun updatePartyStatus(partyId: String, status: PartyStatus): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        parties[partyId]?.let { party ->
            parties[partyId] = party.copy(status = status)
            partyFlows[partyId]?.value = parties[partyId]
            updateLivePartiesFlow()
        }
        return Result.success(Unit)
    }

    override suspend fun startParty(partyId: String): Result<PartyEvent> {
        if (shouldFail) return Result.failure(failureError)
        val party = parties[partyId] ?: return Result.failure(Exception("Party not found"))
        val updated = party.copy(status = PartyStatus.LIVE)
        parties[partyId] = updated
        partyFlows[partyId]?.value = updated
        updateLivePartiesFlow()
        return Result.success(updated)
    }

    override suspend fun endParty(partyId: String): Result<PartyEvent> {
        if (shouldFail) return Result.failure(failureError)
        val party = parties[partyId] ?: return Result.failure(Exception("Party not found"))
        val updated = party.copy(status = PartyStatus.ENDED)
        parties[partyId] = updated
        partyFlows[partyId]?.value = updated
        updateLivePartiesFlow()
        return Result.success(updated)
    }

    override suspend fun cancelParty(partyId: String, reason: String?): Result<PartyEvent> {
        if (shouldFail) return Result.failure(failureError)
        val party = parties[partyId] ?: return Result.failure(Exception("Party not found"))
        val updated = party.copy(status = PartyStatus.CANCELLED)
        parties[partyId] = updated
        partyFlows[partyId]?.value = updated
        updateLivePartiesFlow()
        return Result.success(updated)
    }

    override suspend fun getAttendees(partyId: String): Result<List<PartyAttendee>> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(attendees[partyId] ?: emptyList())
    }

    override suspend fun getAttendeeCount(partyId: String): Result<Int> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(attendees[partyId]?.size ?: 0)
    }

    override suspend fun rsvp(partyId: String, userId: String, status: RsvpStatus): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        rsvpStatuses.getOrPut(partyId) { mutableMapOf() }[userId] = status
        rsvpStatusFlows["$partyId:$userId"]?.value = status
        return Result.success(Unit)
    }

    override suspend fun checkIn(partyId: String, userId: String): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        val partyAttendees = attendees.getOrPut(partyId) { mutableListOf() }
        val index = partyAttendees.indexOfFirst { it.user.id == userId }
        if (index >= 0) {
            partyAttendees[index] = partyAttendees[index].copy(checkedInAt = Clock.System.now())
        }
        return Result.success(Unit)
    }

    override suspend fun getUserRsvpStatus(partyId: String, userId: String): Result<RsvpStatus?> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(rsvpStatuses[partyId]?.get(userId))
    }

    override suspend fun addCoHost(partyId: String, userId: String): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        // Simplified implementation
        return Result.success(Unit)
    }

    override suspend fun removeCoHost(partyId: String, userId: String): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(Unit)
    }

    override suspend fun getCoHosts(partyId: String): Result<List<UserSummary>> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(parties[partyId]?.coHosts ?: emptyList())
    }

    override fun observeParty(partyId: String): Flow<PartyEvent?> {
        return partyFlows.getOrPut(partyId) {
            MutableStateFlow(parties[partyId])
        }
    }

    override fun observeLiveParties(): Flow<List<PartyEvent>> = livePartiesStateFlow

    override fun observeAttendeeCount(partyId: String): Flow<Int> {
        return attendeeCountFlows.getOrPut(partyId) {
            MutableStateFlow(attendees[partyId]?.size ?: 0)
        }
    }

    override fun observeUserRsvpStatus(partyId: String, userId: String): Flow<RsvpStatus?> {
        return rsvpStatusFlows.getOrPut("$partyId:$userId") {
            MutableStateFlow(rsvpStatuses[partyId]?.get(userId))
        }
    }
}
