package com.partygallery.domain.repository

import com.partygallery.domain.model.PartyEvent
import com.partygallery.domain.model.PartyStatus
import com.partygallery.domain.model.UserSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

/**
 * Repository interface for Party Event operations.
 *
 * S1-011: Interfaces base de repositorios
 */
interface PartyRepository {

    // ============================================
    // CRUD Operations
    // ============================================

    suspend fun createParty(party: PartyEvent): Result<PartyEvent>
    suspend fun getPartyById(partyId: String): Result<PartyEvent?>
    suspend fun updateParty(party: PartyEvent): Result<PartyEvent>
    suspend fun deleteParty(partyId: String): Result<Unit>

    // ============================================
    // Query Operations
    // ============================================

    suspend fun getPartiesByHost(hostId: String, limit: Int = 20): Result<List<PartyEvent>>
    suspend fun getUpcomingParties(limit: Int = 20): Result<List<PartyEvent>>
    suspend fun getLiveParties(limit: Int = 20): Result<List<PartyEvent>>
    suspend fun getPastParties(userId: String, limit: Int = 20): Result<List<PartyEvent>>
    suspend fun searchParties(query: String, limit: Int = 20): Result<List<PartyEvent>>
    suspend fun getPartiesByTags(tags: List<String>, limit: Int = 20): Result<List<PartyEvent>>
    suspend fun getNearbyParties(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 10.0,
        limit: Int = 20,
    ): Result<List<PartyEvent>>

    // ============================================
    // Status Management
    // ============================================

    suspend fun updatePartyStatus(partyId: String, status: PartyStatus): Result<Unit>
    suspend fun startParty(partyId: String): Result<PartyEvent>
    suspend fun endParty(partyId: String): Result<PartyEvent>
    suspend fun cancelParty(partyId: String, reason: String? = null): Result<PartyEvent>

    // ============================================
    // Attendee Management
    // ============================================

    suspend fun getAttendees(partyId: String): Result<List<PartyAttendee>>
    suspend fun getAttendeeCount(partyId: String): Result<Int>
    suspend fun rsvp(partyId: String, userId: String, status: RsvpStatus): Result<Unit>
    suspend fun checkIn(partyId: String, userId: String): Result<Unit>
    suspend fun getUserRsvpStatus(partyId: String, userId: String): Result<RsvpStatus?>

    // ============================================
    // Co-hosts
    // ============================================

    suspend fun addCoHost(partyId: String, userId: String): Result<Unit>
    suspend fun removeCoHost(partyId: String, userId: String): Result<Unit>
    suspend fun getCoHosts(partyId: String): Result<List<UserSummary>>

    // ============================================
    // Observable Flows
    // ============================================

    fun observeParty(partyId: String): Flow<PartyEvent?>
    fun observeLiveParties(): Flow<List<PartyEvent>>
    fun observeAttendeeCount(partyId: String): Flow<Int>
    fun observeUserRsvpStatus(partyId: String, userId: String): Flow<RsvpStatus?>
}

/**
 * RSVP status for party attendance.
 */
enum class RsvpStatus {
    GOING,
    MAYBE,
    NOT_GOING,
    INVITED,
}

/**
 * Party attendee with RSVP info.
 */
data class PartyAttendee(
    val user: UserSummary,
    val rsvpStatus: RsvpStatus,
    val respondedAt: Instant,
    val checkedInAt: Instant? = null,
)
