package com.partygallery.data.datasource

import com.partygallery.data.dto.UserSummaryDto
import kotlinx.coroutines.flow.Flow

/**
 * Data source interface for user follow operations.
 * Platform-specific implementations will provide the actual Firebase Firestore integration.
 *
 * S2.5-005: UserFollowRepositoryImpl
 */
interface UserFollowDataSource {

    // ============================================
    // Follow/Unfollow Actions
    // ============================================

    suspend fun followUser(followerId: String, followedId: String)

    suspend fun unfollowUser(followerId: String, followedId: String)

    // ============================================
    // Query Operations
    // ============================================

    suspend fun isFollowing(followerId: String, followedId: String): Boolean

    suspend fun getFollowers(userId: String, limit: Int): List<UserSummaryDto>

    suspend fun getFollowing(userId: String, limit: Int): List<UserSummaryDto>

    suspend fun getFollowersCount(userId: String): Int

    suspend fun getFollowingCount(userId: String): Int

    // ============================================
    // Mutual Follows
    // ============================================

    suspend fun getMutualFollowers(userId1: String, userId2: String): List<UserSummaryDto>

    suspend fun getMutualFollowersCount(userId1: String, userId2: String): Int

    // ============================================
    // Suggestions
    // ============================================

    suspend fun getSuggestedUsersToFollow(userId: String, limit: Int): List<UserSummaryDto>

    // ============================================
    // Observable Flows
    // ============================================

    fun observeFollowCounts(userId: String): Flow<FollowCountsDto>

    fun observeIsFollowing(followerId: String, followedId: String): Flow<Boolean>

    fun observeFollowers(userId: String): Flow<List<UserSummaryDto>>

    fun observeFollowing(userId: String): Flow<List<UserSummaryDto>>
}

/**
 * DTO for follow counts.
 */
data class FollowCountsDto(
    val followers: Int = 0,
    val following: Int = 0,
)
