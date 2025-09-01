package com.partygallery.domain.repository

import com.partygallery.domain.model.party.PartyEvent
import com.partygallery.domain.model.party.PartyRecommendation
import com.partygallery.domain.model.user.User
import kotlinx.coroutines.flow.Flow

interface PartyRecommendationRepository {
    suspend fun getPartyRecommendationsForUser(userId: String, limit: Int = 20): Result<List<PartyRecommendation>>
    suspend fun getPersonalizedPartyFeed(userId: String, limit: Int = 20, offset: Int = 0): Result<List<PartyEvent>>
    suspend fun getSimilarPartyEvents(eventId: String, limit: Int = 10): Result<List<PartyEvent>>
    
    suspend fun getRecommendedPartyCreators(userId: String, limit: Int = 10): Result<List<User>>
    suspend fun getRecommendedPartyAttendees(userId: String, limit: Int = 20): Result<List<User>>
    
    suspend fun getPartyRecommendationsByLocation(
        userId: String,
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 10.0,
        limit: Int = 15
    ): Result<List<PartyEvent>>
    
    suspend fun getPartyRecommendationsByTags(
        userId: String,
        tags: List<String>,
        limit: Int = 15
    ): Result<List<PartyEvent>>
    
    suspend fun getPartyRecommendationsByMusicGenre(
        userId: String,
        genres: List<String>,
        limit: Int = 15
    ): Result<List<PartyEvent>>
    
    suspend fun recordPartyInteraction(
        userId: String,
        partyEventId: String,
        interactionType: String,
        weight: Double = 1.0
    ): Result<Unit>
    
    suspend fun recordPartyPreference(
        userId: String,
        preferenceType: String,
        value: String,
        weight: Double = 1.0
    ): Result<Unit>
    
    suspend fun getPartyDiscoveryFeed(userId: String, limit: Int = 20): Result<List<PartyEvent>>
    suspend fun getExplorePartyFeed(userId: String, limit: Int = 20): Result<List<PartyEvent>>
    
    suspend fun updateRecommendationModel(userId: String): Result<Unit>
    suspend fun refreshUserRecommendations(userId: String): Result<Unit>
    
    fun observePartyRecommendations(userId: String): Flow<List<PartyRecommendation>>
    fun observePersonalizedFeed(userId: String): Flow<List<PartyEvent>>
    fun observeDiscoveryFeed(userId: String): Flow<List<PartyEvent>>
}