package com.partygallery.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for User entity.
 * Used for Firebase Firestore serialization/deserialization.
 *
 * S2.5-007: Data mappers (DTO to Domain)
 */
@Serializable
data class UserDto(
    val id: String = "",
    @SerialName("firebase_id")
    val firebaseId: String = "",
    val email: String = "",
    val username: String = "",
    @SerialName("first_name")
    val firstName: String = "",
    @SerialName("last_name")
    val lastName: String = "",
    val bio: String? = null,
    @SerialName("birth_date")
    val birthDate: String? = null, // ISO-8601 format: "2000-01-15"
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("cover_photo_url")
    val coverPhotoUrl: String? = null,
    @SerialName("phone_number")
    val phoneNumber: String? = null,
    @SerialName("social_links")
    val socialLinks: SocialLinksDto = SocialLinksDto(),
    val tags: List<String> = emptyList(),
    @SerialName("followers_count")
    val followersCount: Int = 0,
    @SerialName("following_count")
    val followingCount: Int = 0,
    @SerialName("is_verified")
    val isVerified: Boolean = false,
    @SerialName("is_profile_complete")
    val isProfileComplete: Boolean = false,
    @SerialName("notifications_enabled")
    val notificationsEnabled: Boolean = true,
    @SerialName("created_at")
    val createdAt: Long = 0L, // Unix timestamp millis
    @SerialName("updated_at")
    val updatedAt: Long? = null,
)

@Serializable
data class SocialLinksDto(
    val instagram: String? = null,
    val tiktok: String? = null,
    val twitter: String? = null,
    val facebook: String? = null,
    val pinterest: String? = null,
    val spotify: String? = null,
)

/**
 * Lightweight DTO for user summaries in lists
 */
@Serializable
data class UserSummaryDto(
    val id: String = "",
    val username: String = "",
    @SerialName("first_name")
    val firstName: String = "",
    @SerialName("last_name")
    val lastName: String = "",
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("is_verified")
    val isVerified: Boolean = false,
)
