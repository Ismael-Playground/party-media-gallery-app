package com.partygallery.data.repository

import com.partygallery.data.datasource.PartyDataSource
import com.partygallery.data.mapper.toDomain
import com.partygallery.data.mapper.toDto
import com.partygallery.domain.model.PartyEvent
import com.partygallery.domain.model.PartyStatus
import com.partygallery.domain.model.UserSummary
import com.partygallery.domain.repository.PartyAttendee
import com.partygallery.domain.repository.PartyRepository
import com.partygallery.domain.repository.RsvpStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Implementation of PartyRepository using Firebase Firestore.
 *
 * S2.5-002: PartyRepositoryImpl con Firebase
 */
class PartyRepositoryImpl(
    private val partyDataSource: PartyDataSource,
) : PartyRepository {

    // ============================================
    // CRUD Operations
    // ============================================

    override suspend fun createParty(party: PartyEvent): Result<PartyEvent> {
        return try {
            val dto = party.toDto()
            val createdDto = partyDataSource.createParty(dto)
            Result.success(createdDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPartyById(partyId: String): Result<PartyEvent?> {
        return try {
            val dto = partyDataSource.getPartyById(partyId)
            Result.success(dto?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateParty(party: PartyEvent): Result<PartyEvent> {
        return try {
            val dto = party.toDto()
            val updatedDto = partyDataSource.updateParty(dto)
            Result.success(updatedDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteParty(partyId: String): Result<Unit> {
        return try {
            partyDataSource.deleteParty(partyId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Query Operations
    // ============================================

    override suspend fun getPartiesByHost(hostId: String, limit: Int): Result<List<PartyEvent>> {
        return try {
            val dtos = partyDataSource.getPartiesByHost(hostId, limit)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUpcomingParties(limit: Int): Result<List<PartyEvent>> {
        return try {
            val dtos = partyDataSource.getUpcomingParties(limit)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLiveParties(limit: Int): Result<List<PartyEvent>> {
        return try {
            val dtos = partyDataSource.getLiveParties(limit)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPastParties(userId: String, limit: Int): Result<List<PartyEvent>> {
        return try {
            val dtos = partyDataSource.getPastParties(userId, limit)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchParties(query: String, limit: Int): Result<List<PartyEvent>> {
        return try {
            val dtos = partyDataSource.searchParties(query, limit)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPartiesByTags(tags: List<String>, limit: Int): Result<List<PartyEvent>> {
        return try {
            val dtos = partyDataSource.getPartiesByTags(tags, limit)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNearbyParties(
        latitude: Double,
        longitude: Double,
        radiusKm: Double,
        limit: Int,
    ): Result<List<PartyEvent>> {
        return try {
            val dtos = partyDataSource.getNearbyParties(latitude, longitude, radiusKm, limit)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Status Management
    // ============================================

    override suspend fun updatePartyStatus(partyId: String, status: PartyStatus): Result<Unit> {
        return try {
            partyDataSource.updatePartyStatus(partyId, status.name)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun startParty(partyId: String): Result<PartyEvent> {
        return try {
            partyDataSource.updatePartyStatus(partyId, PartyStatus.LIVE.name)
            val updatedDto = partyDataSource.getPartyById(partyId)
                ?: throw IllegalStateException("Party not found after status update")
            Result.success(updatedDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun endParty(partyId: String): Result<PartyEvent> {
        return try {
            partyDataSource.updatePartyStatus(partyId, PartyStatus.ENDED.name)
            val updatedDto = partyDataSource.getPartyById(partyId)
                ?: throw IllegalStateException("Party not found after status update")
            Result.success(updatedDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelParty(partyId: String, reason: String?): Result<PartyEvent> {
        return try {
            partyDataSource.updatePartyStatus(partyId, PartyStatus.CANCELLED.name)
            val updatedDto = partyDataSource.getPartyById(partyId)
                ?: throw IllegalStateException("Party not found after status update")
            Result.success(updatedDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Attendee Management
    // ============================================

    override suspend fun getAttendees(partyId: String): Result<List<PartyAttendee>> {
        return try {
            val dtos = partyDataSource.getAttendees(partyId)
            val attendees = dtos.map { dto ->
                PartyAttendee(
                    user = dto.user?.toDomain() ?: UserSummary(
                        id = dto.userId,
                        username = "",
                        displayName = "",
                        avatarUrl = null,
                    ),
                    rsvpStatus = parseRsvpStatus(dto.status),
                    respondedAt = Instant.fromEpochMilliseconds(dto.rsvpAt),
                )
            }
            Result.success(attendees)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAttendeeCount(partyId: String): Result<Int> {
        return try {
            val count = partyDataSource.getAttendeeCount(partyId)
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rsvp(partyId: String, userId: String, status: RsvpStatus): Result<Unit> {
        return try {
            partyDataSource.rsvp(partyId, userId, status.name)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkIn(partyId: String, userId: String): Result<Unit> {
        return try {
            partyDataSource.checkIn(partyId, userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserRsvpStatus(partyId: String, userId: String): Result<RsvpStatus?> {
        return try {
            val status = partyDataSource.getUserRsvpStatus(partyId, userId)
            Result.success(status?.let { parseRsvpStatus(it) })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Co-hosts
    // ============================================

    override suspend fun addCoHost(partyId: String, userId: String): Result<Unit> {
        return try {
            partyDataSource.addCoHost(partyId, userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeCoHost(partyId: String, userId: String): Result<Unit> {
        return try {
            partyDataSource.removeCoHost(partyId, userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCoHosts(partyId: String): Result<List<UserSummary>> {
        return try {
            val dtos = partyDataSource.getCoHosts(partyId)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Observable Flows
    // ============================================

    override fun observeParty(partyId: String): Flow<PartyEvent?> {
        return partyDataSource.observeParty(partyId).map { dto ->
            dto?.toDomain()
        }
    }

    override fun observeLiveParties(): Flow<List<PartyEvent>> {
        return partyDataSource.observeLiveParties().map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override fun observeAttendeeCount(partyId: String): Flow<Int> {
        return partyDataSource.observeAttendeeCount(partyId)
    }

    override fun observeUserRsvpStatus(partyId: String, userId: String): Flow<RsvpStatus?> {
        return partyDataSource.observeUserRsvpStatus(partyId, userId).map { status ->
            status?.let { parseRsvpStatus(it) }
        }
    }

    // ============================================
    // Helper Functions
    // ============================================

    private fun parseRsvpStatus(status: String): RsvpStatus {
        return try {
            RsvpStatus.valueOf(status)
        } catch (e: Exception) {
            RsvpStatus.INVITED
        }
    }
}
