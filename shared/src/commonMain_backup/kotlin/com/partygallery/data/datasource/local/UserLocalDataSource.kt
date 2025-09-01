package com.partygallery.data.datasource.local

import com.partygallery.domain.model.user.User
import com.partygallery.domain.model.user.SocialLink
import kotlinx.coroutines.flow.Flow

interface UserLocalDataSource {
    suspend fun saveUser(user: User): Result<Unit>
    suspend fun getUserById(userId: String): User?
    suspend fun deleteUser(userId: String): Result<Unit>
    
    suspend fun saveUsers(users: List<User>): Result<Unit>
    suspend fun getUsersByIds(userIds: List<String>): List<User>
    
    suspend fun updateUserAvatar(userId: String, avatarUrl: String): Result<Unit>
    suspend fun updateUserSocialLinks(userId: String, socialLinks: List<SocialLink>): Result<Unit>
    suspend fun updateUserTags(userId: String, tags: List<String>): Result<Unit>
    
    suspend fun getCurrentUser(): User?
    suspend fun saveCurrentUser(user: User): Result<Unit>
    suspend fun clearCurrentUser(): Result<Unit>
    
    fun observeCurrentUser(): Flow<User?>
    fun observeUser(userId: String): Flow<User?>
    
    suspend fun searchLocalUsers(query: String, limit: Int): List<User>
    suspend fun clearAllUsers(): Result<Unit>
}