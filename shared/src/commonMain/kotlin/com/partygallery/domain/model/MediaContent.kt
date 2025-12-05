package com.partygallery.domain.model

import kotlinx.datetime.Instant

/**
 * Type of media content.
 */
enum class MediaType {
    PHOTO,
    VIDEO,
    AUDIO,
    DOCUMENT,
}

/**
 * Party mood associated with media content.
 * Used for filtering and mood-based galleries.
 *
 * S1-009: Modelo MediaContent (domain)
 */
enum class PartyMood(val displayName: String, val emoji: String) {
    HYPE("Hype", "\uD83D\uDD25"), // Fire emoji
    CHILL("Chill", "\uD83D\uDE0E"), // Cool emoji
    WILD("Wild", "\uD83E\uDD2A"), // Crazy face
    ROMANTIC("Romantic", "\u2764\uFE0F"), // Red heart
    CRAZY("Crazy", "\uD83C\uDF89"), // Party popper
    ELEGANT("Elegant", "\u2728"), // Sparkles
}

/**
 * Media metadata for video/audio files.
 */
data class MediaMetadata(
    val width: Int? = null,
    val height: Int? = null,
    val durationSeconds: Int? = null,
    val mimeType: String? = null,
    val sizeBytes: Long? = null,
)

/**
 * Social metrics for media content.
 */
data class MediaSocialMetrics(
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val viewsCount: Int = 0,
    val isLikedByUser: Boolean = false,
    val isSavedByUser: Boolean = false,
)

/**
 * Media content domain model.
 *
 * S1-009: Modelo MediaContent (domain)
 */
data class MediaContent(
    val id: String,
    val partyEventId: String,
    val uploaderId: String,
    val uploader: UserSummary? = null,
    val type: MediaType,
    val url: String,
    val thumbnailUrl: String? = null,
    val caption: String? = null,
    val mood: PartyMood? = null,
    val tags: List<String> = emptyList(),
    val metadata: MediaMetadata = MediaMetadata(),
    val socialMetrics: MediaSocialMetrics = MediaSocialMetrics(),
    val isHighlight: Boolean = false,
    val isFeatured: Boolean = false,
    val capturedAt: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant? = null,
) {
    val isVideo: Boolean
        get() = type == MediaType.VIDEO

    val isPhoto: Boolean
        get() = type == MediaType.PHOTO

    val hasAudio: Boolean
        get() = type == MediaType.AUDIO || type == MediaType.VIDEO

    val aspectRatio: Float?
        get() = if (metadata.width != null && metadata.height != null && metadata.height > 0) {
            metadata.width.toFloat() / metadata.height.toFloat()
        } else {
            null
        }

    val durationFormatted: String?
        get() = metadata.durationSeconds?.let { seconds ->
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            "%d:%02d".format(minutes, remainingSeconds)
        }
}

/**
 * Simplified media content for gallery grids.
 */
data class MediaContentThumbnail(
    val id: String,
    val thumbnailUrl: String?,
    val type: MediaType,
    val mood: PartyMood?,
    val isVideo: Boolean,
    val durationSeconds: Int?,
)

/**
 * Extension to convert MediaContent to thumbnail representation.
 */
fun MediaContent.toThumbnail(): MediaContentThumbnail = MediaContentThumbnail(
    id = id,
    thumbnailUrl = thumbnailUrl ?: url,
    type = type,
    mood = mood,
    isVideo = isVideo,
    durationSeconds = metadata.durationSeconds,
)
