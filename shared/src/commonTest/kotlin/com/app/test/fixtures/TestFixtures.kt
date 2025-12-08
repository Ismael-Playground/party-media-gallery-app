package com.app.test.fixtures

import com.partygallery.domain.model.PartyMood
import com.partygallery.domain.model.User
import com.partygallery.domain.repository.AuthResult
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
        createdAt = createdAt,
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
