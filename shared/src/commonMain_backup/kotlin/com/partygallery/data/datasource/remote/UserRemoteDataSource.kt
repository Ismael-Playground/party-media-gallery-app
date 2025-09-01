package com.partygallery.data.datasource.remote

import com.partygallery.domain.model.user.SocialLink
import kotlinx.coroutines.flow.Flow

interface UserRemoteDataSource {
    suspend fun createUser(user: RemoteUser): Result<RemoteUser>
    suspend fun getUserById(userId: String): Result<RemoteUser?>
    suspend fun getUserByUsername(username: String): Result<RemoteUser?>
    suspend fun getUserByEmail(email: String): Result<RemoteUser?>
    suspend fun updateUser(user: RemoteUser): Result<RemoteUser>
    suspend fun deleteUser(userId: String): Result<Unit>
    
    suspend fun searchUsers(query: String, limit: Int): Result<List<RemoteUser>>
    suspend fun getUsersByIds(userIds: List<String>): Result<List<RemoteUser>>
    
    suspend fun updateUserAvatar(userId: String, avatarUrl: String): Result<Unit>
    suspend fun updateUserSocialLinks(userId: String, socialLinks: List<SocialLink>): Result<Unit>
    suspend fun updateUserTags(userId: String, tags: List<String>): Result<Unit>
    
    fun observeUser(userId: String): Flow<RemoteUser?>
    fun observeUserFollowCounts(userId: String): Flow<Pair<Int, Int>>
    
    suspend fun getCurrentUser(): Result<RemoteUser?>
}

data class RemoteUser(
    val id: String,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val birthDate: String, // ISO string
    val avatarUrl: String?,
    val bio: String?,
    val isPrivate: Boolean,
    val followersCount: Int,
    val followingCount: Int,
    val postsCount: Int,
    val socialLinks: List<SocialLink>,
    val tags: List<String>,
    val createdAt: String, // ISO string
    val updatedAt: String  // ISO string
)