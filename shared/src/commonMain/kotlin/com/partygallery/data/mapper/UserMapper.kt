package com.partygallery.data.mapper

import com.partygallery.data.dto.SocialLinksDto
import com.partygallery.data.dto.UserDto
import com.partygallery.data.dto.UserSummaryDto
import com.partygallery.domain.model.SocialLinks
import com.partygallery.domain.model.User
import com.partygallery.domain.model.UserSummary
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * Mapper extensions for User DTO <-> Domain conversions.
 *
 * S2.5-007: Data mappers (DTO to Domain)
 */

// DTO -> Domain

fun UserDto.toDomain(): User = User(
    id = id,
    firebaseId = firebaseId,
    email = email,
    username = username,
    firstName = firstName,
    lastName = lastName,
    bio = bio,
    birthDate = birthDate?.let { parseLocalDate(it) },
    avatarUrl = avatarUrl,
    coverPhotoUrl = coverPhotoUrl,
    phoneNumber = phoneNumber,
    socialLinks = socialLinks.toDomain(),
    tags = tags,
    followersCount = followersCount,
    followingCount = followingCount,
    isVerified = isVerified,
    isProfileComplete = isProfileComplete,
    notificationsEnabled = notificationsEnabled,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = updatedAt?.let { Instant.fromEpochMilliseconds(it) },
)

fun SocialLinksDto.toDomain(): SocialLinks = SocialLinks(
    instagram = instagram,
    tiktok = tiktok,
    twitter = twitter,
    facebook = facebook,
    pinterest = pinterest,
    spotify = spotify,
)

fun UserSummaryDto.toDomain(): UserSummary = UserSummary(
    id = id,
    username = username,
    displayName = "$firstName $lastName".trim().ifEmpty { username },
    avatarUrl = avatarUrl,
    isVerified = isVerified,
)

// Domain -> DTO

fun User.toDto(): UserDto = UserDto(
    id = id,
    firebaseId = firebaseId,
    email = email,
    username = username,
    firstName = firstName,
    lastName = lastName,
    bio = bio,
    birthDate = birthDate?.toString(), // ISO-8601 format
    avatarUrl = avatarUrl,
    coverPhotoUrl = coverPhotoUrl,
    phoneNumber = phoneNumber,
    socialLinks = socialLinks.toDto(),
    tags = tags,
    followersCount = followersCount,
    followingCount = followingCount,
    isVerified = isVerified,
    isProfileComplete = isProfileComplete,
    notificationsEnabled = notificationsEnabled,
    createdAt = createdAt.toEpochMilliseconds(),
    updatedAt = updatedAt?.toEpochMilliseconds(),
)

fun SocialLinks.toDto(): SocialLinksDto = SocialLinksDto(
    instagram = instagram,
    tiktok = tiktok,
    twitter = twitter,
    facebook = facebook,
    pinterest = pinterest,
    spotify = spotify,
)

fun UserSummary.toDto(): UserSummaryDto {
    val parts = displayName.split(" ", limit = 2)
    return UserSummaryDto(
        id = id,
        username = username,
        firstName = parts.getOrElse(0) { "" },
        lastName = parts.getOrElse(1) { "" },
        avatarUrl = avatarUrl,
        isVerified = isVerified,
    )
}

// Helper function to parse LocalDate from ISO-8601 string
private fun parseLocalDate(dateString: String): LocalDate? {
    return try {
        LocalDate.parse(dateString)
    } catch (e: Exception) {
        null
    }
}
