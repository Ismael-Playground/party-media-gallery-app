package com.partygallery.data.datasource

import com.partygallery.data.dto.PartyAttendeeDto
import com.partygallery.data.dto.PartyEventDto
import com.partygallery.data.dto.UserSummaryDto
import kotlinx.coroutines.flow.Flow

/**
 * Data source interface for Party Event operations.
 * Platform-specific implementations will provide the actual Firebase Firestore integration.
 *
 * S2.5-002: PartyRepositoryImpl con Firebase
 */
interface PartyDataSource {

    // ============================================
    // CRUD Operations
    // ============================================

    suspend fun createParty(party: PartyEventDto): PartyEventDto

    suspend fun getPartyById(partyId: String): PartyEventDto?

    suspend fun updateParty(party: PartyEventDto): PartyEventDto

    suspend fun deleteParty(partyId: String)

    // ============================================
    // Query Operations
    // ============================================

    suspend fun getPartiesByHost(hostId: String, limit: Int): List<PartyEventDto>

    suspend fun getUpcomingParties(limit: Int): List<PartyEventDto>

    suspend fun getLiveParties(limit: Int): List<PartyEventDto>

    suspend fun getPastParties(userId: String, limit: Int): List<PartyEventDto>

    suspend fun searchParties(query: String, limit: Int): List<PartyEventDto>

    suspend fun getPartiesByTags(tags: List<String>, limit: Int): List<PartyEventDto>

    suspend fun getNearbyParties(
        latitude: Double,
        longitude: Double,
        radiusKm: Double,
        limit: Int,
    ): List<PartyEventDto>

    // ============================================
    // Status Management
    // ============================================

    suspend fun updatePartyStatus(partyId: String, status: String)

    // ============================================
    // Attendee Management
    // ============================================

    suspend fun getAttendees(partyId: String): List<PartyAttendeeDto>

    suspend fun getAttendeeCount(partyId: String): Int

    suspend fun rsvp(partyId: String, userId: String, status: String)

    suspend fun checkIn(partyId: String, userId: String)

    suspend fun getUserRsvpStatus(partyId: String, userId: String): String?

    // ============================================
    // Co-hosts
    // ============================================

    suspend fun addCoHost(partyId: String, userId: String)

    suspend fun removeCoHost(partyId: String, userId: String)

    suspend fun getCoHosts(partyId: String): List<UserSummaryDto>

    // ============================================
    // Real-time Observers
    // ============================================

    fun observeParty(partyId: String): Flow<PartyEventDto?>

    fun observeLiveParties(): Flow<List<PartyEventDto>>

    fun observeAttendeeCount(partyId: String): Flow<Int>

    fun observeUserRsvpStatus(partyId: String, userId: String): Flow<String?>
}
