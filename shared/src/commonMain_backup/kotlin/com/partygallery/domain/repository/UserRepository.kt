package com.partygallery.domain.repository

import com.partygallery.domain.model.user.User
import com.partygallery.domain.model.user.SocialLink
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun createUser(user: User): Result<User>
    suspend fun getUserById(userId: String): Result<User?>
    suspend fun getUserByUsername(username: String): Result<User?>
    suspend fun getUserByEmail(email: String): Result<User?>
    suspend fun updateUser(user: User): Result<User>
    suspend fun deleteUser(userId: String): Result<Unit>
    
    suspend fun searchUsers(query: String, limit: Int = 20): Result<List<User>>
    suspend fun getUsersByIds(userIds: List<String>): Result<List<User>>
    
    suspend fun updateUserAvatar(userId: String, avatarUrl: String): Result<Unit>
    suspend fun updateUserSocialLinks(userId: String, socialLinks: List<SocialLink>): Result<Unit>
    suspend fun updateUserTags(userId: String, tags: List<String>): Result<Unit>
    
    fun observeUser(userId: String): Flow<User?>
    fun observeUserFollowCounts(userId: String): Flow<Pair<Int, Int>> // followers, following
    
    suspend fun getCurrentUser(): Result<User?>
    fun observeCurrentUser(): Flow<User?>
}