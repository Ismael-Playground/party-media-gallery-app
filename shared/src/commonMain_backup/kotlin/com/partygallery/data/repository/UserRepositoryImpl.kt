package com.partygallery.data.repository

import com.partygallery.domain.repository.UserRepository
import com.partygallery.domain.model.user.User
import com.partygallery.domain.model.user.SocialLink
import com.partygallery.data.datasource.remote.UserRemoteDataSource
import com.partygallery.data.datasource.local.UserLocalDataSource
import com.partygallery.data.mapper.UserMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource,
    private val mapper: UserMapper
) : UserRepository {

    override suspend fun createUser(user: User): Result<User> = runCatching {
        val remoteUser = remoteDataSource.createUser(mapper.toRemoteUser(user)).getOrThrow()
        val localUser = mapper.toDomainUser(remoteUser)
        localDataSource.saveUser(localUser)
        localUser
    }

    override suspend fun getUserById(userId: String): Result<User?> = runCatching {
        // Try local first for better performance
        localDataSource.getUserById(userId)?.let { return@runCatching it }
        
        // Fallback to remote
        val remoteUser = remoteDataSource.getUserById(userId).getOrThrow() ?: return@runCatching null
        val domainUser = mapper.toDomainUser(remoteUser)
        localDataSource.saveUser(domainUser)
        domainUser
    }

    override suspend fun getUserByUsername(username: String): Result<User?> = runCatching {
        val remoteUser = remoteDataSource.getUserByUsername(username).getOrThrow() ?: return@runCatching null
        val domainUser = mapper.toDomainUser(remoteUser)
        localDataSource.saveUser(domainUser)
        domainUser
    }

    override suspend fun getUserByEmail(email: String): Result<User?> = runCatching {
        val remoteUser = remoteDataSource.getUserByEmail(email).getOrThrow() ?: return@runCatching null
        val domainUser = mapper.toDomainUser(remoteUser)
        localDataSource.saveUser(domainUser)
        domainUser
    }

    override suspend fun updateUser(user: User): Result<User> = runCatching {
        val remoteUser = remoteDataSource.updateUser(mapper.toRemoteUser(user)).getOrThrow()
        val localUser = mapper.toDomainUser(remoteUser)
        localDataSource.saveUser(localUser)
        localUser
    }

    override suspend fun deleteUser(userId: String): Result<Unit> = runCatching {
        remoteDataSource.deleteUser(userId).getOrThrow()
        localDataSource.deleteUser(userId)
    }

    override suspend fun searchUsers(query: String, limit: Int): Result<List<User>> = runCatching {
        val remoteUsers = remoteDataSource.searchUsers(query, limit).getOrThrow()
        remoteUsers.map { mapper.toDomainUser(it) }
    }

    override suspend fun getUsersByIds(userIds: List<String>): Result<List<User>> = runCatching {
        val remoteUsers = remoteDataSource.getUsersByIds(userIds).getOrThrow()
        remoteUsers.map { mapper.toDomainUser(it) }
    }

    override suspend fun updateUserAvatar(userId: String, avatarUrl: String): Result<Unit> = runCatching {
        remoteDataSource.updateUserAvatar(userId, avatarUrl).getOrThrow()
        localDataSource.updateUserAvatar(userId, avatarUrl)
    }

    override suspend fun updateUserSocialLinks(userId: String, socialLinks: List<SocialLink>): Result<Unit> = runCatching {
        remoteDataSource.updateUserSocialLinks(userId, socialLinks).getOrThrow()
        localDataSource.updateUserSocialLinks(userId, socialLinks)
    }

    override suspend fun updateUserTags(userId: String, tags: List<String>): Result<Unit> = runCatching {
        remoteDataSource.updateUserTags(userId, tags).getOrThrow()
        localDataSource.updateUserTags(userId, tags)
    }

    override fun observeUser(userId: String): Flow<User?> {
        return remoteDataSource.observeUser(userId).map { remoteUser ->
            remoteUser?.let { 
                val domainUser = mapper.toDomainUser(it)
                localDataSource.saveUser(domainUser)
                domainUser
            }
        }
    }

    override fun observeUserFollowCounts(userId: String): Flow<Pair<Int, Int>> {
        return remoteDataSource.observeUserFollowCounts(userId)
    }

    override suspend fun getCurrentUser(): Result<User?> = runCatching {
        localDataSource.getCurrentUser() ?: run {
            val remoteUser = remoteDataSource.getCurrentUser().getOrThrow() ?: return@runCatching null
            val domainUser = mapper.toDomainUser(remoteUser)
            localDataSource.saveCurrentUser(domainUser)
            domainUser
        }
    }

    override fun observeCurrentUser(): Flow<User?> {
        return localDataSource.observeCurrentUser()
    }
}