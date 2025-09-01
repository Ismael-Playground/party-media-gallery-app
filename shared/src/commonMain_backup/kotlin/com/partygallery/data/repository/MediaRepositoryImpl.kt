package com.partygallery.data.repository

import com.partygallery.domain.repository.MediaRepository
import com.partygallery.domain.model.media.PartyMediaContent
import com.partygallery.domain.model.media.MediaType
import com.partygallery.domain.model.media.PartyMood
import com.partygallery.data.datasource.remote.MediaRemoteDataSource
import com.partygallery.data.datasource.local.MediaLocalDataSource
import com.partygallery.data.mapper.MediaMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class MediaRepositoryImpl(
    private val remoteDataSource: MediaRemoteDataSource,
    private val localDataSource: MediaLocalDataSource,
    private val mapper: MediaMapper
) : MediaRepository {

    override suspend fun uploadMedia(
        content: ByteArray,
        filename: String,
        mediaType: MediaType,
        partyEventId: String?,
        metadata: Map<String, Any>
    ): Result<String> = runCatching {
        remoteDataSource.uploadMedia(content, filename, mediaType, partyEventId, metadata).getOrThrow()
    }

    override suspend fun createMediaContent(media: PartyMediaContent): Result<PartyMediaContent> = runCatching {
        val remoteMedia = remoteDataSource.createMediaContent(mapper.toRemoteMedia(media)).getOrThrow()
        val localMedia = mapper.toDomainMedia(remoteMedia)
        localDataSource.saveMediaContent(localMedia)
        localMedia
    }

    override suspend fun getMediaContentById(mediaId: String): Result<PartyMediaContent?> = runCatching {
        // Try local first
        localDataSource.getMediaContentById(mediaId)?.let { return@runCatching it }
        
        // Fallback to remote
        val remoteMedia = remoteDataSource.getMediaContentById(mediaId).getOrThrow() ?: return@runCatching null
        val domainMedia = mapper.toDomainMedia(remoteMedia)
        localDataSource.saveMediaContent(domainMedia)
        domainMedia
    }

    override suspend fun updateMediaContent(media: PartyMediaContent): Result<PartyMediaContent> = runCatching {
        val remoteMedia = remoteDataSource.updateMediaContent(mapper.toRemoteMedia(media)).getOrThrow()
        val localMedia = mapper.toDomainMedia(remoteMedia)
        localDataSource.saveMediaContent(localMedia)
        localMedia
    }

    override suspend fun deleteMediaContent(mediaId: String): Result<Unit> = runCatching {
        remoteDataSource.deleteMediaContent(mediaId).getOrThrow()
        localDataSource.deleteMediaContent(mediaId)
    }

    override suspend fun getMediaByUser(userId: String, limit: Int, offset: Int): Result<List<PartyMediaContent>> = runCatching {
        val remoteMedia = remoteDataSource.getMediaByUser(userId, limit, offset).getOrThrow()
        val domainMedia = remoteMedia.map { mapper.toDomainMedia(it) }
        localDataSource.saveMediaContents(domainMedia)
        domainMedia
    }

    override suspend fun getMediaByPartyEvent(eventId: String, limit: Int): Result<List<PartyMediaContent>> = runCatching {
        val remoteMedia = remoteDataSource.getMediaByPartyEvent(eventId, limit).getOrThrow()
        val domainMedia = remoteMedia.map { mapper.toDomainMedia(it) }
        localDataSource.saveMediaContents(domainMedia)
        domainMedia
    }

    override suspend fun getMediaByType(mediaType: MediaType, limit: Int): Result<List<PartyMediaContent>> = runCatching {
        val remoteMedia = remoteDataSource.getMediaByType(mediaType, limit).getOrThrow()
        remoteMedia.map { mapper.toDomainMedia(it) }
    }

    override suspend fun getMediaByMood(mood: PartyMood, limit: Int): Result<List<PartyMediaContent>> = runCatching {
        val remoteMedia = remoteDataSource.getMediaByMood(mood, limit).getOrThrow()
        remoteMedia.map { mapper.toDomainMedia(it) }
    }

    override suspend fun getPublicPartyMedia(limit: Int, offset: Int): Result<List<PartyMediaContent>> = runCatching {
        val remoteMedia = remoteDataSource.getPublicPartyMedia(limit, offset).getOrThrow()
        remoteMedia.map { mapper.toDomainMedia(it) }
    }

    override suspend fun getTrendingPartyMedia(limit: Int): Result<List<PartyMediaContent>> = runCatching {
        val remoteMedia = remoteDataSource.getTrendingPartyMedia(limit).getOrThrow()
        remoteMedia.map { mapper.toDomainMedia(it) }
    }

    override suspend fun getPartyFeedMedia(userId: String, limit: Int, offset: Int): Result<List<PartyMediaContent>> = runCatching {
        val remoteMedia = remoteDataSource.getPartyFeedMedia(userId, limit, offset).getOrThrow()
        val domainMedia = remoteMedia.map { mapper.toDomainMedia(it) }
        localDataSource.saveMediaContents(domainMedia)
        domainMedia
    }

    override suspend fun searchMediaContent(
        query: String,
        mediaType: MediaType?,
        tags: List<String>?,
        dateRange: Pair<Instant, Instant>?,
        limit: Int
    ): Result<List<PartyMediaContent>> = runCatching {
        val remoteMedia = remoteDataSource.searchMediaContent(query, mediaType, tags, dateRange, limit).getOrThrow()
        remoteMedia.map { mapper.toDomainMedia(it) }
    }

    override suspend fun likeMedia(mediaId: String, userId: String): Result<Unit> = runCatching {
        remoteDataSource.likeMedia(mediaId, userId).getOrThrow()
        localDataSource.updateMediaLikeStatus(mediaId, true)
    }

    override suspend fun unlikeMedia(mediaId: String, userId: String): Result<Unit> = runCatching {
        remoteDataSource.unlikeMedia(mediaId, userId).getOrThrow()
        localDataSource.updateMediaLikeStatus(mediaId, false)
    }

    override suspend fun isMediaLikedByUser(mediaId: String, userId: String): Result<Boolean> = runCatching {
        remoteDataSource.isMediaLikedByUser(mediaId, userId).getOrThrow()
    }

    override suspend fun addMediaToFavorites(mediaId: String, userId: String): Result<Unit> = runCatching {
        remoteDataSource.addMediaToFavorites(mediaId, userId).getOrThrow()
        localDataSource.addMediaToFavorites(mediaId, userId)
    }

    override suspend fun removeMediaFromFavorites(mediaId: String, userId: String): Result<Unit> = runCatching {
        remoteDataSource.removeMediaFromFavorites(mediaId, userId).getOrThrow()
        localDataSource.removeMediaFromFavorites(mediaId, userId)
    }

    override suspend fun getUserFavoriteMedia(userId: String, limit: Int): Result<List<PartyMediaContent>> = runCatching {
        val remoteMedia = remoteDataSource.getUserFavoriteMedia(userId, limit).getOrThrow()
        remoteMedia.map { mapper.toDomainMedia(it) }
    }

    override suspend fun tagPeopleInMedia(mediaId: String, userIds: List<String>): Result<Unit> = runCatching {
        remoteDataSource.tagPeopleInMedia(mediaId, userIds).getOrThrow()
        localDataSource.updateMediaPeopleTags(mediaId, userIds)
    }

    override suspend fun getMediaWithUserTag(userId: String): Result<List<PartyMediaContent>> = runCatching {
        val remoteMedia = remoteDataSource.getMediaWithUserTag(userId).getOrThrow()
        remoteMedia.map { mapper.toDomainMedia(it) }
    }

    override suspend fun generateThumbnail(mediaUrl: String, mediaType: MediaType): Result<String> = runCatching {
        remoteDataSource.generateThumbnail(mediaUrl, mediaType).getOrThrow()
    }

    override suspend fun compressMedia(mediaUrl: String, quality: Float): Result<String> = runCatching {
        remoteDataSource.compressMedia(mediaUrl, quality).getOrThrow()
    }

    override fun observeMediaContent(mediaId: String): Flow<PartyMediaContent?> {
        return remoteDataSource.observeMediaContent(mediaId).map { remoteMedia ->
            remoteMedia?.let {
                val domainMedia = mapper.toDomainMedia(it)
                localDataSource.saveMediaContent(domainMedia)
                domainMedia
            }
        }
    }

    override fun observePartyEventMedia(eventId: String): Flow<List<PartyMediaContent>> {
        return remoteDataSource.observePartyEventMedia(eventId).map { remoteMedia ->
            val domainMedia = remoteMedia.map { mapper.toDomainMedia(it) }
            localDataSource.saveMediaContents(domainMedia)
            domainMedia
        }
    }

    override fun observeLivePartyMedia(): Flow<List<PartyMediaContent>> {
        return remoteDataSource.observeLivePartyMedia().map { remoteMedia ->
            remoteMedia.map { mapper.toDomainMedia(it) }
        }
    }

    override fun observeUserMedia(userId: String): Flow<List<PartyMediaContent>> {
        return remoteDataSource.observeUserMedia(userId).map { remoteMedia ->
            val domainMedia = remoteMedia.map { mapper.toDomainMedia(it) }
            localDataSource.saveMediaContents(domainMedia)
            domainMedia
        }
    }
}