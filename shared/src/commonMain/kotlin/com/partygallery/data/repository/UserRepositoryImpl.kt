package com.partygallery.data.repository

import com.partygallery.data.datasource.UserDataSource
import com.partygallery.data.dto.UserDto
import com.partygallery.data.mapper.toDomain
import com.partygallery.data.mapper.toDto
import com.partygallery.domain.model.SocialLinks
import com.partygallery.domain.model.User
import com.partygallery.domain.model.UserSummary
import com.partygallery.domain.repository.FollowCounts
import com.partygallery.domain.repository.ProfileCompletionData
import com.partygallery.domain.repository.UserRepository
import com.partygallery.domain.repository.UsernameValidationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

/**
 * Implementation of UserRepository using Firebase Firestore.
 *
 * S2.5-001: UserRepositoryImpl con Firebase Firestore
 */
class UserRepositoryImpl(
    private val userDataSource: UserDataSource,
) : UserRepository {

    // In-memory cache for current user
    private val _currentUserFlow = MutableStateFlow<User?>(null)

    // ============================================
    // CRUD Operations
    // ============================================

    override suspend fun createUser(user: User): Result<User> {
        return try {
            val dto = user.toDto()
            val createdDto = userDataSource.createUser(dto)
            Result.success(createdDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserById(userId: String): Result<User?> {
        return try {
            val dto = userDataSource.getUserById(userId)
            Result.success(dto?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserByUsername(username: String): Result<User?> {
        return try {
            val dto = userDataSource.getUserByUsername(username)
            Result.success(dto?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserByEmail(email: String): Result<User?> {
        return try {
            val dto = userDataSource.getUserByEmail(email)
            Result.success(dto?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(user: User): Result<User> {
        return try {
            val dto = user.toDto()
            val updatedDto = userDataSource.updateUser(dto)
            val updatedUser = updatedDto.toDomain()

            // Update cache if this is the current user
            if (_currentUserFlow.value?.id == user.id) {
                _currentUserFlow.value = updatedUser
            }

            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            userDataSource.deleteUser(userId)

            // Clear cache if this is the current user
            if (_currentUserFlow.value?.id == userId) {
                _currentUserFlow.value = null
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Search & Query
    // ============================================

    override suspend fun searchUsers(query: String, limit: Int): Result<List<UserSummary>> {
        return try {
            val dtos = userDataSource.searchUsers(query, limit)
            val summaries = dtos.map { dto ->
                UserSummary(
                    id = dto.id,
                    username = dto.username,
                    displayName = "${dto.firstName} ${dto.lastName}".trim(),
                    avatarUrl = dto.avatarUrl,
                    isVerified = dto.isVerified,
                )
            }
            Result.success(summaries)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUsersByIds(userIds: List<String>): Result<List<User>> {
        return try {
            val dtos = userDataSource.getUsersByIds(userIds)
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSuggestedUsers(userId: String, limit: Int): Result<List<UserSummary>> {
        return try {
            val dtos = userDataSource.getSuggestedUsers(userId, limit)
            val summaries = dtos.map { dto ->
                UserSummary(
                    id = dto.id,
                    username = dto.username,
                    displayName = "${dto.firstName} ${dto.lastName}".trim(),
                    avatarUrl = dto.avatarUrl,
                    isVerified = dto.isVerified,
                )
            }
            Result.success(summaries)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Profile Updates
    // ============================================

    override suspend fun updateAvatar(userId: String, avatarUrl: String): Result<Unit> {
        return try {
            userDataSource.updateField(userId, "avatar_url", avatarUrl)

            // Update cache if this is the current user
            _currentUserFlow.value?.let { currentUser ->
                if (currentUser.id == userId) {
                    _currentUserFlow.value = currentUser.copy(avatarUrl = avatarUrl)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCoverPhoto(userId: String, coverPhotoUrl: String): Result<Unit> {
        return try {
            userDataSource.updateField(userId, "cover_photo_url", coverPhotoUrl)

            // Update cache if this is the current user
            _currentUserFlow.value?.let { currentUser ->
                if (currentUser.id == userId) {
                    _currentUserFlow.value = currentUser.copy(coverPhotoUrl = coverPhotoUrl)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSocialLinks(userId: String, socialLinks: SocialLinks): Result<Unit> {
        return try {
            userDataSource.updateField(userId, "social_links", socialLinks.toDto())

            // Update cache if this is the current user
            _currentUserFlow.value?.let { currentUser ->
                if (currentUser.id == userId) {
                    _currentUserFlow.value = currentUser.copy(socialLinks = socialLinks)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTags(userId: String, tags: List<String>): Result<Unit> {
        return try {
            userDataSource.updateField(userId, "tags", tags)

            // Update cache if this is the current user
            _currentUserFlow.value?.let { currentUser ->
                if (currentUser.id == userId) {
                    _currentUserFlow.value = currentUser.copy(tags = tags)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completeProfile(
        userId: String,
        updates: ProfileCompletionData,
    ): Result<User> {
        return try {
            val existingDto = userDataSource.getUserById(userId)
                ?: throw IllegalStateException("User not found: $userId")

            val updatedDto = existingDto.copy(
                firstName = updates.firstName,
                lastName = updates.lastName,
                username = updates.username,
                bio = updates.bio,
                avatarUrl = updates.avatarUrl ?: existingDto.avatarUrl,
                tags = updates.tags,
                socialLinks = updates.socialLinks.toDto(),
                isProfileComplete = true,
                updatedAt = Clock.System.now().toEpochMilliseconds(),
            )

            val savedDto = userDataSource.updateUser(updatedDto)
            val user = savedDto.toDomain()

            // Update cache if this is the current user
            if (_currentUserFlow.value?.id == userId) {
                _currentUserFlow.value = user
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================
    // Username Validation
    // ============================================

    override suspend fun isUsernameAvailable(username: String): Result<Boolean> {
        return try {
            val isAvailable = userDataSource.isUsernameAvailable(username)
            Result.success(isAvailable)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun validateUsername(username: String): UsernameValidationResult {
        // Length validation
        if (username.length < 3) {
            return UsernameValidationResult.TooShort
        }
        if (username.length > 30) {
            return UsernameValidationResult.TooLong
        }

        // Character validation (alphanumeric and underscores only)
        val usernameRegex = Regex("^[a-zA-Z0-9_]+$")
        if (!usernameRegex.matches(username)) {
            return UsernameValidationResult.InvalidCharacters
        }

        // Reserved usernames
        val reservedUsernames = setOf(
            "admin", "administrator", "root", "system", "support",
            "help", "api", "www", "mail", "ftp", "partygallery",
            "party_gallery", "official", "staff", "moderator", "mod",
        )
        if (username.lowercase() in reservedUsernames) {
            return UsernameValidationResult.Reserved
        }

        // Check availability
        return try {
            val isAvailable = userDataSource.isUsernameAvailable(username)
            if (isAvailable) {
                UsernameValidationResult.Valid
            } else {
                UsernameValidationResult.AlreadyTaken
            }
        } catch (e: Exception) {
            // In case of error, assume valid to not block user
            UsernameValidationResult.Valid
        }
    }

    // ============================================
    // Current User
    // ============================================

    override suspend fun getCurrentUser(): Result<User?> {
        return Result.success(_currentUserFlow.value)
    }

    override suspend fun setCurrentUser(user: User): Result<Unit> {
        _currentUserFlow.value = user
        return Result.success(Unit)
    }

    override suspend fun clearCurrentUser(): Result<Unit> {
        _currentUserFlow.value = null
        return Result.success(Unit)
    }

    // ============================================
    // Observable Flows
    // ============================================

    override fun observeUser(userId: String): Flow<User?> {
        return userDataSource.observeUser(userId).map { dto ->
            dto?.toDomain()
        }
    }

    override fun observeCurrentUser(): Flow<User?> {
        return _currentUserFlow.asStateFlow()
    }

    override fun observeFollowCounts(userId: String): Flow<FollowCounts> {
        return userDataSource.observeFollowCounts(userId).map { (followers, following) ->
            FollowCounts(followers = followers, following = following)
        }
    }
}
