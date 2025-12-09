package com.partygallery.data.repository

import com.partygallery.data.datasource.MediaDataSource
import com.partygallery.data.datasource.UploadProgressDto
import com.partygallery.data.mapper.toDomain
import com.partygallery.data.mapper.toDto
import com.partygallery.domain.model.MediaContent
import com.partygallery.domain.model.MediaType
import com.partygallery.domain.model.PartyMood
import com.partygallery.domain.repository.MediaRepository
import com.partygallery.domain.repository.MediaUploadItem
import com.partygallery.domain.repository.UploadProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of MediaRepository using Firebase Storage.
 *
 * S2.5-003: MediaRepositoryImpl con Firebase Storage
 */
class MediaRepositoryImpl(
    private val mediaDataSource: MediaDataSource,
) : MediaRepository {

    // ============================================
    // CRUD Operations
    // ============================================

    override suspend fun createMedia(media: MediaContent): Result<MediaContent> {
        return try {
            val dto = media.toDto()
            val createdDto = mediaDataSource.createMedia(dto)
            Result.success(createdDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMediaById(mediaId: String): Result<MediaContent?> {
        return try {
            val dto = mediaDataSource.getMediaById(mediaId)
            Result.success(dto?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMedia(media: MediaContent): Result<MediaContent> {
        return try {
            val dto = media.toDto()
            val updatedDto = mediaDataSource.updateMedia(dto)
            Result.success(updatedDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMedia(mediaId: String): Result<Unit> {
        return try {
            mediaDataSource.deleteMedia(mediaId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Query Operations
    // ============================================

    override suspend fun getMediaByParty(partyId: String, limit: Int): Result<List<MediaContent>> {
        return try {
            val dtos = mediaDataSource.getMediaByParty(partyId, limit)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMediaByUser(userId: String, limit: Int): Result<List<MediaContent>> {
        return try {
            val dtos = mediaDataSource.getMediaByUser(userId, limit)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMediaByMood(mood: PartyMood, limit: Int): Result<List<MediaContent>> {
        return try {
            val dtos = mediaDataSource.getMediaByMood(mood.name, limit)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMediaByType(type: MediaType, limit: Int): Result<List<MediaContent>> {
        return try {
            val dtos = mediaDataSource.getMediaByType(type.name, limit)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecentMedia(limit: Int): Result<List<MediaContent>> {
        return try {
            val dtos = mediaDataSource.getRecentMedia(limit)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTrendingMedia(limit: Int): Result<List<MediaContent>> {
        return try {
            val dtos = mediaDataSource.getTrendingMedia(limit)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Upload Operations
    // ============================================

    override suspend fun uploadMedia(
        partyId: String,
        localPath: String,
        type: MediaType,
        mood: PartyMood?,
        caption: String?,
    ): Result<MediaContent> {
        return try {
            val dto = mediaDataSource.uploadMedia(
                partyId = partyId,
                localPath = localPath,
                type = type.name,
                mood = mood?.name,
                caption = caption,
            )
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadMultipleMedia(
        partyId: String,
        items: List<MediaUploadItem>,
    ): Result<List<MediaContent>> {
        return try {
            val results = items.map { item ->
                mediaDataSource.uploadMedia(
                    partyId = partyId,
                    localPath = item.localPath,
                    type = item.type.name,
                    mood = item.mood?.name,
                    caption = item.caption,
                )
            }
            Result.success(results.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeUploadProgress(uploadId: String): Flow<UploadProgress> {
        return mediaDataSource.observeUploadProgress(uploadId).map { dto ->
            dto.toDomain()
        }
    }

    // ============================================
    // Engagement
    // ============================================

    override suspend fun likeMedia(mediaId: String, userId: String): Result<Unit> {
        return try {
            mediaDataSource.likeMedia(mediaId, userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unlikeMedia(mediaId: String, userId: String): Result<Unit> {
        return try {
            mediaDataSource.unlikeMedia(mediaId, userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isLikedByUser(mediaId: String, userId: String): Result<Boolean> {
        return try {
            val isLiked = mediaDataSource.isLikedByUser(mediaId, userId)
            Result.success(isLiked)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLikesCount(mediaId: String): Result<Int> {
        return try {
            val count = mediaDataSource.getLikesCount(mediaId)
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Tags
    // ============================================

    override suspend fun tagUsers(mediaId: String, userIds: List<String>): Result<Unit> {
        return try {
            mediaDataSource.tagUsers(mediaId, userIds)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeUserTag(mediaId: String, userId: String): Result<Unit> {
        return try {
            mediaDataSource.removeUserTag(mediaId, userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTaggedUsers(mediaId: String): Result<List<String>> {
        return try {
            val users = mediaDataSource.getTaggedUsers(mediaId)
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Observable Flows
    // ============================================

    override fun observeMediaByParty(partyId: String): Flow<List<MediaContent>> {
        return mediaDataSource.observeMediaByParty(partyId).map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override fun observeLikesCount(mediaId: String): Flow<Int> {
        return mediaDataSource.observeLikesCount(mediaId)
    }

    override fun observeIsLiked(mediaId: String, userId: String): Flow<Boolean> {
        return mediaDataSource.observeIsLiked(mediaId, userId)
    }
}

// ============================================
// Helper Extension
// ============================================

private fun UploadProgressDto.toDomain(): UploadProgress {
    return when (status) {
        "PENDING" -> UploadProgress.Pending
        "UPLOADING" -> UploadProgress.Uploading(percentage)
        "PROCESSING" -> UploadProgress.Processing(step ?: "Processing...")
        "COMPLETED" -> {
            // Create a placeholder MediaContent for completed status
            // In real implementation, would fetch the actual media
            UploadProgress.Processing("Completed - media ID: $mediaId")
        }
        "FAILED" -> UploadProgress.Failed(Exception(errorMessage ?: "Upload failed"))
        else -> UploadProgress.Pending
    }
}
