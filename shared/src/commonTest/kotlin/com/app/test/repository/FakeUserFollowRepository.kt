package com.app.test.repository

import com.partygallery.domain.model.UserSummary
import com.partygallery.domain.repository.FollowCounts
import com.partygallery.domain.repository.UserFollowRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Fake UserFollowRepository for testing.
 *
 * This fake allows you to control follow/unfollow behavior in tests
 * by setting up success/failure scenarios before each test.
 */
class FakeUserFollowRepository : UserFollowRepository {

    // ============================================
    // Test State
    // ============================================

    // Map of followerId -> Set of followedIds
    private val follows = mutableMapOf<String, MutableSet<String>>()
    private val users = mutableMapOf<String, UserSummary>()
    private val followCountsFlows = mutableMapOf<String, MutableStateFlow<FollowCounts>>()
    private val isFollowingFlows = mutableMapOf<String, MutableStateFlow<Boolean>>()
    private val followersFlows = mutableMapOf<String, MutableStateFlow<List<UserSummary>>>()
    private val followingFlows = mutableMapOf<String, MutableStateFlow<List<UserSummary>>>()

    // Configurable behaviors
    private var shouldFail: Boolean = false
    private var failureError: Exception = Exception("Test error")
    private var suggestedUsers: List<UserSummary> = emptyList()

    // ============================================
    // Test Setup Methods
    // ============================================

    fun setUsers(vararg userList: UserSummary) {
        users.clear()
        userList.forEach { users[it.id] = it }
    }

    fun addUser(user: UserSummary) {
        users[user.id] = user
    }

    fun setFollowing(followerId: String, followedId: String) {
        follows.getOrPut(followerId) { mutableSetOf() }.add(followedId)
        updateFlows(followerId)
        updateFlows(followedId)
    }

    fun removeFollowing(followerId: String, followedId: String) {
        follows[followerId]?.remove(followedId)
        updateFlows(followerId)
        updateFlows(followedId)
    }

    fun setShouldFail(fail: Boolean, error: Exception = Exception("Test error")) {
        shouldFail = fail
        failureError = error
    }

    fun setSuggestedUsers(users: List<UserSummary>) {
        suggestedUsers = users
    }

    fun reset() {
        follows.clear()
        users.clear()
        followCountsFlows.clear()
        isFollowingFlows.clear()
        followersFlows.clear()
        followingFlows.clear()
        shouldFail = false
        suggestedUsers = emptyList()
    }

    private fun updateFlows(userId: String) {
        val followersCount = follows.values.count { it.contains(userId) }
        val followingCount = follows[userId]?.size ?: 0

        followCountsFlows[userId]?.value = FollowCounts(
            followers = followersCount,
            following = followingCount,
        )

        // Update followers flow
        val followers = follows.entries
            .filter { it.value.contains(userId) }
            .mapNotNull { users[it.key] }
        followersFlows[userId]?.value = followers

        // Update following flow
        val following = follows[userId]?.mapNotNull { users[it] } ?: emptyList()
        followingFlows[userId]?.value = following
    }

    // ============================================
    // UserFollowRepository Implementation
    // ============================================

    override suspend fun followUser(followerId: String, followedId: String): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        follows.getOrPut(followerId) { mutableSetOf() }.add(followedId)
        isFollowingFlows["$followerId:$followedId"]?.value = true
        updateFlows(followerId)
        updateFlows(followedId)
        return Result.success(Unit)
    }

    override suspend fun unfollowUser(followerId: String, followedId: String): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        follows[followerId]?.remove(followedId)
        isFollowingFlows["$followerId:$followedId"]?.value = false
        updateFlows(followerId)
        updateFlows(followedId)
        return Result.success(Unit)
    }

    override suspend fun isFollowing(followerId: String, followedId: String): Result<Boolean> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(follows[followerId]?.contains(followedId) ?: false)
    }

    override suspend fun getFollowers(userId: String, limit: Int): Result<List<UserSummary>> {
        if (shouldFail) return Result.failure(failureError)
        val followers = follows.entries
            .filter { it.value.contains(userId) }
            .mapNotNull { users[it.key] }
            .take(limit)
        return Result.success(followers)
    }

    override suspend fun getFollowing(userId: String, limit: Int): Result<List<UserSummary>> {
        if (shouldFail) return Result.failure(failureError)
        val following = follows[userId]?.mapNotNull { users[it] }?.take(limit) ?: emptyList()
        return Result.success(following)
    }

    override suspend fun getFollowersCount(userId: String): Result<Int> {
        if (shouldFail) return Result.failure(failureError)
        val count = follows.values.count { it.contains(userId) }
        return Result.success(count)
    }

    override suspend fun getFollowingCount(userId: String): Result<Int> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(follows[userId]?.size ?: 0)
    }

    override suspend fun getMutualFollowers(
        userId1: String,
        userId2: String,
    ): Result<List<UserSummary>> {
        if (shouldFail) return Result.failure(failureError)

        // Get followers of both users
        val followersOfUser1 = follows.entries
            .filter { it.value.contains(userId1) }
            .map { it.key }
            .toSet()
        val followersOfUser2 = follows.entries
            .filter { it.value.contains(userId2) }
            .map { it.key }
            .toSet()

        // Find mutual followers
        val mutualFollowerIds = followersOfUser1.intersect(followersOfUser2)
        val mutualFollowers = mutualFollowerIds.mapNotNull { users[it] }

        return Result.success(mutualFollowers)
    }

    override suspend fun getMutualFollowersCount(userId1: String, userId2: String): Result<Int> {
        if (shouldFail) return Result.failure(failureError)
        return getMutualFollowers(userId1, userId2).map { it.size }
    }

    override suspend fun getSuggestedUsersToFollow(
        userId: String,
        limit: Int,
    ): Result<List<UserSummary>> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(suggestedUsers.take(limit))
    }

    override fun observeFollowCounts(userId: String): Flow<FollowCounts> {
        return followCountsFlows.getOrPut(userId) {
            val followersCount = follows.values.count { it.contains(userId) }
            val followingCount = follows[userId]?.size ?: 0
            MutableStateFlow(FollowCounts(followers = followersCount, following = followingCount))
        }
    }

    override fun observeIsFollowing(followerId: String, followedId: String): Flow<Boolean> {
        return isFollowingFlows.getOrPut("$followerId:$followedId") {
            MutableStateFlow(follows[followerId]?.contains(followedId) ?: false)
        }
    }

    override fun observeFollowers(userId: String): Flow<List<UserSummary>> {
        return followersFlows.getOrPut(userId) {
            val followers = follows.entries
                .filter { it.value.contains(userId) }
                .mapNotNull { users[it.key] }
            MutableStateFlow(followers)
        }
    }

    override fun observeFollowing(userId: String): Flow<List<UserSummary>> {
        return followingFlows.getOrPut(userId) {
            val following = follows[userId]?.mapNotNull { users[it] } ?: emptyList()
            MutableStateFlow(following)
        }
    }
}
