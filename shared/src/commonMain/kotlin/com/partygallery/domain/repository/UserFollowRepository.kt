package com.partygallery.domain.repository

import com.partygallery.domain.model.UserSummary
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user follow/unfollow operations.
 *
 * S2.5-005: UserFollowRepositoryImpl
 */
interface UserFollowRepository {

    // ============================================
    // Follow/Unfollow Actions
    // ============================================

    suspend fun followUser(followerId: String, followedId: String): Result<Unit>
    suspend fun unfollowUser(followerId: String, followedId: String): Result<Unit>

    // ============================================
    // Query Operations
    // ============================================

    suspend fun isFollowing(followerId: String, followedId: String): Result<Boolean>
    suspend fun getFollowers(userId: String, limit: Int = 50): Result<List<UserSummary>>
    suspend fun getFollowing(userId: String, limit: Int = 50): Result<List<UserSummary>>
    suspend fun getFollowersCount(userId: String): Result<Int>
    suspend fun getFollowingCount(userId: String): Result<Int>

    // ============================================
    // Mutual Follows
    // ============================================

    suspend fun getMutualFollowers(userId1: String, userId2: String): Result<List<UserSummary>>
    suspend fun getMutualFollowersCount(userId1: String, userId2: String): Result<Int>

    // ============================================
    // Suggestions
    // ============================================

    suspend fun getSuggestedUsersToFollow(userId: String, limit: Int = 10): Result<List<UserSummary>>

    // ============================================
    // Observable Flows
    // ============================================

    fun observeFollowCounts(userId: String): Flow<FollowCounts>
    fun observeIsFollowing(followerId: String, followedId: String): Flow<Boolean>
    fun observeFollowers(userId: String): Flow<List<UserSummary>>
    fun observeFollowing(userId: String): Flow<List<UserSummary>>
}
