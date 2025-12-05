package com.partygallery.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * User domain model representing a Party Gallery user.
 *
 * S1-007: Modelo User (domain)
 */
data class User(
    val id: String,
    val firebaseId: String,
    val email: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val bio: String? = null,
    val birthDate: LocalDate? = null,
    val avatarUrl: String? = null,
    val coverPhotoUrl: String? = null,
    val phoneNumber: String? = null,
    val socialLinks: SocialLinks = SocialLinks(),
    val tags: List<String> = emptyList(),
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isVerified: Boolean = false,
    val isProfileComplete: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val createdAt: Instant,
    val updatedAt: Instant? = null,
) {
    val displayName: String
        get() = "$firstName $lastName"

    val initials: String
        get() = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}".uppercase()
}

/**
 * Social media links for a user profile.
 */
data class SocialLinks(
    val instagram: String? = null,
    val tiktok: String? = null,
    val twitter: String? = null,
    val facebook: String? = null,
    val pinterest: String? = null,
    val spotify: String? = null,
) {
    val hasAnyLink: Boolean
        get() = listOf(instagram, tiktok, twitter, facebook, pinterest, spotify).any { it != null }
}

/**
 * Simplified user representation for lists and references.
 */
data class UserSummary(
    val id: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String?,
    val isVerified: Boolean = false,
)

/**
 * Extension to convert full User to UserSummary.
 */
fun User.toSummary(): UserSummary = UserSummary(
    id = id,
    username = username,
    displayName = displayName,
    avatarUrl = avatarUrl,
    isVerified = isVerified,
)
