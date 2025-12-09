package com.partygallery.data.datasource

import com.partygallery.data.dto.MediaContentDto
import com.partygallery.data.dto.UserSummaryDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

/**
 * Mock implementation of MediaDataSource for development and testing.
 * Will be replaced with FirebaseMediaDataSource in production.
 *
 * S2.5-003: MediaRepositoryImpl con Firebase Storage
 */
class MockMediaDataSource : MediaDataSource {

    // In-memory storage
    private val mediaItems = MutableStateFlow<Map<String, MediaContentDto>>(createInitialMedia())
    private val likes = MutableStateFlow<Map<String, Set<String>>>(emptyMap()) // mediaId -> Set<userId>
    private val tags = MutableStateFlow<Map<String, List<String>>>(emptyMap()) // mediaId -> List<userId>

    // Simulate network delay
    private suspend fun simulateNetworkDelay() {
        delay(300)
    }

    // ============================================
    // CRUD Operations
    // ============================================

    override suspend fun createMedia(media: MediaContentDto): MediaContentDto {
        simulateNetworkDelay()
        val now = Clock.System.now().toEpochMilliseconds()
        val newMedia = media.copy(
            id = "media_${System.currentTimeMillis()}",
            createdAt = now,
            updatedAt = now,
        )
        mediaItems.value = mediaItems.value + (newMedia.id to newMedia)
        return newMedia
    }

    override suspend fun getMediaById(mediaId: String): MediaContentDto? {
        simulateNetworkDelay()
        return mediaItems.value[mediaId]
    }

    override suspend fun updateMedia(media: MediaContentDto): MediaContentDto {
        simulateNetworkDelay()
        val updatedMedia = media.copy(
            updatedAt = Clock.System.now().toEpochMilliseconds(),
        )
        mediaItems.value = mediaItems.value + (media.id to updatedMedia)
        return updatedMedia
    }

    override suspend fun deleteMedia(mediaId: String) {
        simulateNetworkDelay()
        mediaItems.value = mediaItems.value - mediaId
        likes.value = likes.value - mediaId
        tags.value = tags.value - mediaId
    }

    // ============================================
    // Query Operations
    // ============================================

    override suspend fun getMediaByParty(partyId: String, limit: Int): List<MediaContentDto> {
        simulateNetworkDelay()
        return mediaItems.value.values
            .filter { it.partyEventId == partyId }
            .sortedByDescending { it.createdAt }
            .take(limit)
    }

    override suspend fun getMediaByUser(userId: String, limit: Int): List<MediaContentDto> {
        simulateNetworkDelay()
        return mediaItems.value.values
            .filter { it.creatorId == userId }
            .sortedByDescending { it.createdAt }
            .take(limit)
    }

    override suspend fun getMediaByMood(mood: String, limit: Int): List<MediaContentDto> {
        simulateNetworkDelay()
        return mediaItems.value.values
            .filter { it.partyMood == mood }
            .sortedByDescending { it.createdAt }
            .take(limit)
    }

    override suspend fun getMediaByType(type: String, limit: Int): List<MediaContentDto> {
        simulateNetworkDelay()
        return mediaItems.value.values
            .filter { it.type == type }
            .sortedByDescending { it.createdAt }
            .take(limit)
    }

    override suspend fun getRecentMedia(limit: Int): List<MediaContentDto> {
        simulateNetworkDelay()
        return mediaItems.value.values
            .sortedByDescending { it.createdAt }
            .take(limit)
    }

    override suspend fun getTrendingMedia(limit: Int): List<MediaContentDto> {
        simulateNetworkDelay()
        return mediaItems.value.values
            .sortedByDescending { it.likesCount + it.commentsCount }
            .take(limit)
    }

    // ============================================
    // Upload Operations
    // ============================================

    override suspend fun uploadMedia(
        partyId: String,
        localPath: String,
        type: String,
        mood: String?,
        caption: String?,
    ): MediaContentDto {
        simulateNetworkDelay()
        delay(1000) // Simulate upload time

        val now = Clock.System.now().toEpochMilliseconds()
        val newMedia = MediaContentDto(
            id = "media_${System.currentTimeMillis()}",
            partyEventId = partyId,
            creatorId = "user1", // Would come from auth
            creator = UserSummaryDto(
                id = "user1",
                username = "partyking",
                firstName = "Test",
                lastName = "User",
            ),
            type = type,
            url = "https://picsum.photos/seed/${System.currentTimeMillis()}/800/600",
            thumbnailUrl = "https://picsum.photos/seed/${System.currentTimeMillis()}/400/300",
            caption = caption,
            partyMood = mood,
            createdAt = now,
        )

        mediaItems.value = mediaItems.value + (newMedia.id to newMedia)
        return newMedia
    }

    override fun observeUploadProgress(uploadId: String): Flow<UploadProgressDto> {
        return flow {
            emit(UploadProgressDto(status = "PENDING"))
            delay(500)
            for (i in 1..10) {
                emit(UploadProgressDto(status = "UPLOADING", percentage = i * 10f))
                delay(200)
            }
            emit(UploadProgressDto(status = "PROCESSING", step = "Optimizing image"))
            delay(500)
            emit(UploadProgressDto(status = "COMPLETED", mediaId = uploadId))
        }
    }

    // ============================================
    // Engagement
    // ============================================

    override suspend fun likeMedia(mediaId: String, userId: String) {
        simulateNetworkDelay()
        val currentLikes = likes.value[mediaId]?.toMutableSet() ?: mutableSetOf()
        currentLikes.add(userId)
        likes.value = likes.value + (mediaId to currentLikes)

        // Update likes count
        mediaItems.value[mediaId]?.let { media ->
            val updated = media.copy(likesCount = currentLikes.size)
            mediaItems.value = mediaItems.value + (mediaId to updated)
        }
    }

    override suspend fun unlikeMedia(mediaId: String, userId: String) {
        simulateNetworkDelay()
        val currentLikes = likes.value[mediaId]?.toMutableSet() ?: return
        currentLikes.remove(userId)
        likes.value = likes.value + (mediaId to currentLikes)

        // Update likes count
        mediaItems.value[mediaId]?.let { media ->
            val updated = media.copy(likesCount = currentLikes.size)
            mediaItems.value = mediaItems.value + (mediaId to updated)
        }
    }

    override suspend fun isLikedByUser(mediaId: String, userId: String): Boolean {
        simulateNetworkDelay()
        return likes.value[mediaId]?.contains(userId) == true
    }

    override suspend fun getLikesCount(mediaId: String): Int {
        simulateNetworkDelay()
        return likes.value[mediaId]?.size ?: 0
    }

    // ============================================
    // Tags
    // ============================================

    override suspend fun tagUsers(mediaId: String, userIds: List<String>) {
        simulateNetworkDelay()
        val currentTags = tags.value[mediaId]?.toMutableList() ?: mutableListOf()
        currentTags.addAll(userIds.filter { it !in currentTags })
        tags.value = tags.value + (mediaId to currentTags)
    }

    override suspend fun removeUserTag(mediaId: String, userId: String) {
        simulateNetworkDelay()
        val currentTags = tags.value[mediaId]?.toMutableList() ?: return
        currentTags.remove(userId)
        tags.value = tags.value + (mediaId to currentTags)
    }

    override suspend fun getTaggedUsers(mediaId: String): List<String> {
        simulateNetworkDelay()
        return tags.value[mediaId] ?: emptyList()
    }

    // ============================================
    // Real-time Observers
    // ============================================

    override fun observeMediaByParty(partyId: String): Flow<List<MediaContentDto>> {
        return mediaItems.map { items ->
            items.values
                .filter { it.partyEventId == partyId }
                .sortedByDescending { it.createdAt }
        }
    }

    override fun observeLikesCount(mediaId: String): Flow<Int> {
        return likes.map { it[mediaId]?.size ?: 0 }
    }

    override fun observeIsLiked(mediaId: String, userId: String): Flow<Boolean> {
        return likes.map { it[mediaId]?.contains(userId) == true }
    }

    // ============================================
    // Mock Data
    // ============================================

    private fun createInitialMedia(): Map<String, MediaContentDto> {
        val now = Clock.System.now().toEpochMilliseconds()
        val hourInMillis = 60 * 60 * 1000L

        val media1 = MediaContentDto(
            id = "media1",
            partyEventId = "party2",
            creatorId = "user1",
            creator = UserSummaryDto(
                id = "user1",
                username = "partyking",
                firstName = "Test",
                lastName = "User",
                avatarUrl = "https://i.pravatar.cc/150?u=user1",
                isVerified = true,
            ),
            type = "PHOTO",
            url = "https://picsum.photos/seed/media1/800/600",
            thumbnailUrl = "https://picsum.photos/seed/media1/400/300",
            caption = "Amazing vibes at the techno night!",
            partyMood = "HYPE",
            likesCount = 45,
            commentsCount = 12,
            isPartyMoment = true,
            createdAt = now - (2 * hourInMillis),
        )

        val media2 = MediaContentDto(
            id = "media2",
            partyEventId = "party2",
            creatorId = "user2",
            creator = UserSummaryDto(
                id = "user2",
                username = "djmaster",
                firstName = "DJ",
                lastName = "Master",
                avatarUrl = "https://i.pravatar.cc/150?u=user2",
            ),
            type = "VIDEO",
            url = "https://picsum.photos/seed/media2/800/600",
            thumbnailUrl = "https://picsum.photos/seed/media2/400/300",
            caption = "Drop the bass!",
            partyMood = "WILD",
            likesCount = 128,
            commentsCount = 34,
            durationSeconds = 30,
            isPartyMoment = true,
            createdAt = now - hourInMillis,
        )

        val media3 = MediaContentDto(
            id = "media3",
            partyEventId = "party4",
            creatorId = "user3",
            creator = UserSummaryDto(
                id = "user3",
                username = "clubqueen",
                firstName = "Club",
                lastName = "Queen",
                avatarUrl = "https://i.pravatar.cc/150?u=user3",
            ),
            type = "PHOTO",
            url = "https://picsum.photos/seed/media3/800/600",
            thumbnailUrl = "https://picsum.photos/seed/media3/400/300",
            caption = "NYE countdown was epic!",
            partyMood = "CRAZY",
            likesCount = 256,
            commentsCount = 67,
            isPartyMoment = false,
            createdAt = now - (30 * 24 * hourInMillis),
        )

        val media4 = MediaContentDto(
            id = "media4",
            partyEventId = "party1",
            creatorId = "user1",
            creator = UserSummaryDto(
                id = "user1",
                username = "partyking",
                firstName = "Test",
                lastName = "User",
                avatarUrl = "https://i.pravatar.cc/150?u=user1",
                isVerified = true,
            ),
            type = "PHOTO",
            url = "https://picsum.photos/seed/media4/800/600",
            thumbnailUrl = "https://picsum.photos/seed/media4/400/300",
            caption = "Rooftop views",
            partyMood = "CHILL",
            likesCount = 89,
            commentsCount = 8,
            createdAt = now - (30 * 60 * 1000L), // 30 minutes ago
        )

        val media5 = MediaContentDto(
            id = "media5",
            partyEventId = "party3",
            creatorId = "user2",
            creator = UserSummaryDto(
                id = "user2",
                username = "djmaster",
                firstName = "DJ",
                lastName = "Master",
                avatarUrl = "https://i.pravatar.cc/150?u=user2",
            ),
            type = "PHOTO",
            url = "https://picsum.photos/seed/media5/800/600",
            thumbnailUrl = "https://picsum.photos/seed/media5/400/300",
            caption = "R&B vibes",
            partyMood = "ROMANTIC",
            likesCount = 167,
            commentsCount = 23,
            createdAt = now - (5 * hourInMillis),
        )

        return mapOf(
            media1.id to media1,
            media2.id to media2,
            media3.id to media3,
            media4.id to media4,
            media5.id to media5,
        )
    }
}
