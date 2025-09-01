package com.partygallery.data.repository

import com.partygallery.domain.repository.PartyEventRepository
import com.partygallery.domain.model.party.PartyEvent
import com.partygallery.domain.model.party.PartyStatus
import com.partygallery.domain.model.party.Venue
import com.partygallery.data.datasource.remote.PartyEventRemoteDataSource
import com.partygallery.data.datasource.local.PartyEventLocalDataSource
import com.partygallery.data.mapper.PartyEventMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class PartyEventRepositoryImpl(
    private val remoteDataSource: PartyEventRemoteDataSource,
    private val localDataSource: PartyEventLocalDataSource,
    private val mapper: PartyEventMapper
) : PartyEventRepository {

    override suspend fun createPartyEvent(event: PartyEvent): Result<PartyEvent> = runCatching {
        val remoteEvent = remoteDataSource.createPartyEvent(mapper.toRemotePartyEvent(event)).getOrThrow()
        val localEvent = mapper.toDomainPartyEvent(remoteEvent)
        localDataSource.savePartyEvent(localEvent)
        localEvent
    }

    override suspend fun getPartyEventById(eventId: String): Result<PartyEvent?> = runCatching {
        // Try local first
        localDataSource.getPartyEventById(eventId)?.let { return@runCatching it }
        
        // Fallback to remote
        val remoteEvent = remoteDataSource.getPartyEventById(eventId).getOrThrow() ?: return@runCatching null
        val domainEvent = mapper.toDomainPartyEvent(remoteEvent)
        localDataSource.savePartyEvent(domainEvent)
        domainEvent
    }

    override suspend fun updatePartyEvent(event: PartyEvent): Result<PartyEvent> = runCatching {
        val remoteEvent = remoteDataSource.updatePartyEvent(mapper.toRemotePartyEvent(event)).getOrThrow()
        val localEvent = mapper.toDomainPartyEvent(remoteEvent)
        localDataSource.savePartyEvent(localEvent)
        localEvent
    }

    override suspend fun deletePartyEvent(eventId: String): Result<Unit> = runCatching {
        remoteDataSource.deletePartyEvent(eventId).getOrThrow()
        localDataSource.deletePartyEvent(eventId)
    }

    override suspend fun getPartyEventsByHost(hostId: String): Result<List<PartyEvent>> = runCatching {
        val remoteEvents = remoteDataSource.getPartyEventsByHost(hostId).getOrThrow()
        val domainEvents = remoteEvents.map { mapper.toDomainPartyEvent(it) }
        localDataSource.savePartyEvents(domainEvents)
        domainEvents
    }

    override suspend fun getPartyEventsByAttendee(userId: String): Result<List<PartyEvent>> = runCatching {
        val remoteEvents = remoteDataSource.getPartyEventsByAttendee(userId).getOrThrow()
        remoteEvents.map { mapper.toDomainPartyEvent(it) }
    }

    override suspend fun getPartyEventsByCoHost(userId: String): Result<List<PartyEvent>> = runCatching {
        val remoteEvents = remoteDataSource.getPartyEventsByCoHost(userId).getOrThrow()
        remoteEvents.map { mapper.toDomainPartyEvent(it) }
    }

    override suspend fun searchPartyEvents(
        query: String,
        location: Pair<Double, Double>?,
        radius: Double?,
        tags: List<String>?,
        startDate: Instant?,
        endDate: Instant?,
        limit: Int
    ): Result<List<PartyEvent>> = runCatching {
        val remoteEvents = remoteDataSource.searchPartyEvents(
            query, location, radius, tags, startDate, endDate, limit
        ).getOrThrow()
        remoteEvents.map { mapper.toDomainPartyEvent(it) }
    }

    override suspend fun getPublicPartyEvents(limit: Int, offset: Int): Result<List<PartyEvent>> = runCatching {
        val remoteEvents = remoteDataSource.getPublicPartyEvents(limit, offset).getOrThrow()
        remoteEvents.map { mapper.toDomainPartyEvent(it) }
    }

    override suspend fun getTrendingPartyEvents(limit: Int): Result<List<PartyEvent>> = runCatching {
        val remoteEvents = remoteDataSource.getTrendingPartyEvents(limit).getOrThrow()
        remoteEvents.map { mapper.toDomainPartyEvent(it) }
    }

    override suspend fun getUpcomingPartyEvents(userId: String): Result<List<PartyEvent>> = runCatching {
        val remoteEvents = remoteDataSource.getUpcomingPartyEvents(userId).getOrThrow()
        val domainEvents = remoteEvents.map { mapper.toDomainPartyEvent(it) }
        localDataSource.savePartyEvents(domainEvents)
        domainEvents
    }

    override suspend fun getLivePartyEvents(): Result<List<PartyEvent>> = runCatching {
        val remoteEvents = remoteDataSource.getLivePartyEvents().getOrThrow()
        remoteEvents.map { mapper.toDomainPartyEvent(it) }
    }

    override suspend fun updatePartyStatus(eventId: String, status: PartyStatus): Result<Unit> = runCatching {
        remoteDataSource.updatePartyStatus(eventId, status).getOrThrow()
        localDataSource.updatePartyStatus(eventId, status)
    }

    override suspend fun addCoHost(eventId: String, coHostId: String): Result<Unit> = runCatching {
        remoteDataSource.addCoHost(eventId, coHostId).getOrThrow()
        localDataSource.addCoHost(eventId, coHostId)
    }

    override suspend fun removeCoHost(eventId: String, coHostId: String): Result<Unit> = runCatching {
        remoteDataSource.removeCoHost(eventId, coHostId).getOrThrow()
        localDataSource.removeCoHost(eventId, coHostId)
    }

    override fun observePartyEvent(eventId: String): Flow<PartyEvent?> {
        return remoteDataSource.observePartyEvent(eventId).map { remoteEvent ->
            remoteEvent?.let {
                val domainEvent = mapper.toDomainPartyEvent(it)
                localDataSource.savePartyEvent(domainEvent)
                domainEvent
            }
        }
    }

    override fun observeLivePartyEvents(): Flow<List<PartyEvent>> {
        return remoteDataSource.observeLivePartyEvents().map { remoteEvents ->
            remoteEvents.map { mapper.toDomainPartyEvent(it) }
        }
    }

    override fun observeUserPartyEvents(userId: String): Flow<List<PartyEvent>> {
        return remoteDataSource.observeUserPartyEvents(userId).map { remoteEvents ->
            val domainEvents = remoteEvents.map { mapper.toDomainPartyEvent(it) }
            localDataSource.savePartyEvents(domainEvents)
            domainEvents
        }
    }

    override suspend fun getPartyEventsByVenue(venue: Venue): Result<List<PartyEvent>> = runCatching {
        val remoteEvents = remoteDataSource.getPartyEventsByVenue(mapper.toRemoteVenue(venue)).getOrThrow()
        remoteEvents.map { mapper.toDomainPartyEvent(it) }
    }

    override suspend fun getPopularVenues(limit: Int): Result<List<Venue>> = runCatching {
        val remoteVenues = remoteDataSource.getPopularVenues(limit).getOrThrow()
        remoteVenues.map { mapper.toDomainVenue(it) }
    }
}