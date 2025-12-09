package com.partygallery.data.mapper

import com.partygallery.data.dto.MediaContentDto
import com.partygallery.data.dto.MediaUploadDto
import com.partygallery.domain.model.MediaContent
import com.partygallery.domain.model.MediaMetadata
import com.partygallery.domain.model.MediaSocialMetrics
import com.partygallery.domain.model.MediaType
import com.partygallery.domain.model.PartyMood
import kotlinx.datetime.Instant

/**
 * Mapper extensions for MediaContent DTO <-> Domain conversions.
 *
 * S2.5-007: Data mappers (DTO to Domain)
 */

// DTO -> Domain

fun MediaContentDto.toDomain(): MediaContent = MediaContent(
    id = id,
    partyEventId = partyEventId,
    uploaderId = creatorId,
    uploader = creator?.toDomain(),
    type = parseMediaType(type),
    url = url,
    thumbnailUrl = thumbnailUrl,
    caption = caption,
    mood = partyMood?.let { parsePartyMood(it) },
    tags = taggedUsers.map { it.username }, // Convert tagged users to tags
    metadata = MediaMetadata(
        width = width,
        height = height,
        durationSeconds = durationSeconds,
        sizeBytes = fileSizeBytes,
    ),
    socialMetrics = MediaSocialMetrics(
        likesCount = likesCount,
        commentsCount = commentsCount,
    ),
    isHighlight = isPartyMoment,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = updatedAt?.let { Instant.fromEpochMilliseconds(it) },
)

// Domain -> DTO

fun MediaContent.toDto(): MediaContentDto = MediaContentDto(
    id = id,
    partyEventId = partyEventId,
    creatorId = uploaderId,
    creator = uploader?.toDto(),
    type = type.name,
    url = url,
    thumbnailUrl = thumbnailUrl,
    caption = caption,
    partyMood = mood?.name,
    taggedUsers = emptyList(), // Tags would need to be resolved to UserSummaryDto separately
    likesCount = socialMetrics.likesCount,
    commentsCount = socialMetrics.commentsCount,
    isPartyMoment = isHighlight,
    durationSeconds = metadata.durationSeconds,
    fileSizeBytes = metadata.sizeBytes,
    width = metadata.width,
    height = metadata.height,
    createdAt = createdAt.toEpochMilliseconds(),
    updatedAt = updatedAt?.toEpochMilliseconds(),
)

// Upload status helpers

enum class MediaUploadStatus {
    PENDING,
    UPLOADING,
    COMPLETED,
    FAILED,
}

data class MediaUpload(
    val id: String,
    val localPath: String,
    val partyEventId: String,
    val type: MediaType,
    val caption: String?,
    val mood: PartyMood?,
    val status: MediaUploadStatus,
    val progressPercent: Int,
    val errorMessage: String?,
)

fun MediaUploadDto.toDomain(): MediaUpload = MediaUpload(
    id = id,
    localPath = localPath,
    partyEventId = partyEventId,
    type = parseMediaType(type),
    caption = caption,
    mood = partyMood?.let { parsePartyMood(it) },
    status = parseUploadStatus(status),
    progressPercent = progressPercent,
    errorMessage = errorMessage,
)

fun MediaUpload.toDto(): MediaUploadDto = MediaUploadDto(
    id = id,
    localPath = localPath,
    partyEventId = partyEventId,
    type = type.name,
    caption = caption,
    partyMood = mood?.name,
    status = status.name,
    progressPercent = progressPercent,
    errorMessage = errorMessage,
)

// Helper functions

private fun parseMediaType(type: String): MediaType {
    return try {
        MediaType.valueOf(type)
    } catch (e: Exception) {
        MediaType.PHOTO
    }
}

private fun parsePartyMood(mood: String): PartyMood? {
    return try {
        PartyMood.valueOf(mood)
    } catch (e: Exception) {
        null
    }
}

private fun parseUploadStatus(status: String): MediaUploadStatus {
    return try {
        MediaUploadStatus.valueOf(status)
    } catch (e: Exception) {
        MediaUploadStatus.PENDING
    }
}
