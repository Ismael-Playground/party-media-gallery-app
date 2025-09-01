package com.partygallery.data.datasource.local

import com.partygallery.domain.model.party.PartyEvent
import com.partygallery.domain.model.party.PartyStatus
import kotlinx.coroutines.flow.Flow

interface PartyEventLocalDataSource {
    suspend fun savePartyEvent(event: PartyEvent): Result<Unit>
    suspend fun getPartyEventById(eventId: String): PartyEvent?
    suspend fun deletePartyEvent(eventId: String): Result<Unit>
    
    suspend fun savePartyEvents(events: List<PartyEvent>): Result<Unit>
    suspend fun getPartyEventsByHost(hostId: String): List<PartyEvent>
    suspend fun getPartyEventsByAttendee(userId: String): List<PartyEvent>
    
    suspend fun updatePartyStatus(eventId: String, status: PartyStatus): Result<Unit>
    suspend fun addCoHost(eventId: String, coHostId: String): Result<Unit>
    suspend fun removeCoHost(eventId: String, coHostId: String): Result<Unit>
    
    suspend fun getUpcomingPartyEvents(userId: String): List<PartyEvent>
    suspend fun getCachedPartyEvents(limit: Int): List<PartyEvent>
    
    fun observePartyEvent(eventId: String): Flow<PartyEvent?>
    fun observeUserPartyEvents(userId: String): Flow<List<PartyEvent>>
    
    suspend fun clearPartyEventsCache(): Result<Unit>
    suspend fun clearExpiredPartyEvents(): Result<Unit>
}