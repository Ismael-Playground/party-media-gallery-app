package com.partygallery.domain.repository

import com.partygallery.domain.model.UserSummary
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user follow operations.
 *
 * S5-003: UserFollowRepository interface
 */
interface UserFollowRepository {

    /**
     * Follow a user.
     */
    suspend fun followUser(userId: String): Result<Unit>

    /**
     * Unfollow a user.
     */
    suspend fun unfollowUser(userId: String): Result<Unit>

    /**
     * Check if current user is following a user.
     */
    suspend fun isFollowing(userId: String): Boolean

    /**
     * Get followers of a user.
     */
    fun getFollowers(
        userId: String,
        page: Int = 0,
        pageSize: Int = 20,
    ): Flow<List<UserSummary>>

    /**
     * Get users that a user is following.
     */
    fun getFollowing(
        userId: String,
        page: Int = 0,
        pageSize: Int = 20,
    ): Flow<List<UserSummary>>

    /**
     * Get followers count for a user.
     */
    suspend fun getFollowersCount(userId: String): Result<Int>

    /**
     * Get following count for a user.
     */
    suspend fun getFollowingCount(userId: String): Result<Int>

    /**
     * Get mutual followers (followers who also follow you).
     */
    fun getMutualFollowers(userId: String): Flow<List<UserSummary>>

    /**
     * Get suggested users to follow.
     */
    fun getSuggestedUsers(limit: Int = 10): Flow<List<UserSummary>>

    /**
     * Search followers by query.
     */
    fun searchFollowers(userId: String, query: String): Flow<List<UserSummary>>

    /**
     * Search following by query.
     */
    fun searchFollowing(userId: String, query: String): Flow<List<UserSummary>>

    /**
     * Block a user.
     */
    suspend fun blockUser(userId: String): Result<Unit>

    /**
     * Unblock a user.
     */
    suspend fun unblockUser(userId: String): Result<Unit>

    /**
     * Check if a user is blocked.
     */
    suspend fun isBlocked(userId: String): Boolean

    /**
     * Get list of blocked users.
     */
    fun getBlockedUsers(): Flow<List<UserSummary>>

    /**
     * Remove a follower.
     */
    suspend fun removeFollower(userId: String): Result<Unit>
}

/**
 * Follow status between current user and another user.
 */
data class FollowStatus(
    val isFollowing: Boolean,
    val isFollowedBy: Boolean,
    val isBlocked: Boolean,
    val isBlockedBy: Boolean,
) {
    val isMutual: Boolean
        get() = isFollowing && isFollowedBy

    companion object {
        val NONE = FollowStatus(
            isFollowing = false,
            isFollowedBy = false,
            isBlocked = false,
            isBlockedBy = false,
        )
    }
}
