package com.app.test.repository

import com.partygallery.domain.model.MediaContent
import com.partygallery.domain.model.MediaType
import com.partygallery.domain.model.PartyMood
import com.partygallery.domain.repository.MediaRepository
import com.partygallery.domain.repository.MediaUploadItem
import com.partygallery.domain.repository.UploadProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock

/**
 * Fake MediaRepository for testing.
 *
 * This fake allows you to control media data behavior in tests
 * by setting up success/failure scenarios before each test.
 */
class FakeMediaRepository : MediaRepository {

    // ============================================
    // Test State
    // ============================================

    private val media = mutableMapOf<String, MediaContent>()
    private val likes = mutableMapOf<String, MutableSet<String>>()
    private val tags = mutableMapOf<String, MutableList<String>>()
    private val mediaByPartyFlows = mutableMapOf<String, MutableStateFlow<List<MediaContent>>>()
    private val likesCountFlows = mutableMapOf<String, MutableStateFlow<Int>>()
    private val isLikedFlows = mutableMapOf<String, MutableStateFlow<Boolean>>()
    private val uploadProgressFlows = mutableMapOf<String, MutableStateFlow<UploadProgress>>()

    // Configurable behaviors
    private var shouldFail: Boolean = false
    private var failureError: Exception = Exception("Test error")
    private var uploadDelay: Long = 0

    // ============================================
    // Test Setup Methods
    // ============================================

    fun setMedia(vararg mediaList: MediaContent) {
        media.clear()
        mediaList.forEach { media[it.id] = it }
        updatePartyFlows()
    }

    fun addMedia(item: MediaContent) {
        media[item.id] = item
        updatePartyFlows()
    }

    fun setShouldFail(fail: Boolean, error: Exception = Exception("Test error")) {
        shouldFail = fail
        failureError = error
    }

    fun setUploadDelay(delayMs: Long) {
        uploadDelay = delayMs
    }

    fun setLiked(mediaId: String, userId: String, isLiked: Boolean) {
        val mediaLikes = likes.getOrPut(mediaId) { mutableSetOf() }
        if (isLiked) {
            mediaLikes.add(userId)
        } else {
            mediaLikes.remove(userId)
        }
        likesCountFlows[mediaId]?.value = mediaLikes.size
        isLikedFlows["$mediaId:$userId"]?.value = isLiked
    }

    fun reset() {
        media.clear()
        likes.clear()
        tags.clear()
        mediaByPartyFlows.clear()
        likesCountFlows.clear()
        isLikedFlows.clear()
        uploadProgressFlows.clear()
        shouldFail = false
        uploadDelay = 0
    }

    private fun updatePartyFlows() {
        val mediaByParty = media.values.groupBy { it.partyEventId }
        mediaByParty.forEach { (partyId, mediaList) ->
            mediaByPartyFlows[partyId]?.value = mediaList
        }
    }

    // ============================================
    // MediaRepository Implementation
    // ============================================

    override suspend fun createMedia(media: MediaContent): Result<MediaContent> {
        if (shouldFail) return Result.failure(failureError)
        this.media[media.id] = media
        updatePartyFlows()
        return Result.success(media)
    }

    override suspend fun getMediaById(mediaId: String): Result<MediaContent?> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(media[mediaId])
    }

    override suspend fun updateMedia(media: MediaContent): Result<MediaContent> {
        if (shouldFail) return Result.failure(failureError)
        this.media[media.id] = media
        updatePartyFlows()
        return Result.success(media)
    }

    override suspend fun deleteMedia(mediaId: String): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        media.remove(mediaId)
        likes.remove(mediaId)
        tags.remove(mediaId)
        updatePartyFlows()
        return Result.success(Unit)
    }

    override suspend fun getMediaByParty(partyId: String, limit: Int): Result<List<MediaContent>> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(
            media.values
                .filter { it.partyEventId == partyId }
                .take(limit)
        )
    }

    override suspend fun getMediaByUser(userId: String, limit: Int): Result<List<MediaContent>> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(
            media.values
                .filter { it.uploaderId == userId }
                .take(limit)
        )
    }

    override suspend fun getMediaByMood(mood: PartyMood, limit: Int): Result<List<MediaContent>> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(
            media.values
                .filter { it.mood == mood }
                .take(limit)
        )
    }

    override suspend fun getMediaByType(type: MediaType, limit: Int): Result<List<MediaContent>> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(
            media.values
                .filter { it.type == type }
                .take(limit)
        )
    }

    override suspend fun getRecentMedia(limit: Int): Result<List<MediaContent>> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(
            media.values
                .sortedByDescending { it.createdAt }
                .take(limit)
        )
    }

    override suspend fun getTrendingMedia(limit: Int): Result<List<MediaContent>> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(
            media.values
                .sortedByDescending { it.socialMetrics.likesCount }
                .take(limit)
        )
    }

    override suspend fun uploadMedia(
        partyId: String,
        localPath: String,
        type: MediaType,
        mood: PartyMood?,
        caption: String?,
    ): Result<MediaContent> {
        if (shouldFail) return Result.failure(failureError)

        val newMedia = MediaContent(
            id = "media-${Clock.System.now().toEpochMilliseconds()}",
            partyEventId = partyId,
            uploaderId = "test-user",
            type = type,
            url = "https://example.com/media/${localPath.substringAfterLast('/')}",
            thumbnailUrl = "https://example.com/thumb/${localPath.substringAfterLast('/')}",
            caption = caption,
            mood = mood,
            createdAt = Clock.System.now(),
        )
        media[newMedia.id] = newMedia
        updatePartyFlows()
        return Result.success(newMedia)
    }

    override suspend fun uploadMultipleMedia(
        partyId: String,
        items: List<MediaUploadItem>,
    ): Result<List<MediaContent>> {
        if (shouldFail) return Result.failure(failureError)

        val uploadedMedia = items.map { item ->
            MediaContent(
                id = "media-${Clock.System.now().toEpochMilliseconds()}-${item.localPath.hashCode()}",
                partyEventId = partyId,
                uploaderId = "test-user",
                type = item.type,
                url = "https://example.com/media/${item.localPath.substringAfterLast('/')}",
                thumbnailUrl = "https://example.com/thumb/${item.localPath.substringAfterLast('/')}",
                caption = item.caption,
                mood = item.mood,
                createdAt = Clock.System.now(),
            )
        }

        uploadedMedia.forEach { media[it.id] = it }
        updatePartyFlows()
        return Result.success(uploadedMedia)
    }

    override fun observeUploadProgress(uploadId: String): Flow<UploadProgress> {
        return uploadProgressFlows.getOrPut(uploadId) {
            MutableStateFlow(UploadProgress.Pending)
        }
    }

    override suspend fun likeMedia(mediaId: String, userId: String): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        likes.getOrPut(mediaId) { mutableSetOf() }.add(userId)
        likesCountFlows[mediaId]?.value = likes[mediaId]?.size ?: 0
        isLikedFlows["$mediaId:$userId"]?.value = true
        return Result.success(Unit)
    }

    override suspend fun unlikeMedia(mediaId: String, userId: String): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        likes[mediaId]?.remove(userId)
        likesCountFlows[mediaId]?.value = likes[mediaId]?.size ?: 0
        isLikedFlows["$mediaId:$userId"]?.value = false
        return Result.success(Unit)
    }

    override suspend fun isLikedByUser(mediaId: String, userId: String): Result<Boolean> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(likes[mediaId]?.contains(userId) ?: false)
    }

    override suspend fun getLikesCount(mediaId: String): Result<Int> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(likes[mediaId]?.size ?: 0)
    }

    override suspend fun tagUsers(mediaId: String, userIds: List<String>): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        tags[mediaId] = userIds.toMutableList()
        return Result.success(Unit)
    }

    override suspend fun removeUserTag(mediaId: String, userId: String): Result<Unit> {
        if (shouldFail) return Result.failure(failureError)
        tags[mediaId]?.remove(userId)
        return Result.success(Unit)
    }

    override suspend fun getTaggedUsers(mediaId: String): Result<List<String>> {
        if (shouldFail) return Result.failure(failureError)
        return Result.success(tags[mediaId] ?: emptyList())
    }

    override fun observeMediaByParty(partyId: String): Flow<List<MediaContent>> {
        return mediaByPartyFlows.getOrPut(partyId) {
            MutableStateFlow(media.values.filter { it.partyEventId == partyId })
        }
    }

    override fun observeLikesCount(mediaId: String): Flow<Int> {
        return likesCountFlows.getOrPut(mediaId) {
            MutableStateFlow(likes[mediaId]?.size ?: 0)
        }
    }

    override fun observeIsLiked(mediaId: String, userId: String): Flow<Boolean> {
        return isLikedFlows.getOrPut("$mediaId:$userId") {
            MutableStateFlow(likes[mediaId]?.contains(userId) ?: false)
        }
    }

    // ============================================
    // Test Helper Methods
    // ============================================

    /**
     * Simulate upload progress for testing.
     */
    fun simulateUploadProgress(uploadId: String, progress: UploadProgress) {
        uploadProgressFlows.getOrPut(uploadId) {
            MutableStateFlow(UploadProgress.Pending)
        }.value = progress
    }
}
