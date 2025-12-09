package com.partygallery.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for MediaContent entity.
 * Used for Firebase Firestore serialization/deserialization.
 *
 * S2.5-007: Data mappers (DTO to Domain)
 */
@Serializable
data class MediaContentDto(
    val id: String = "",
    @SerialName("party_event_id")
    val partyEventId: String = "",
    @SerialName("creator_id")
    val creatorId: String = "",
    val creator: UserSummaryDto? = null,
    // Valid values: PHOTO, VIDEO, AUDIO, DOCUMENT
    val type: String = "PHOTO",
    val url: String = "",
    @SerialName("thumbnail_url")
    val thumbnailUrl: String? = null,
    val caption: String? = null,
    // Valid values: HYPE, CHILL, WILD, ROMANTIC, CRAZY, ELEGANT
    @SerialName("party_mood")
    val partyMood: String? = null,
    @SerialName("tagged_users")
    val taggedUsers: List<UserSummaryDto> = emptyList(),
    @SerialName("likes_count")
    val likesCount: Int = 0,
    @SerialName("comments_count")
    val commentsCount: Int = 0,
    @SerialName("is_party_moment")
    val isPartyMoment: Boolean = false,
    // For video/audio content
    @SerialName("duration_seconds")
    val durationSeconds: Int? = null,
    @SerialName("file_size_bytes")
    val fileSizeBytes: Long? = null,
    val width: Int? = null,
    val height: Int? = null,
    @SerialName("created_at")
    val createdAt: Long = 0L,
    @SerialName("updated_at")
    val updatedAt: Long? = null,
)

/**
 * DTO for media upload progress tracking
 */
@Serializable
data class MediaUploadDto(
    val id: String = "",
    @SerialName("local_path")
    val localPath: String = "",
    @SerialName("party_event_id")
    val partyEventId: String = "",
    val type: String = "PHOTO",
    val caption: String? = null,
    @SerialName("party_mood")
    val partyMood: String? = null,
    // Valid values: PENDING, UPLOADING, COMPLETED, FAILED
    val status: String = "PENDING",
    @SerialName("progress_percent")
    val progressPercent: Int = 0,
    @SerialName("error_message")
    val errorMessage: String? = null,
)
