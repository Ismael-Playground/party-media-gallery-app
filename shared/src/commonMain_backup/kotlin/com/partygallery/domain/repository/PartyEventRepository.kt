package com.partygallery.domain.repository

import com.partygallery.domain.model.party.PartyEvent
import com.partygallery.domain.model.party.PartyStatus
import com.partygallery.domain.model.party.Venue
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface PartyEventRepository {
    suspend fun createPartyEvent(event: PartyEvent): Result<PartyEvent>
    suspend fun getPartyEventById(eventId: String): Result<PartyEvent?>
    suspend fun updatePartyEvent(event: PartyEvent): Result<PartyEvent>
    suspend fun deletePartyEvent(eventId: String): Result<Unit>
    
    suspend fun getPartyEventsByHost(hostId: String): Result<List<PartyEvent>>
    suspend fun getPartyEventsByAttendee(userId: String): Result<List<PartyEvent>>
    suspend fun getPartyEventsByCoHost(userId: String): Result<List<PartyEvent>>
    
    suspend fun searchPartyEvents(
        query: String,
        location: Pair<Double, Double>? = null,
        radius: Double? = null,
        tags: List<String>? = null,
        startDate: Instant? = null,
        endDate: Instant? = null,
        limit: Int = 20
    ): Result<List<PartyEvent>>
    
    suspend fun getPublicPartyEvents(limit: Int = 20, offset: Int = 0): Result<List<PartyEvent>>
    suspend fun getTrendingPartyEvents(limit: Int = 20): Result<List<PartyEvent>>
    suspend fun getUpcomingPartyEvents(userId: String): Result<List<PartyEvent>>
    suspend fun getLivePartyEvents(): Result<List<PartyEvent>>
    
    suspend fun updatePartyStatus(eventId: String, status: PartyStatus): Result<Unit>
    suspend fun addCoHost(eventId: String, coHostId: String): Result<Unit>
    suspend fun removeCoHost(eventId: String, coHostId: String): Result<Unit>
    
    fun observePartyEvent(eventId: String): Flow<PartyEvent?>
    fun observeLivePartyEvents(): Flow<List<PartyEvent>>
    fun observeUserPartyEvents(userId: String): Flow<List<PartyEvent>>
    
    suspend fun getPartyEventsByVenue(venue: Venue): Result<List<PartyEvent>>
    suspend fun getPopularVenues(limit: Int = 10): Result<List<Venue>>
}