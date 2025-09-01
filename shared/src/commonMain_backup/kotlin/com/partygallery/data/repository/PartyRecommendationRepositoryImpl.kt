package com.partygallery.data.repository

import com.partygallery.domain.repository.PartyRecommendationRepository
import com.partygallery.domain.repository.UserRepository
import com.partygallery.domain.model.party.PartyEvent
import com.partygallery.domain.model.party.PartyRecommendation
import com.partygallery.domain.model.user.User
import com.partygallery.data.datasource.remote.PartyRecommendationRemoteDataSource
import com.partygallery.data.datasource.local.PartyRecommendationLocalDataSource
import com.partygallery.data.mapper.RecommendationMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PartyRecommendationRepositoryImpl(
    private val remoteDataSource: PartyRecommendationRemoteDataSource,
    private val localDataSource: PartyRecommendationLocalDataSource,
    private val userRepository: UserRepository,
    private val mapper: RecommendationMapper
) : PartyRecommendationRepository {

    override suspend fun getPartyRecommendationsForUser(userId: String, limit: Int): Result<List<PartyRecommendation>> = runCatching {
        val remoteRecommendations = remoteDataSource.getPartyRecommendationsForUser(userId, limit).getOrThrow()
        val domainRecommendations = remoteRecommendations.map { mapper.toDomainRecommendation(it) }
        localDataSource.saveRecommendations(userId, domainRecommendations)
        domainRecommendations
    }

    override suspend fun getPersonalizedPartyFeed(userId: String, limit: Int, offset: Int): Result<List<PartyEvent>> = runCatching {
        val remoteEvents = remoteDataSource.getPersonalizedPartyFeed(userId, limit, offset).getOrThrow()
        val domainEvents = remoteEvents.map { mapper.toDomainPartyEvent(it) }
        localDataSource.savePersonalizedFeed(userId, domainEvents)
        domainEvents
    }

    override suspend fun getSimilarPartyEvents(eventId: String, limit: Int): Result<List<PartyEvent>> = runCatching {
        val remoteEvents = remoteDataSource.getSimilarPartyEvents(eventId, limit).getOrThrow()
        remoteEvents.map { mapper.toDomainPartyEvent(it) }
    }

    override suspend fun getRecommendedPartyCreators(userId: String, limit: Int): Result<List<User>> = runCatching {
        val remoteUsers = remoteDataSource.getRecommendedPartyCreators(userId, limit).getOrThrow()
        remoteUsers.map { mapper.toDomainUser(it) }
    }

    override suspend fun getRecommendedPartyAttendees(userId: String, limit: Int): Result<List<User>> = runCatching {
        val remoteUsers = remoteDataSource.getRecommendedPartyAttendees(userId, limit).getOrThrow()
        remoteUsers.map { mapper.toDomainUser(it) }
    }

    override suspend fun getPartyRecommendationsByLocation(
        userId: String,
        latitude: Double,
        longitude: Double,
        radiusKm: Double,
        limit: Int
    ): Result<List<PartyEvent>> = runCatching {
        val remoteEvents = remoteDataSource.getPartyRecommendationsByLocation(
            userId, latitude, longitude, radiusKm, limit
        ).getOrThrow()
        remoteEvents.map { mapper.toDomainPartyEvent(it) }
    }

    override suspend fun getPartyRecommendationsByTags(
        userId: String,
        tags: List<String>,
        limit: Int
    ): Result<List<PartyEvent>> = runCatching {
        val remoteEvents = remoteDataSource.getPartyRecommendationsByTags(userId, tags, limit).getOrThrow()
        remoteEvents.map { mapper.toDomainPartyEvent(it) }
    }

    override suspend fun getPartyRecommendationsByMusicGenre(
        userId: String,
        genres: List<String>,
        limit: Int
    ): Result<List<PartyEvent>> = runCatching {
        val remoteEvents = remoteDataSource.getPartyRecommendationsByMusicGenre(userId, genres, limit).getOrThrow()
        remoteEvents.map { mapper.toDomainPartyEvent(it) }
    }

    override suspend fun recordPartyInteraction(
        userId: String,
        partyEventId: String,
        interactionType: String,
        weight: Double
    ): Result<Unit> = runCatching {
        localDataSource.recordPartyInteraction(userId, partyEventId, interactionType, weight)
        remoteDataSource.recordPartyInteraction(userId, partyEventId, interactionType, weight).getOrThrow()
    }

    override suspend fun recordPartyPreference(
        userId: String,
        preferenceType: String,
        value: String,
        weight: Double
    ): Result<Unit> = runCatching {
        localDataSource.recordPartyPreference(userId, preferenceType, value, weight)
        remoteDataSource.recordPartyPreference(userId, preferenceType, value, weight).getOrThrow()
    }

    override suspend fun getPartyDiscoveryFeed(userId: String, limit: Int): Result<List<PartyEvent>> = runCatching {
        val remoteEvents = remoteDataSource.getPartyDiscoveryFeed(userId, limit).getOrThrow()
        val domainEvents = remoteEvents.map { mapper.toDomainPartyEvent(it) }
        localDataSource.saveDiscoveryFeed(userId, domainEvents)
        domainEvents
    }

    override suspend fun getExplorePartyFeed(userId: String, limit: Int): Result<List<PartyEvent>> = runCatching {
        val remoteEvents = remoteDataSource.getExplorePartyFeed(userId, limit).getOrThrow()
        remoteEvents.map { mapper.toDomainPartyEvent(it) }
    }

    override suspend fun updateRecommendationModel(userId: String): Result<Unit> = runCatching {
        remoteDataSource.updateRecommendationModel(userId).getOrThrow()
    }

    override suspend fun refreshUserRecommendations(userId: String): Result<Unit> = runCatching {
        remoteDataSource.refreshUserRecommendations(userId).getOrThrow()
        localDataSource.clearUserRecommendations(userId)
    }

    override fun observePartyRecommendations(userId: String): Flow<List<PartyRecommendation>> {
        return remoteDataSource.observePartyRecommendations(userId).map { remoteRecommendations ->
            val domainRecommendations = remoteRecommendations.map { mapper.toDomainRecommendation(it) }
            localDataSource.saveRecommendations(userId, domainRecommendations)
            domainRecommendations
        }
    }

    override fun observePersonalizedFeed(userId: String): Flow<List<PartyEvent>> {
        return remoteDataSource.observePersonalizedFeed(userId).map { remoteEvents ->
            val domainEvents = remoteEvents.map { mapper.toDomainPartyEvent(it) }
            localDataSource.savePersonalizedFeed(userId, domainEvents)
            domainEvents
        }
    }

    override fun observeDiscoveryFeed(userId: String): Flow<List<PartyEvent>> {
        return remoteDataSource.observeDiscoveryFeed(userId).map { remoteEvents ->
            val domainEvents = remoteEvents.map { mapper.toDomainPartyEvent(it) }
            localDataSource.saveDiscoveryFeed(userId, domainEvents)
            domainEvents
        }
    }
}