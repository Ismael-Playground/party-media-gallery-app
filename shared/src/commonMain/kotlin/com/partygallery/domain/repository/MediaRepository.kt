package com.partygallery.domain.repository

import com.partygallery.domain.model.MediaContent
import com.partygallery.domain.model.MediaType
import com.partygallery.domain.model.PartyMood
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Media Content operations.
 *
 * S1-011: Interfaces base de repositorios
 */
interface MediaRepository {

    // ============================================
    // CRUD Operations
    // ============================================

    suspend fun createMedia(media: MediaContent): Result<MediaContent>
    suspend fun getMediaById(mediaId: String): Result<MediaContent?>
    suspend fun updateMedia(media: MediaContent): Result<MediaContent>
    suspend fun deleteMedia(mediaId: String): Result<Unit>

    // ============================================
    // Query Operations
    // ============================================

    suspend fun getMediaByParty(partyId: String, limit: Int = 50): Result<List<MediaContent>>
    suspend fun getMediaByUser(userId: String, limit: Int = 50): Result<List<MediaContent>>
    suspend fun getMediaByMood(mood: PartyMood, limit: Int = 50): Result<List<MediaContent>>
    suspend fun getMediaByType(type: MediaType, limit: Int = 50): Result<List<MediaContent>>
    suspend fun getRecentMedia(limit: Int = 20): Result<List<MediaContent>>
    suspend fun getTrendingMedia(limit: Int = 20): Result<List<MediaContent>>

    // ============================================
    // Upload Operations
    // ============================================

    suspend fun uploadMedia(
        partyId: String,
        localPath: String,
        type: MediaType,
        mood: PartyMood? = null,
        caption: String? = null,
    ): Result<MediaContent>

    suspend fun uploadMultipleMedia(partyId: String, items: List<MediaUploadItem>): Result<List<MediaContent>>

    fun observeUploadProgress(uploadId: String): Flow<UploadProgress>

    // ============================================
    // Engagement
    // ============================================

    suspend fun likeMedia(mediaId: String, userId: String): Result<Unit>
    suspend fun unlikeMedia(mediaId: String, userId: String): Result<Unit>
    suspend fun isLikedByUser(mediaId: String, userId: String): Result<Boolean>
    suspend fun getLikesCount(mediaId: String): Result<Int>

    // ============================================
    // Tags
    // ============================================

    suspend fun tagUsers(mediaId: String, userIds: List<String>): Result<Unit>
    suspend fun removeUserTag(mediaId: String, userId: String): Result<Unit>
    suspend fun getTaggedUsers(mediaId: String): Result<List<String>>

    // ============================================
    // Observable Flows
    // ============================================

    fun observeMediaByParty(partyId: String): Flow<List<MediaContent>>
    fun observeLikesCount(mediaId: String): Flow<Int>
    fun observeIsLiked(mediaId: String, userId: String): Flow<Boolean>
}

/**
 * Item for batch media upload.
 */
data class MediaUploadItem(
    val localPath: String,
    val type: MediaType,
    val mood: PartyMood? = null,
    val caption: String? = null,
)

/**
 * Upload progress state.
 */
sealed class UploadProgress {
    data object Pending : UploadProgress()
    data class Uploading(val percentage: Float) : UploadProgress()
    data class Processing(val step: String) : UploadProgress()
    data class Completed(val media: MediaContent) : UploadProgress()
    data class Failed(val error: Throwable) : UploadProgress()
}
