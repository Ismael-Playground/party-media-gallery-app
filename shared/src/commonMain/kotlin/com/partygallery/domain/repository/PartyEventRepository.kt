package com.partygallery.domain.repository

import com.partygallery.domain.model.PartyEvent
import com.partygallery.domain.model.PartyPrivacy
import com.partygallery.domain.model.PartyStatus
import com.partygallery.domain.model.Venue
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

/**
 * Repository interface for party event operations.
 *
 * S3-015: PartyEventRepository interface
 */
interface PartyEventRepository {

    /**
     * Get a party event by ID.
     */
    suspend fun getPartyById(partyId: String): Result<PartyEvent>

    /**
     * Get party event stream by ID (real-time updates).
     */
    fun observeParty(partyId: String): Flow<PartyEvent>

    /**
     * Get feed of party events.
     */
    fun getFeed(
        page: Int = 0,
        pageSize: Int = 20,
    ): Flow<List<PartyEvent>>

    /**
     * Get live parties.
     */
    fun getLiveParties(): Flow<List<PartyEvent>>

    /**
     * Get upcoming parties.
     */
    fun getUpcomingParties(
        page: Int = 0,
        pageSize: Int = 20,
    ): Flow<List<PartyEvent>>

    /**
     * Get parties hosted by a user.
     */
    fun getHostedParties(userId: String): Flow<List<PartyEvent>>

    /**
     * Get parties a user is attending.
     */
    fun getAttendingParties(userId: String): Flow<List<PartyEvent>>

    /**
     * Get parties near a location.
     */
    fun getNearbyParties(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 50.0,
    ): Flow<List<PartyEvent>>

    /**
     * Search parties by query.
     */
    fun searchParties(query: String): Flow<List<PartyEvent>>

    /**
     * Create a new party event.
     */
    suspend fun createParty(request: CreatePartyRequest): Result<PartyEvent>

    /**
     * Update a party event.
     */
    suspend fun updateParty(partyId: String, request: UpdatePartyRequest): Result<PartyEvent>

    /**
     * Delete a party event.
     */
    suspend fun deleteParty(partyId: String): Result<Unit>

    /**
     * Update party status (PLANNED, LIVE, ENDED, CANCELLED).
     */
    suspend fun updateStatus(partyId: String, status: PartyStatus): Result<Unit>

    /**
     * RSVP to a party.
     */
    suspend fun rsvp(partyId: String, status: RSVPStatus): Result<Unit>

    /**
     * Cancel RSVP to a party.
     */
    suspend fun cancelRsvp(partyId: String): Result<Unit>

    /**
     * Get attendees of a party.
     */
    fun getAttendees(partyId: String): Flow<List<AttendeeInfo>>

    /**
     * Add a co-host to a party.
     */
    suspend fun addCoHost(partyId: String, userId: String): Result<Unit>

    /**
     * Remove a co-host from a party.
     */
    suspend fun removeCoHost(partyId: String, userId: String): Result<Unit>

    /**
     * Save a party to favorites.
     */
    suspend fun saveToFavorites(partyId: String): Result<Unit>

    /**
     * Remove a party from favorites.
     */
    suspend fun removeFromFavorites(partyId: String): Result<Unit>

    /**
     * Get saved/favorite parties.
     */
    fun getSavedParties(userId: String): Flow<List<PartyEvent>>

    /**
     * Check if user can edit a party.
     */
    suspend fun canEdit(partyId: String): Boolean
}

/**
 * Request to create a new party.
 */
data class CreatePartyRequest(
    val title: String,
    val description: String? = null,
    val venue: Venue,
    val startsAt: LocalDateTime,
    val endsAt: LocalDateTime? = null,
    val privacy: PartyPrivacy = PartyPrivacy.PUBLIC,
    val maxAttendees: Int? = null,
    val coverImageUri: String? = null,
    val tags: List<String> = emptyList(),
    val musicGenres: List<String> = emptyList(),
)

/**
 * Request to update a party.
 */
data class UpdatePartyRequest(
    val title: String? = null,
    val description: String? = null,
    val venue: Venue? = null,
    val startsAt: LocalDateTime? = null,
    val endsAt: LocalDateTime? = null,
    val privacy: PartyPrivacy? = null,
    val maxAttendees: Int? = null,
    val coverImageUri: String? = null,
    val tags: List<String>? = null,
    val musicGenres: List<String>? = null,
)

/**
 * RSVP status for a party.
 */
enum class RSVPStatus {
    GOING,
    MAYBE,
    NOT_GOING,
}

/**
 * Information about a party attendee.
 */
data class AttendeeInfo(
    val userId: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String?,
    val rsvpStatus: RSVPStatus,
    val isHost: Boolean,
    val isCoHost: Boolean,
)
