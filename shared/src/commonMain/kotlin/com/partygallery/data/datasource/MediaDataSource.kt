package com.partygallery.data.datasource

import com.partygallery.data.dto.MediaContentDto
import kotlinx.coroutines.flow.Flow

/**
 * Data source interface for Media Content operations.
 * Platform-specific implementations will provide the actual Firebase Storage integration.
 *
 * S2.5-003: MediaRepositoryImpl con Firebase Storage
 */
interface MediaDataSource {

    // ============================================
    // CRUD Operations
    // ============================================

    suspend fun createMedia(media: MediaContentDto): MediaContentDto

    suspend fun getMediaById(mediaId: String): MediaContentDto?

    suspend fun updateMedia(media: MediaContentDto): MediaContentDto

    suspend fun deleteMedia(mediaId: String)

    // ============================================
    // Query Operations
    // ============================================

    suspend fun getMediaByParty(partyId: String, limit: Int): List<MediaContentDto>

    suspend fun getMediaByUser(userId: String, limit: Int): List<MediaContentDto>

    suspend fun getMediaByMood(mood: String, limit: Int): List<MediaContentDto>

    suspend fun getMediaByType(type: String, limit: Int): List<MediaContentDto>

    suspend fun getRecentMedia(limit: Int): List<MediaContentDto>

    suspend fun getTrendingMedia(limit: Int): List<MediaContentDto>

    // ============================================
    // Upload Operations
    // ============================================

    suspend fun uploadMedia(
        partyId: String,
        localPath: String,
        type: String,
        mood: String?,
        caption: String?,
    ): MediaContentDto

    fun observeUploadProgress(uploadId: String): Flow<UploadProgressDto>

    // ============================================
    // Engagement
    // ============================================

    suspend fun likeMedia(mediaId: String, userId: String)

    suspend fun unlikeMedia(mediaId: String, userId: String)

    suspend fun isLikedByUser(mediaId: String, userId: String): Boolean

    suspend fun getLikesCount(mediaId: String): Int

    // ============================================
    // Tags
    // ============================================

    suspend fun tagUsers(mediaId: String, userIds: List<String>)

    suspend fun removeUserTag(mediaId: String, userId: String)

    suspend fun getTaggedUsers(mediaId: String): List<String>

    // ============================================
    // Real-time Observers
    // ============================================

    fun observeMediaByParty(partyId: String): Flow<List<MediaContentDto>>

    fun observeLikesCount(mediaId: String): Flow<Int>

    fun observeIsLiked(mediaId: String, userId: String): Flow<Boolean>
}

/**
 * DTO for upload progress tracking.
 */
data class UploadProgressDto(
    // Valid values: PENDING, UPLOADING, PROCESSING, COMPLETED, FAILED
    val status: String,
    val percentage: Float = 0f,
    val step: String? = null,
    val mediaId: String? = null,
    val errorMessage: String? = null,
)
