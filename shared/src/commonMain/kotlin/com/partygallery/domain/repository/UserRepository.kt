package com.partygallery.domain.repository

import com.partygallery.domain.model.SocialLinks
import com.partygallery.domain.model.User
import com.partygallery.domain.model.UserSummary
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for User data operations.
 *
 * S1-011: Interfaces base de repositorios
 */
interface UserRepository {

    // ============================================
    // CRUD Operations
    // ============================================

    suspend fun createUser(user: User): Result<User>
    suspend fun getUserById(userId: String): Result<User?>
    suspend fun getUserByUsername(username: String): Result<User?>
    suspend fun getUserByEmail(email: String): Result<User?>
    suspend fun updateUser(user: User): Result<User>
    suspend fun deleteUser(userId: String): Result<Unit>

    // ============================================
    // Search & Query
    // ============================================

    suspend fun searchUsers(query: String, limit: Int = 20): Result<List<UserSummary>>
    suspend fun getUsersByIds(userIds: List<String>): Result<List<User>>
    suspend fun getSuggestedUsers(userId: String, limit: Int = 10): Result<List<UserSummary>>

    // ============================================
    // Profile Updates
    // ============================================

    suspend fun updateAvatar(userId: String, avatarUrl: String): Result<Unit>
    suspend fun updateCoverPhoto(userId: String, coverPhotoUrl: String): Result<Unit>
    suspend fun updateSocialLinks(userId: String, socialLinks: SocialLinks): Result<Unit>
    suspend fun updateTags(userId: String, tags: List<String>): Result<Unit>
    suspend fun completeProfile(userId: String, updates: ProfileCompletionData): Result<User>

    // ============================================
    // Username Validation
    // ============================================

    suspend fun isUsernameAvailable(username: String): Result<Boolean>
    suspend fun validateUsername(username: String): UsernameValidationResult

    // ============================================
    // Current User
    // ============================================

    suspend fun getCurrentUser(): Result<User?>
    suspend fun setCurrentUser(user: User): Result<Unit>
    suspend fun clearCurrentUser(): Result<Unit>

    // ============================================
    // Observable Flows
    // ============================================

    fun observeUser(userId: String): Flow<User?>
    fun observeCurrentUser(): Flow<User?>
    fun observeFollowCounts(userId: String): Flow<FollowCounts>
}

/**
 * Data for completing user profile (onboarding).
 */
data class ProfileCompletionData(
    val firstName: String,
    val lastName: String,
    val username: String,
    val bio: String? = null,
    val avatarUrl: String? = null,
    val tags: List<String> = emptyList(),
    val socialLinks: SocialLinks = SocialLinks(),
)

/**
 * Username validation result.
 */
sealed class UsernameValidationResult {
    data object Valid : UsernameValidationResult()
    data object TooShort : UsernameValidationResult()
    data object TooLong : UsernameValidationResult()
    data object InvalidCharacters : UsernameValidationResult()
    data object AlreadyTaken : UsernameValidationResult()
    data object Reserved : UsernameValidationResult()
}

/**
 * Follow counts for a user.
 */
data class FollowCounts(
    val followers: Int,
    val following: Int,
)
