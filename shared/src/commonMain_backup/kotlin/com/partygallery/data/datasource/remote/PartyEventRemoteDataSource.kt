package com.partygallery.data.datasource.remote

import com.partygallery.domain.model.party.PartyStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface PartyEventRemoteDataSource {
    suspend fun createPartyEvent(event: RemotePartyEvent): Result<RemotePartyEvent>
    suspend fun getPartyEventById(eventId: String): Result<RemotePartyEvent?>
    suspend fun updatePartyEvent(event: RemotePartyEvent): Result<RemotePartyEvent>
    suspend fun deletePartyEvent(eventId: String): Result<Unit>
    
    suspend fun getPartyEventsByHost(hostId: String): Result<List<RemotePartyEvent>>
    suspend fun getPartyEventsByAttendee(userId: String): Result<List<RemotePartyEvent>>
    suspend fun getPartyEventsByCoHost(userId: String): Result<List<RemotePartyEvent>>
    
    suspend fun searchPartyEvents(
        query: String,
        location: Pair<Double, Double>?,
        radius: Double?,
        tags: List<String>?,
        startDate: Instant?,
        endDate: Instant?,
        limit: Int
    ): Result<List<RemotePartyEvent>>
    
    suspend fun getPublicPartyEvents(limit: Int, offset: Int): Result<List<RemotePartyEvent>>
    suspend fun getTrendingPartyEvents(limit: Int): Result<List<RemotePartyEvent>>
    suspend fun getUpcomingPartyEvents(userId: String): Result<List<RemotePartyEvent>>
    suspend fun getLivePartyEvents(): Result<List<RemotePartyEvent>>
    
    suspend fun updatePartyStatus(eventId: String, status: PartyStatus): Result<Unit>
    suspend fun addCoHost(eventId: String, coHostId: String): Result<Unit>
    suspend fun removeCoHost(eventId: String, coHostId: String): Result<Unit>
    
    fun observePartyEvent(eventId: String): Flow<RemotePartyEvent?>
    fun observeLivePartyEvents(): Flow<List<RemotePartyEvent>>
    fun observeUserPartyEvents(userId: String): Flow<List<RemotePartyEvent>>
    
    suspend fun getPartyEventsByVenue(venue: RemoteVenue): Result<List<RemotePartyEvent>>
    suspend fun getPopularVenues(limit: Int): Result<List<RemoteVenue>>
}

data class RemotePartyEvent(
    val id: String,
    val hostId: String,
    val coHosts: List<String>,
    val title: String,
    val description: String?,
    val venue: RemoteVenue?,
    val startDateTime: String, // ISO string
    val endDateTime: String?,  // ISO string
    val coverImageUrl: String?,
    val isPrivate: Boolean,
    val inviteOnly: Boolean,
    val maxAttendees: Int?,
    val tags: List<String>,
    val musicGenres: List<String>,
    val ageRestriction: String?,
    val dresscode: String?,
    val attendeesCount: Int,
    val interestedCount: Int,
    val mediaCount: Int,
    val status: String, // PartyStatus as string
    val createdAt: String, // ISO string
    val updatedAt: String  // ISO string
)

data class RemoteVenue(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val country: String
)