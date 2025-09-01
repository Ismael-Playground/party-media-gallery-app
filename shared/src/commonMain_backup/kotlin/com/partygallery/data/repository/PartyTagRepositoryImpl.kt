package com.partygallery.data.repository

import com.partygallery.domain.repository.PartyTagRepository
import com.partygallery.domain.model.party.PartyTag
import com.partygallery.data.datasource.remote.PartyTagRemoteDataSource
import com.partygallery.data.datasource.local.PartyTagLocalDataSource
import com.partygallery.data.mapper.PartyTagMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PartyTagRepositoryImpl(
    private val remoteDataSource: PartyTagRemoteDataSource,
    private val localDataSource: PartyTagLocalDataSource,
    private val mapper: PartyTagMapper
) : PartyTagRepository {

    override suspend fun createPartyTag(tag: PartyTag): Result<PartyTag> = runCatching {
        val remoteTag = remoteDataSource.createPartyTag(mapper.toRemotePartyTag(tag)).getOrThrow()
        val localTag = mapper.toDomainPartyTag(remoteTag)
        localDataSource.savePartyTag(localTag)
        localTag
    }

    override suspend fun getPartyTagById(tagId: String): Result<PartyTag?> = runCatching {
        localDataSource.getPartyTagById(tagId) ?: run {
            val remoteTag = remoteDataSource.getPartyTagById(tagId).getOrThrow() ?: return@runCatching null
            val domainTag = mapper.toDomainPartyTag(remoteTag)
            localDataSource.savePartyTag(domainTag)
            domainTag
        }
    }

    override suspend fun getPartyTagByName(name: String): Result<PartyTag?> = runCatching {
        val remoteTag = remoteDataSource.getPartyTagByName(name).getOrThrow() ?: return@runCatching null
        val domainTag = mapper.toDomainPartyTag(remoteTag)
        localDataSource.savePartyTag(domainTag)
        domainTag
    }

    override suspend fun updatePartyTag(tag: PartyTag): Result<PartyTag> = runCatching {
        val remoteTag = remoteDataSource.updatePartyTag(mapper.toRemotePartyTag(tag)).getOrThrow()
        val localTag = mapper.toDomainPartyTag(remoteTag)
        localDataSource.savePartyTag(localTag)
        localTag
    }

    override suspend fun deletePartyTag(tagId: String): Result<Unit> = runCatching {
        remoteDataSource.deletePartyTag(tagId).getOrThrow()
        localDataSource.deletePartyTag(tagId)
    }

    override suspend fun getAllPartyTags(): Result<List<PartyTag>> = runCatching {
        val remoteTags = remoteDataSource.getAllPartyTags().getOrThrow()
        val domainTags = remoteTags.map { mapper.toDomainPartyTag(it) }
        localDataSource.savePartyTags(domainTags)
        domainTags
    }

    override suspend fun getPopularPartyTags(limit: Int): Result<List<PartyTag>> = runCatching {
        val remoteTags = remoteDataSource.getPopularPartyTags(limit).getOrThrow()
        val domainTags = remoteTags.map { mapper.toDomainPartyTag(it) }
        localDataSource.savePartyTags(domainTags)
        domainTags
    }

    override suspend fun getTrendingPartyTags(limit: Int): Result<List<PartyTag>> = runCatching {
        val remoteTags = remoteDataSource.getTrendingPartyTags(limit).getOrThrow()
        remoteTags.map { mapper.toDomainPartyTag(it) }
    }

    override suspend fun searchPartyTags(query: String, limit: Int): Result<List<PartyTag>> = runCatching {
        val remoteTags = remoteDataSource.searchPartyTags(query, limit).getOrThrow()
        remoteTags.map { mapper.toDomainPartyTag(it) }
    }

    override suspend fun getPartyTagsByCategory(category: String): Result<List<PartyTag>> = runCatching {
        val remoteTags = remoteDataSource.getPartyTagsByCategory(category).getOrThrow()
        remoteTags.map { mapper.toDomainPartyTag(it) }
    }

    override suspend fun getMusicGenreTags(): Result<List<PartyTag>> = runCatching {
        val remoteTags = remoteDataSource.getMusicGenreTags().getOrThrow()
        val domainTags = remoteTags.map { mapper.toDomainPartyTag(it) }
        localDataSource.savePartyTags(domainTags)
        domainTags
    }

    override suspend fun getPartyTypeTags(): Result<List<PartyTag>> = runCatching {
        val remoteTags = remoteDataSource.getPartyTypeTags().getOrThrow()
        val domainTags = remoteTags.map { mapper.toDomainPartyTag(it) }
        localDataSource.savePartyTags(domainTags)
        domainTags
    }

    override suspend fun incrementTagUsage(tagId: String): Result<Unit> = runCatching {
        remoteDataSource.incrementTagUsage(tagId).getOrThrow()
        localDataSource.incrementTagUsage(tagId)
    }

    override suspend fun getTagUsageCount(tagId: String): Result<Int> = runCatching {
        remoteDataSource.getTagUsageCount(tagId).getOrThrow()
    }

    override suspend fun getUserPartyTags(userId: String): Result<List<PartyTag>> = runCatching {
        val remoteTags = remoteDataSource.getUserPartyTags(userId).getOrThrow()
        val domainTags = remoteTags.map { mapper.toDomainPartyTag(it) }
        localDataSource.savePartyTags(domainTags)
        domainTags
    }

    override suspend fun saveUserPartyTags(userId: String, tagIds: List<String>): Result<Unit> = runCatching {
        remoteDataSource.saveUserPartyTags(userId, tagIds).getOrThrow()
        localDataSource.saveUserPartyTags(userId, tagIds)
    }

    override fun observePopularPartyTags(): Flow<List<PartyTag>> {
        return remoteDataSource.observePopularPartyTags().map { remoteTags ->
            val domainTags = remoteTags.map { mapper.toDomainPartyTag(it) }
            localDataSource.savePartyTags(domainTags)
            domainTags
        }
    }

    override fun observeTrendingPartyTags(): Flow<List<PartyTag>> {
        return remoteDataSource.observeTrendingPartyTags().map { remoteTags ->
            remoteTags.map { mapper.toDomainPartyTag(it) }
        }
    }

    override fun observeUserPartyTags(userId: String): Flow<List<PartyTag>> {
        return remoteDataSource.observeUserPartyTags(userId).map { remoteTags ->
            val domainTags = remoteTags.map { mapper.toDomainPartyTag(it) }
            localDataSource.savePartyTags(domainTags)
            domainTags
        }
    }
}