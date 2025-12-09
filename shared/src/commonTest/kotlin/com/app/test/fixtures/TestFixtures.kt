package com.app.test.fixtures

import com.partygallery.domain.model.MediaContent
import com.partygallery.domain.model.MediaMetadata
import com.partygallery.domain.model.MediaSocialMetrics
import com.partygallery.domain.model.MediaType
import com.partygallery.domain.model.PartyEvent
import com.partygallery.domain.model.PartyMood
import com.partygallery.domain.model.PartyPrivacy
import com.partygallery.domain.model.PartyStatus
import com.partygallery.domain.model.User
import com.partygallery.domain.model.UserSummary
import com.partygallery.domain.model.Venue
import com.partygallery.domain.repository.AuthResult
import kotlin.time.Duration.Companion.hours
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Test fixtures and factories for Party Gallery tests.
 *
 * S2.6-EX-004: Test factories for User, Party, Media
 *
 * Usage:
 * ```kotlin
 * val user = TestFixtures.user()
 * val customUser = TestFixtures.user(username = "custom_user")
 * val party = TestFixtures.party()
 * val media = TestFixtures.media()
 * ```
 */
object TestFixtures {

    // ============================================
    // User Factory
    // ============================================

    fun user(
        id: String = "user-123",
        firebaseId: String = "firebase-user-123",
        username: String = "testuser",
        email: String = "test@party.gallery",
        firstName: String = "Test",
        lastName: String = "User",
        avatarUrl: String? = "https://example.com/avatar.jpg",
        followersCount: Int = 100,
        followingCount: Int = 50,
        isVerified: Boolean = false,
        isProfileComplete: Boolean = true,
        createdAt: Instant = Clock.System.now(),
    ): User = User(
        id = id,
        firebaseId = firebaseId,
        username = username,
        email = email,
        firstName = firstName,
        lastName = lastName,
        avatarUrl = avatarUrl,
        followersCount = followersCount,
        followingCount = followingCount,
        isVerified = isVerified,
        isProfileComplete = isProfileComplete,
        createdAt = createdAt,
    )

    fun userSummary(
        id: String = "user-123",
        username: String = "testuser",
        displayName: String = "Test User",
        avatarUrl: String? = "https://example.com/avatar.jpg",
        isVerified: Boolean = false,
    ): UserSummary = UserSummary(
        id = id,
        username = username,
        displayName = displayName,
        avatarUrl = avatarUrl,
        isVerified = isVerified,
    )

    // ============================================
    // Party Factory
    // ============================================

    fun party(
        id: String = "party-123",
        hostId: String = "user-123",
        host: UserSummary? = userSummary(),
        title: String = "Test Party",
        description: String? = "A test party event",
        venue: Venue = venue(),
        coverImageUrl: String? = "https://example.com/party-cover.jpg",
        startsAt: Instant = Clock.System.now() + 2.hours,
        endsAt: Instant? = Clock.System.now() + 6.hours,
        status: PartyStatus = PartyStatus.PLANNED,
        privacy: PartyPrivacy = PartyPrivacy.PUBLIC,
        tags: List<String> = listOf("party", "music"),
        musicGenres: List<String> = listOf("Electronic", "House"),
        maxAttendees: Int? = 100,
        attendeesCount: Int = 25,
        mediaCount: Int = 10,
        createdAt: Instant = Clock.System.now(),
    ): PartyEvent = PartyEvent(
        id = id,
        hostId = hostId,
        host = host,
        title = title,
        description = description,
        venue = venue,
        coverImageUrl = coverImageUrl,
        startsAt = startsAt,
        endsAt = endsAt,
        status = status,
        privacy = privacy,
        tags = tags,
        musicGenres = musicGenres,
        maxAttendees = maxAttendees,
        attendeesCount = attendeesCount,
        mediaCount = mediaCount,
        createdAt = createdAt,
    )

    fun venue(
        name: String = "Test Venue",
        address: String? = "123 Party Street",
        city: String? = "Party City",
        country: String? = "Partyland",
        latitude: Double? = 40.7128,
        longitude: Double? = -74.0060,
    ): Venue = Venue(
        name = name,
        address = address,
        city = city,
        country = country,
        latitude = latitude,
        longitude = longitude,
    )

    // ============================================
    // Media Factory
    // ============================================

    fun media(
        id: String = "media-123",
        partyEventId: String = "party-123",
        uploaderId: String = "user-123",
        uploader: UserSummary? = userSummary(),
        type: MediaType = MediaType.PHOTO,
        url: String = "https://example.com/media/photo.jpg",
        thumbnailUrl: String? = "https://example.com/media/thumb.jpg",
        caption: String? = "Check out this moment!",
        mood: PartyMood? = PartyMood.HYPE,
        tags: List<String> = emptyList(),
        metadata: MediaMetadata = MediaMetadata(),
        socialMetrics: MediaSocialMetrics = MediaSocialMetrics(likesCount = 42),
        isHighlight: Boolean = false,
        createdAt: Instant = Clock.System.now(),
    ): MediaContent = MediaContent(
        id = id,
        partyEventId = partyEventId,
        uploaderId = uploaderId,
        uploader = uploader,
        type = type,
        url = url,
        thumbnailUrl = thumbnailUrl,
        caption = caption,
        mood = mood,
        tags = tags,
        metadata = metadata,
        socialMetrics = socialMetrics,
        isHighlight = isHighlight,
        createdAt = createdAt,
    )

    fun videoMedia(
        id: String = "video-123",
        partyEventId: String = "party-123",
        uploaderId: String = "user-123",
        durationSeconds: Int = 30,
        caption: String? = "Party video!",
        mood: PartyMood? = PartyMood.WILD,
    ): MediaContent = MediaContent(
        id = id,
        partyEventId = partyEventId,
        uploaderId = uploaderId,
        type = MediaType.VIDEO,
        url = "https://example.com/media/video.mp4",
        thumbnailUrl = "https://example.com/media/video-thumb.jpg",
        caption = caption,
        mood = mood,
        metadata = MediaMetadata(
            width = 1920,
            height = 1080,
            durationSeconds = durationSeconds,
            mimeType = "video/mp4",
        ),
        createdAt = Clock.System.now(),
    )

    // ============================================
    // Auth Result Factory
    // ============================================

    fun authResult(
        userId: String = "user-123",
        email: String = "test@party.gallery",
        isNewUser: Boolean = false,
        isEmailVerified: Boolean = true,
        idToken: String = "test-token-abc123",
    ): AuthResult = AuthResult(
        userId = userId,
        email = email,
        isNewUser = isNewUser,
        isEmailVerified = isEmailVerified,
        idToken = idToken,
    )

    // ============================================
    // Common Test Values
    // ============================================

    object Emails {
        const val VALID = "test@party.gallery"
        const val INVALID = "not-an-email"
        const val EMPTY = ""
    }

    object Passwords {
        const val VALID = "password123"
        const val SHORT = "abc"
        const val EMPTY = ""
    }

    object Errors {
        val NETWORK_ERROR = Exception("Network error")
        val INVALID_CREDENTIALS = Exception("Invalid credentials")
        val USER_NOT_FOUND = Exception("User not found")
        val ACCOUNT_DISABLED = Exception("Account disabled")
    }

    object Moods {
        val ALL = listOf(
            PartyMood.HYPE,
            PartyMood.CHILL,
            PartyMood.WILD,
            PartyMood.ROMANTIC,
            PartyMood.CRAZY,
            PartyMood.ELEGANT,
        )
    }
}
