package com.partygallery.presentation.state

import com.partygallery.domain.model.PartyEvent
import com.partygallery.domain.model.PartyMood

/**
 * Home feed state.
 *
 * S3-006: HomeState con feed data
 */
data class HomeState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val feedItems: List<FeedItem> = emptyList(),
    val liveParties: List<PartyEvent> = emptyList(),
    val selectedFilter: FeedFilter = FeedFilter.All,
)

/**
 * Feed item - can be a party event or media content.
 */
sealed class FeedItem {
    abstract val id: String
    abstract val timestamp: Long

    data class PartyCard(
        override val id: String,
        override val timestamp: Long,
        val title: String,
        val hostName: String,
        val hostAvatarUrl: String?,
        val coverImageUrl: String?,
        val venueName: String,
        val attendeesCount: Int,
        val isLive: Boolean,
        val mood: PartyMood?,
        val tags: List<String>,
    ) : FeedItem()

    data class MediaPost(
        override val id: String,
        override val timestamp: Long,
        val partyId: String,
        val partyTitle: String,
        val userName: String,
        val userAvatarUrl: String?,
        val mediaUrl: String,
        val mediaType: MediaType,
        val likesCount: Int,
        val commentsCount: Int,
        val isLiked: Boolean,
        val mood: PartyMood?,
        val caption: String?,
    ) : FeedItem()
}

/**
 * Media type for feed items.
 */
enum class MediaType {
    PHOTO,
    VIDEO,
}

/**
 * Feed filter options.
 */
enum class FeedFilter(val label: String) {
    All("All"),
    Live("Live"),
    Trending("Trending"),
    Following("Following"),
    Nearby("Nearby"),
}
