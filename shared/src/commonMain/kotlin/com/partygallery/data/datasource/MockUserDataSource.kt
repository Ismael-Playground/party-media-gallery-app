package com.partygallery.data.datasource

import com.partygallery.data.dto.SocialLinksDto
import com.partygallery.data.dto.UserDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

/**
 * Mock implementation of UserDataSource for development and testing.
 * Will be replaced with FirebaseUserDataSource in production.
 *
 * S2.5-001: UserRepositoryImpl con Firebase Firestore
 */
class MockUserDataSource : UserDataSource {

    // In-memory storage
    private val users = MutableStateFlow<Map<String, UserDto>>(createInitialUsers())
    private val followCounts = MutableStateFlow<Map<String, Pair<Int, Int>>>(
        mapOf(
            "user1" to Pair(1234, 567),
            "user2" to Pair(89, 123),
        )
    )

    // Simulate network delay
    private suspend fun simulateNetworkDelay() {
        delay(300)
    }

    // ============================================
    // CRUD Operations
    // ============================================

    override suspend fun createUser(user: UserDto): UserDto {
        simulateNetworkDelay()
        val newUser = user.copy(
            createdAt = Clock.System.now().toEpochMilliseconds(),
        )
        users.value = users.value + (user.id to newUser)
        return newUser
    }

    override suspend fun getUserById(userId: String): UserDto? {
        simulateNetworkDelay()
        return users.value[userId]
    }

    override suspend fun getUserByUsername(username: String): UserDto? {
        simulateNetworkDelay()
        return users.value.values.find { it.username.equals(username, ignoreCase = true) }
    }

    override suspend fun getUserByEmail(email: String): UserDto? {
        simulateNetworkDelay()
        return users.value.values.find { it.email.equals(email, ignoreCase = true) }
    }

    override suspend fun updateUser(user: UserDto): UserDto {
        simulateNetworkDelay()
        val updatedUser = user.copy(
            updatedAt = Clock.System.now().toEpochMilliseconds(),
        )
        users.value = users.value + (user.id to updatedUser)
        return updatedUser
    }

    override suspend fun deleteUser(userId: String) {
        simulateNetworkDelay()
        users.value = users.value - userId
    }

    // ============================================
    // Search & Query
    // ============================================

    override suspend fun searchUsers(query: String, limit: Int): List<UserDto> {
        simulateNetworkDelay()
        val lowercaseQuery = query.lowercase()
        return users.value.values
            .filter { user ->
                user.username.lowercase().contains(lowercaseQuery) ||
                    user.firstName.lowercase().contains(lowercaseQuery) ||
                    user.lastName.lowercase().contains(lowercaseQuery) ||
                    user.email.lowercase().contains(lowercaseQuery)
            }
            .take(limit)
    }

    override suspend fun getUsersByIds(userIds: List<String>): List<UserDto> {
        simulateNetworkDelay()
        return userIds.mapNotNull { users.value[it] }
    }

    override suspend fun getSuggestedUsers(userId: String, limit: Int): List<UserDto> {
        simulateNetworkDelay()
        // Return all users except the current user, limited
        return users.value.values
            .filter { it.id != userId }
            .take(limit)
    }

    // ============================================
    // Field Updates
    // ============================================

    override suspend fun updateField(userId: String, field: String, value: Any?) {
        simulateNetworkDelay()
        val user = users.value[userId] ?: return

        val updatedUser = when (field) {
            "avatar_url" -> user.copy(avatarUrl = value as? String)
            "cover_photo_url" -> user.copy(coverPhotoUrl = value as? String)
            "bio" -> user.copy(bio = value as? String)
            "tags" -> user.copy(tags = (value as? List<*>)?.filterIsInstance<String>() ?: emptyList())
            "social_links" -> user.copy(socialLinks = value as? SocialLinksDto ?: SocialLinksDto())
            "is_profile_complete" -> user.copy(isProfileComplete = value as? Boolean ?: false)
            "notifications_enabled" -> user.copy(notificationsEnabled = value as? Boolean ?: true)
            else -> user
        }.copy(updatedAt = Clock.System.now().toEpochMilliseconds())

        users.value = users.value + (userId to updatedUser)
    }

    // ============================================
    // Username Validation
    // ============================================

    override suspend fun isUsernameAvailable(username: String): Boolean {
        simulateNetworkDelay()
        return users.value.values.none { it.username.equals(username, ignoreCase = true) }
    }

    // ============================================
    // Real-time Observers
    // ============================================

    override fun observeUser(userId: String): Flow<UserDto?> {
        return users.map { it[userId] }
    }

    override fun observeFollowCounts(userId: String): Flow<Pair<Int, Int>> {
        return followCounts.map { it[userId] ?: Pair(0, 0) }
    }

    // ============================================
    // Mock Data
    // ============================================

    private fun createInitialUsers(): Map<String, UserDto> {
        val now = Clock.System.now().toEpochMilliseconds()

        val testUser = UserDto(
            id = "user1",
            firebaseId = "firebase_user1",
            email = "test@party.gallery",
            username = "partyking",
            firstName = "Test",
            lastName = "User",
            bio = "Party enthusiast and nightlife explorer",
            birthDate = "1995-05-15",
            avatarUrl = "https://i.pravatar.cc/150?u=user1",
            coverPhotoUrl = "https://picsum.photos/seed/cover1/800/300",
            phoneNumber = "+1234567890",
            socialLinks = SocialLinksDto(
                instagram = "@partyking",
                tiktok = "@partyking",
                spotify = "partyking",
            ),
            tags = listOf("Electronic", "House", "Techno", "Nightlife"),
            followersCount = 1234,
            followingCount = 567,
            isVerified = true,
            isProfileComplete = true,
            notificationsEnabled = true,
            createdAt = now - (30L * 24 * 60 * 60 * 1000), // 30 days ago
            updatedAt = now,
        )

        val user2 = UserDto(
            id = "user2",
            firebaseId = "firebase_user2",
            email = "dj@party.gallery",
            username = "djmaster",
            firstName = "DJ",
            lastName = "Master",
            bio = "Professional DJ and event organizer",
            birthDate = "1992-08-22",
            avatarUrl = "https://i.pravatar.cc/150?u=user2",
            coverPhotoUrl = "https://picsum.photos/seed/cover2/800/300",
            socialLinks = SocialLinksDto(
                instagram = "@djmaster",
                spotify = "djmaster",
            ),
            tags = listOf("DJ", "EDM", "Festival", "Production"),
            followersCount = 89,
            followingCount = 123,
            isVerified = false,
            isProfileComplete = true,
            notificationsEnabled = true,
            createdAt = now - (60L * 24 * 60 * 60 * 1000), // 60 days ago
            updatedAt = now - (5L * 24 * 60 * 60 * 1000), // 5 days ago
        )

        val user3 = UserDto(
            id = "user3",
            firebaseId = "firebase_user3",
            email = "club@party.gallery",
            username = "clubqueen",
            firstName = "Club",
            lastName = "Queen",
            bio = "Living for the weekends",
            avatarUrl = "https://i.pravatar.cc/150?u=user3",
            tags = listOf("R&B", "Hip Hop", "VIP"),
            followersCount = 456,
            followingCount = 234,
            isProfileComplete = true,
            createdAt = now - (15L * 24 * 60 * 60 * 1000),
        )

        return mapOf(
            testUser.id to testUser,
            user2.id to user2,
            user3.id to user3,
        )
    }
}
