package com.partygallery.data.datasource

import com.partygallery.data.dto.UserSummaryDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Mock implementation of UserFollowDataSource for development and testing.
 * Will be replaced with FirebaseUserFollowDataSource in production.
 *
 * S2.5-005: UserFollowRepositoryImpl
 */
class MockUserFollowDataSource : UserFollowDataSource {

    // In-memory storage: followerId -> Set<followedId>
    private val followRelations = MutableStateFlow<Map<String, Set<String>>>(createInitialFollows())

    // Simulate network delay
    private suspend fun simulateNetworkDelay() {
        delay(200)
    }

    // ============================================
    // Follow/Unfollow Actions
    // ============================================

    override suspend fun followUser(followerId: String, followedId: String) {
        simulateNetworkDelay()
        val currentFollowing = followRelations.value[followerId]?.toMutableSet() ?: mutableSetOf()
        currentFollowing.add(followedId)
        followRelations.value = followRelations.value + (followerId to currentFollowing)
    }

    override suspend fun unfollowUser(followerId: String, followedId: String) {
        simulateNetworkDelay()
        val currentFollowing = followRelations.value[followerId]?.toMutableSet() ?: return
        currentFollowing.remove(followedId)
        followRelations.value = followRelations.value + (followerId to currentFollowing)
    }

    // ============================================
    // Query Operations
    // ============================================

    override suspend fun isFollowing(followerId: String, followedId: String): Boolean {
        simulateNetworkDelay()
        return followRelations.value[followerId]?.contains(followedId) == true
    }

    override suspend fun getFollowers(userId: String, limit: Int): List<UserSummaryDto> {
        simulateNetworkDelay()
        return followRelations.value
            .filter { (_, followedIds) -> followedIds.contains(userId) }
            .keys
            .take(limit)
            .mapNotNull { getMockUserSummary(it) }
    }

    override suspend fun getFollowing(userId: String, limit: Int): List<UserSummaryDto> {
        simulateNetworkDelay()
        return followRelations.value[userId]
            ?.take(limit)
            ?.mapNotNull { getMockUserSummary(it) }
            ?: emptyList()
    }

    override suspend fun getFollowersCount(userId: String): Int {
        simulateNetworkDelay()
        return followRelations.value.count { (_, followedIds) -> followedIds.contains(userId) }
    }

    override suspend fun getFollowingCount(userId: String): Int {
        simulateNetworkDelay()
        return followRelations.value[userId]?.size ?: 0
    }

    // ============================================
    // Mutual Follows
    // ============================================

    override suspend fun getMutualFollowers(userId1: String, userId2: String): List<UserSummaryDto> {
        simulateNetworkDelay()
        val followers1 = followRelations.value
            .filter { (_, followedIds) -> followedIds.contains(userId1) }
            .keys
        val followers2 = followRelations.value
            .filter { (_, followedIds) -> followedIds.contains(userId2) }
            .keys
        return followers1.intersect(followers2).mapNotNull { getMockUserSummary(it) }
    }

    override suspend fun getMutualFollowersCount(userId1: String, userId2: String): Int {
        simulateNetworkDelay()
        val followers1 = followRelations.value
            .filter { (_, followedIds) -> followedIds.contains(userId1) }
            .keys
        val followers2 = followRelations.value
            .filter { (_, followedIds) -> followedIds.contains(userId2) }
            .keys
        return followers1.intersect(followers2).size
    }

    // ============================================
    // Suggestions
    // ============================================

    override suspend fun getSuggestedUsersToFollow(userId: String, limit: Int): List<UserSummaryDto> {
        simulateNetworkDelay()
        val currentFollowing = followRelations.value[userId] ?: emptySet()
        val allUsers = setOf("user1", "user2", "user3", "user4", "user5")
        return allUsers
            .filter { it != userId && it !in currentFollowing }
            .take(limit)
            .mapNotNull { getMockUserSummary(it) }
    }

    // ============================================
    // Observable Flows
    // ============================================

    override fun observeFollowCounts(userId: String): Flow<FollowCountsDto> {
        return followRelations.map { relations ->
            val followers = relations.count { (_, followedIds) -> followedIds.contains(userId) }
            val following = relations[userId]?.size ?: 0
            FollowCountsDto(followers, following)
        }
    }

    override fun observeIsFollowing(followerId: String, followedId: String): Flow<Boolean> {
        return followRelations.map { relations ->
            relations[followerId]?.contains(followedId) == true
        }
    }

    override fun observeFollowers(userId: String): Flow<List<UserSummaryDto>> {
        return followRelations.map { relations ->
            relations
                .filter { (_, followedIds) -> followedIds.contains(userId) }
                .keys
                .mapNotNull { getMockUserSummary(it) }
        }
    }

    override fun observeFollowing(userId: String): Flow<List<UserSummaryDto>> {
        return followRelations.map { relations ->
            relations[userId]
                ?.mapNotNull { getMockUserSummary(it) }
                ?: emptyList()
        }
    }

    // ============================================
    // Mock Data Helpers
    // ============================================

    private fun getMockUserSummary(userId: String): UserSummaryDto? {
        return when (userId) {
            "user1" -> UserSummaryDto(
                id = "user1",
                username = "partyking",
                firstName = "Test",
                lastName = "User",
                avatarUrl = "https://i.pravatar.cc/150?u=user1",
                isVerified = true,
            )
            "user2" -> UserSummaryDto(
                id = "user2",
                username = "djmaster",
                firstName = "DJ",
                lastName = "Master",
                avatarUrl = "https://i.pravatar.cc/150?u=user2",
            )
            "user3" -> UserSummaryDto(
                id = "user3",
                username = "clubqueen",
                firstName = "Club",
                lastName = "Queen",
                avatarUrl = "https://i.pravatar.cc/150?u=user3",
            )
            "user4" -> UserSummaryDto(
                id = "user4",
                username = "nightowl",
                firstName = "Night",
                lastName = "Owl",
                avatarUrl = "https://i.pravatar.cc/150?u=user4",
            )
            "user5" -> UserSummaryDto(
                id = "user5",
                username = "partyanimal",
                firstName = "Party",
                lastName = "Animal",
                avatarUrl = "https://i.pravatar.cc/150?u=user5",
                isVerified = true,
            )
            else -> null
        }
    }

    // ============================================
    // Initial Mock Data
    // ============================================

    private fun createInitialFollows(): Map<String, Set<String>> {
        // Map of userId to set of userIds they follow
        return mapOf(
            "user1" to setOf("user2", "user3", "user4"),
            "user2" to setOf("user1", "user3", "user5"),
            "user3" to setOf("user1", "user2"),
            "user4" to setOf("user1", "user5"),
            "user5" to setOf("user2", "user3", "user4"),
        )
    }
}
