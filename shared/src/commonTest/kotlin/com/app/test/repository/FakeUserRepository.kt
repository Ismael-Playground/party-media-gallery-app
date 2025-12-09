package com.app.test.repository

import com.partygallery.domain.model.SocialLinks
import com.partygallery.domain.model.User
import com.partygallery.domain.model.UserSummary
import com.partygallery.domain.repository.FollowCounts
import com.partygallery.domain.repository.ProfileCompletionData
import com.partygallery.domain.repository.UserRepository
import com.partygallery.domain.repository.UsernameValidationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Fake UserRepository for testing.
 *
 * This fake allows you to control user data behavior in tests
 * by setting up success/failure scenarios before each test.
 */
class FakeUserRepository : UserRepository {

    // ============================================
    // Test State
    // ============================================

    private val users = mutableMapOf<String, User>()
    private var currentUser: User? = null
    private val _currentUserFlow = MutableStateFlow<User?>(null)
    private val userFlows = mutableMapOf<String, MutableStateFlow<User?>>()
    private val followCountsFlows = mutableMapOf<String, MutableStateFlow<FollowCounts>>()

    // Configurable behaviors
    private var shouldFail: Boolean = false
    private var failureError: Exception = Exception("Test error")
    private var usernameValidationResult: UsernameValidationResult = UsernameValidationResult.Valid
    private var isUsernameAvailableResult: Boolean = true

    // ============================================
    // Test Setup Methods
    // ============================================

    fun setUsers(vararg userList: User) {
        users.clear()
        userList.forEach { users[it.id] = it }
    }

    fun setCurrentUser(user: User?) {
        currentUser = user
        _currentUserFlow.value = user
    }

    fun setShouldFail(fail: Boolean, error: Exception = Exception("Test error")) {
        shouldFail = fail
        failureError = error
    }

    fun setUsernameValidationResult(result: UsernameValidationResult) {
        usernameValidationResult = result
    }

    fun setIsUsernameAvailable(available: Boolean) {
        isUsernameAvailableResult = available
    }

    fun reset() {
        users.clear()
        currentUser = null
        _currentUserFlow.value = null
        userFlows.clear()
        followCountsFlows.clear()
        shouldFail = false
        usernameValidationResult = UsernameValidationResult.Valid
        isUsernameAvailableResult = true
    }

    // ============================================
    // UserRepository Implementation
    // ============================================

    override suspend fun createUser(user: User): Result<User> {
        if (shouldFail) return Result.failure(failureError)
        users[user.id] = user
        return Result.success(user)
    }

    override suspend fun getUserById(userId: String): Result<User?> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(users[userId])
    }

    override suspend fun getUserByUsername(username: String): Result<User?> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(users.values.find { it.username == username })
    }

    override suspend fun getUserByEmail(email: String): Result<User?> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(users.values.find { it.email == email })
    }

    override suspend fun updateUser(user: User): Result<User> {
        if (shouldFail) return Result.failure(failureError)
        users[user.id] = user
        userFlows[user.id]?.value = user
        if (currentUser?.id == user.id) {
            currentUser = user
            _currentUserFlow.value = user
        }
        return Result.success(user)
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        users.remove(userId)
        return Result.success(Unit)
    }

    override suspend fun searchUsers(query: String, limit: Int): Result<List<UserSummary>> {
        if (shouldFail) return Result.failure(failureError)
        val results = users.values
            .filter { it.username.contains(query, ignoreCase = true) ||
                    it.firstName.contains(query, ignoreCase = true) ||
                    it.lastName.contains(query, ignoreCase = true) }
            .take(limit)
            .map { it.toSummary() }
        return Result.success(results)
    }

    override suspend fun getUsersByIds(userIds: List<String>): Result<List<User>> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(userIds.mapNotNull { users[it] })
    }

    override suspend fun getSuggestedUsers(userId: String, limit: Int): Result<List<UserSummary>> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(
            users.values
                .filter { it.id != userId }
                .take(limit)
                .map { it.toSummary() }
        )
    }

    override suspend fun updateAvatar(userId: String, avatarUrl: String): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        users[userId]?.let { user ->
            users[userId] = user.copy(avatarUrl = avatarUrl)
        }
        return Result.success(Unit)
    }

    override suspend fun updateCoverPhoto(userId: String, coverPhotoUrl: String): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        // Note: User model might need coverPhotoUrl field
        return Result.success(Unit)
    }

    override suspend fun updateSocialLinks(userId: String, socialLinks: SocialLinks): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        users[userId]?.let { user ->
            users[userId] = user.copy(socialLinks = socialLinks)
        }
        return Result.success(Unit)
    }

    override suspend fun updateTags(userId: String, tags: List<String>): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        users[userId]?.let { user ->
            users[userId] = user.copy(tags = tags)
        }
        return Result.success(Unit)
    }

    override suspend fun completeProfile(userId: String, updates: ProfileCompletionData): Result<User> {
        if (shouldFail) return Result.failure(failureError)
        val existingUser = users[userId] ?: return Result.failure(Exception("User not found"))
        val updatedUser = existingUser.copy(
            firstName = updates.firstName,
            lastName = updates.lastName,
            username = updates.username,
            bio = updates.bio ?: existingUser.bio,
            avatarUrl = updates.avatarUrl ?: existingUser.avatarUrl,
            tags = updates.tags.ifEmpty { existingUser.tags },
            socialLinks = updates.socialLinks,
            isProfileComplete = true,
        )
        users[userId] = updatedUser
        return Result.success(updatedUser)
    }

    override suspend fun isUsernameAvailable(username: String): Result<Boolean> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(isUsernameAvailableResult)
    }

    override suspend fun validateUsername(username: String): UsernameValidationResult {
        return usernameValidationResult
    }

    override suspend fun getCurrentUser(): Result<User?> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(currentUser)
    }

    override suspend fun setCurrentUser(user: User): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        currentUser = user
        _currentUserFlow.value = user
        users[user.id] = user
        return Result.success(Unit)
    }

    override suspend fun clearCurrentUser(): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        currentUser = null
        _currentUserFlow.value = null
        return Result.success(Unit)
    }

    override fun observeUser(userId: String): Flow<User?> {
        return userFlows.getOrPut(userId) {
            MutableStateFlow(users[userId])
        }
    }

    override fun observeCurrentUser(): Flow<User?> = _currentUserFlow

    override fun observeFollowCounts(userId: String): Flow<FollowCounts> {
        return followCountsFlows.getOrPut(userId) {
            MutableStateFlow(FollowCounts(followers = 0, following = 0))
        }
    }

    // ============================================
    // Helper Methods
    // ============================================

    private fun User.toSummary(): UserSummary = UserSummary(
        id = id,
        username = username,
        displayName = "$firstName $lastName",
        avatarUrl = avatarUrl,
        isVerified = isVerified,
    )
}
