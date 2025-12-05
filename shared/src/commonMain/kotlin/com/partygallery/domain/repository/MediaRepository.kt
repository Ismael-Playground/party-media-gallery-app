package com.partygallery.domain.repository

import com.partygallery.domain.model.MediaContent
import com.partygallery.domain.model.PartyMood
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for media content operations.
 *
 * S4-016: MediaRepository interface
 */
interface MediaRepository {

    /**
     * Get media content by ID.
     */
    suspend fun getMediaById(mediaId: String): Result<MediaContent>

    /**
     * Get all media for a party event.
     */
    fun getPartyMedia(partyId: String): Flow<List<MediaContent>>

    /**
     * Get media uploaded by a user.
     */
    fun getUserMedia(userId: String): Flow<List<MediaContent>>

    /**
     * Get media where a user is tagged.
     */
    fun getTaggedMedia(userId: String): Flow<List<MediaContent>>

    /**
     * Get liked media for a user.
     */
    fun getLikedMedia(userId: String): Flow<List<MediaContent>>

    /**
     * Upload media content.
     *
     * @param partyId The party this media belongs to
     * @param localUri Local URI of the media file
     * @param type Type of media (PHOTO, VIDEO, AUDIO)
     * @param mood Optional party mood
     * @param caption Optional caption
     * @return Flow of upload progress (0.0 to 1.0) and final MediaContent
     */
    fun uploadMedia(
        partyId: String,
        localUri: String,
        type: MediaType,
        mood: PartyMood? = null,
        caption: String? = null,
    ): Flow<UploadProgress>

    /**
     * Delete media content.
     */
    suspend fun deleteMedia(mediaId: String): Result<Unit>

    /**
     * Like media content.
     */
    suspend fun likeMedia(mediaId: String): Result<Unit>

    /**
     * Unlike media content.
     */
    suspend fun unlikeMedia(mediaId: String): Result<Unit>

    /**
     * Tag users in media.
     */
    suspend fun tagUsers(mediaId: String, userIds: List<String>): Result<Unit>

    /**
     * Remove user tag from media.
     */
    suspend fun untagUser(mediaId: String, userId: String): Result<Unit>

    /**
     * Update media caption.
     */
    suspend fun updateCaption(mediaId: String, caption: String): Result<Unit>

    /**
     * Update media mood.
     */
    suspend fun updateMood(mediaId: String, mood: PartyMood?): Result<Unit>

    /**
     * Download media to local storage.
     */
    suspend fun downloadMedia(mediaId: String, destinationPath: String): Result<String>

    /**
     * Get upload URL for direct upload to storage.
     */
    suspend fun getUploadUrl(
        fileName: String,
        contentType: String,
    ): Result<UploadUrlInfo>

    /**
     * Confirm upload completion.
     */
    suspend fun confirmUpload(
        uploadId: String,
        partyId: String,
        type: MediaType,
        mood: PartyMood? = null,
        caption: String? = null,
    ): Result<MediaContent>

    /**
     * Compress image before upload.
     */
    suspend fun compressImage(
        localUri: String,
        maxWidth: Int = 1920,
        maxHeight: Int = 1920,
        quality: Int = 85,
    ): Result<String>

    /**
     * Compress video before upload.
     */
    suspend fun compressVideo(
        localUri: String,
        maxDurationSeconds: Int = 120,
        maxBitrate: Int = 4_000_000,
    ): Result<String>

    /**
     * Generate thumbnail for video.
     */
    suspend fun generateThumbnail(videoUri: String): Result<String>
}

/**
 * Media type enum.
 */
enum class MediaType {
    PHOTO,
    VIDEO,
    AUDIO,
}

/**
 * Upload progress state.
 */
sealed class UploadProgress {
    data class Progress(val fraction: Float) : UploadProgress()
    data class Compressing(val fraction: Float) : UploadProgress()
    data class Uploading(val fraction: Float) : UploadProgress()
    data class Processing(val message: String = "Processing...") : UploadProgress()
    data class Success(val media: MediaContent) : UploadProgress()
    data class Error(val message: String, val cause: Throwable? = null) : UploadProgress()
}

/**
 * Upload URL information for direct upload.
 */
data class UploadUrlInfo(
    val uploadId: String,
    val uploadUrl: String,
    val expiresAt: Long,
    val headers: Map<String, String> = emptyMap(),
)
