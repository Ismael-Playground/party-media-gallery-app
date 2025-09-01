package com.partygallery.domain.repository

import com.partygallery.domain.model.media.PartyMediaContent
import com.partygallery.domain.model.media.MediaType
import com.partygallery.domain.model.media.PartyMood
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface MediaRepository {
    suspend fun uploadMedia(
        content: ByteArray,
        filename: String,
        mediaType: MediaType,
        partyEventId: String?,
        metadata: Map<String, Any> = emptyMap()
    ): Result<String> // Returns media URL
    
    suspend fun createMediaContent(media: PartyMediaContent): Result<PartyMediaContent>
    suspend fun getMediaContentById(mediaId: String): Result<PartyMediaContent?>
    suspend fun updateMediaContent(media: PartyMediaContent): Result<PartyMediaContent>
    suspend fun deleteMediaContent(mediaId: String): Result<Unit>
    
    suspend fun getMediaByUser(userId: String, limit: Int = 20, offset: Int = 0): Result<List<PartyMediaContent>>
    suspend fun getMediaByPartyEvent(eventId: String, limit: Int = 50): Result<List<PartyMediaContent>>
    suspend fun getMediaByType(mediaType: MediaType, limit: Int = 20): Result<List<PartyMediaContent>>
    suspend fun getMediaByMood(mood: PartyMood, limit: Int = 20): Result<List<PartyMediaContent>>
    
    suspend fun getPublicPartyMedia(limit: Int = 20, offset: Int = 0): Result<List<PartyMediaContent>>
    suspend fun getTrendingPartyMedia(limit: Int = 20): Result<List<PartyMediaContent>>
    suspend fun getPartyFeedMedia(userId: String, limit: Int = 20, offset: Int = 0): Result<List<PartyMediaContent>>
    
    suspend fun searchMediaContent(
        query: String,
        mediaType: MediaType? = null,
        tags: List<String>? = null,
        dateRange: Pair<Instant, Instant>? = null,
        limit: Int = 20
    ): Result<List<PartyMediaContent>>
    
    suspend fun likeMedia(mediaId: String, userId: String): Result<Unit>
    suspend fun unlikeMedia(mediaId: String, userId: String): Result<Unit>
    suspend fun isMediaLikedByUser(mediaId: String, userId: String): Result<Boolean>
    
    suspend fun addMediaToFavorites(mediaId: String, userId: String): Result<Unit>
    suspend fun removeMediaFromFavorites(mediaId: String, userId: String): Result<Unit>
    suspend fun getUserFavoriteMedia(userId: String, limit: Int = 20): Result<List<PartyMediaContent>>
    
    suspend fun tagPeopleInMedia(mediaId: String, userIds: List<String>): Result<Unit>
    suspend fun getMediaWithUserTag(userId: String): Result<List<PartyMediaContent>>
    
    suspend fun generateThumbnail(mediaUrl: String, mediaType: MediaType): Result<String>
    suspend fun compressMedia(mediaUrl: String, quality: Float): Result<String>
    
    fun observeMediaContent(mediaId: String): Flow<PartyMediaContent?>
    fun observePartyEventMedia(eventId: String): Flow<List<PartyMediaContent>>
    fun observeLivePartyMedia(): Flow<List<PartyMediaContent>>
    fun observeUserMedia(userId: String): Flow<List<PartyMediaContent>>
}