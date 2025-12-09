package com.partygallery.data.repository

import com.partygallery.data.datasource.UserFollowDataSource
import com.partygallery.data.mapper.toDomain
import com.partygallery.domain.model.UserSummary
import com.partygallery.domain.repository.FollowCounts
import com.partygallery.domain.repository.UserFollowRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of UserFollowRepository using Firebase Firestore.
 *
 * S2.5-005: UserFollowRepositoryImpl
 */
class UserFollowRepositoryImpl(
    private val userFollowDataSource: UserFollowDataSource,
) : UserFollowRepository {

    // ============================================
    // Follow/Unfollow Actions
    // ============================================

    override suspend fun followUser(followerId: String, followedId: String): Result<Unit> {
        return try {
            userFollowDataSource.followUser(followerId, followedId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unfollowUser(followerId: String, followedId: String): Result<Unit> {
        return try {
            userFollowDataSource.unfollowUser(followerId, followedId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Query Operations
    // ============================================

    override suspend fun isFollowing(followerId: String, followedId: String): Result<Boolean> {
        return try {
            val isFollowing = userFollowDataSource.isFollowing(followerId, followedId)
            Result.success(isFollowing)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFollowers(userId: String, limit: Int): Result<List<UserSummary>> {
        return try {
            val dtos = userFollowDataSource.getFollowers(userId, limit)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFollowing(userId: String, limit: Int): Result<List<UserSummary>> {
        return try {
            val dtos = userFollowDataSource.getFollowing(userId, limit)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFollowersCount(userId: String): Result<Int> {
        return try {
            val count = userFollowDataSource.getFollowersCount(userId)
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFollowingCount(userId: String): Result<Int> {
        return try {
            val count = userFollowDataSource.getFollowingCount(userId)
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Mutual Follows
    // ============================================

    override suspend fun getMutualFollowers(userId1: String, userId2: String): Result<List<UserSummary>> {
        return try {
            val dtos = userFollowDataSource.getMutualFollowers(userId1, userId2)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMutualFollowersCount(userId1: String, userId2: String): Result<Int> {
        return try {
            val count = userFollowDataSource.getMutualFollowersCount(userId1, userId2)
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Suggestions
    // ============================================

    override suspend fun getSuggestedUsersToFollow(userId: String, limit: Int): Result<List<UserSummary>> {
        return try {
            val dtos = userFollowDataSource.getSuggestedUsersToFollow(userId, limit)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Observable Flows
    // ============================================

    override fun observeFollowCounts(userId: String): Flow<FollowCounts> {
        return userFollowDataSource.observeFollowCounts(userId).map { dto ->
            FollowCounts(dto.followers, dto.following)
        }
    }

    override fun observeIsFollowing(followerId: String, followedId: String): Flow<Boolean> {
        return userFollowDataSource.observeIsFollowing(followerId, followedId)
    }

    override fun observeFollowers(userId: String): Flow<List<UserSummary>> {
        return userFollowDataSource.observeFollowers(userId).map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override fun observeFollowing(userId: String): Flow<List<UserSummary>> {
        return userFollowDataSource.observeFollowing(userId).map { dtos ->
            dtos.map { it.toDomain() }
        }
    }
}
