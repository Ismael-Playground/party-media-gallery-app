package com.partygallery.data.datasource

import com.partygallery.data.dto.UserDto
import kotlinx.coroutines.flow.Flow

/**
 * Data source interface for User operations.
 * Platform-specific implementations will provide the actual Firebase Firestore integration.
 *
 * S2.5-001: UserRepositoryImpl con Firebase Firestore
 */
interface UserDataSource {

    // ============================================
    // CRUD Operations
    // ============================================

    suspend fun createUser(user: UserDto): UserDto

    suspend fun getUserById(userId: String): UserDto?

    suspend fun getUserByUsername(username: String): UserDto?

    suspend fun getUserByEmail(email: String): UserDto?

    suspend fun updateUser(user: UserDto): UserDto

    suspend fun deleteUser(userId: String)

    // ============================================
    // Search & Query
    // ============================================

    suspend fun searchUsers(query: String, limit: Int): List<UserDto>

    suspend fun getUsersByIds(userIds: List<String>): List<UserDto>

    suspend fun getSuggestedUsers(userId: String, limit: Int): List<UserDto>

    // ============================================
    // Field Updates
    // ============================================

    suspend fun updateField(userId: String, field: String, value: Any?)

    // ============================================
    // Username Validation
    // ============================================

    suspend fun isUsernameAvailable(username: String): Boolean

    // ============================================
    // Real-time Observers
    // ============================================

    fun observeUser(userId: String): Flow<UserDto?>

    fun observeFollowCounts(userId: String): Flow<Pair<Int, Int>>
}
