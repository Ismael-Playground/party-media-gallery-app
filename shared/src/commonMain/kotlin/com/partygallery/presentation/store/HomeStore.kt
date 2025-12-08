package com.partygallery.presentation.store

import com.partygallery.domain.model.PartyMood
import com.partygallery.presentation.intent.HomeIntent
import com.partygallery.presentation.state.FeedFilter
import com.partygallery.presentation.state.FeedItem
import com.partygallery.presentation.state.HomeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * Home screen MVI store.
 *
 * S3-008: HomeStore MVI
 *
 * Note: Uses Dispatchers.Default for Desktop compatibility
 * (Dispatchers.Main requires platform-specific dispatcher)
 */
class HomeStore(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        processIntent(HomeIntent.LoadFeed)
    }

    fun processIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadFeed -> loadFeed()
            is HomeIntent.RefreshFeed -> refreshFeed()
            is HomeIntent.LoadMore -> loadMore()
            is HomeIntent.SelectFilter -> selectFilter(intent.filter)
            is HomeIntent.LikePost -> likePost(intent.postId)
            is HomeIntent.UnlikePost -> unlikePost(intent.postId)
            is HomeIntent.OpenParty -> { /* Navigation handled by UI */ }
            is HomeIntent.OpenProfile -> { /* Navigation handled by UI */ }
            is HomeIntent.DismissError -> dismissError()
        }
    }

    private fun loadFeed() {
        scope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Simulate network delay
            delay(800)

            // Mock data
            val mockFeedItems = generateMockFeedItems()

            _state.update {
                it.copy(
                    isLoading = false,
                    feedItems = mockFeedItems,
                )
            }
        }
    }

    private fun refreshFeed() {
        scope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }

            delay(1000)

            val refreshedItems = generateMockFeedItems()

            _state.update {
                it.copy(
                    isRefreshing = false,
                    feedItems = refreshedItems,
                )
            }
        }
    }

    private fun loadMore() {
        scope.launch {
            val currentItems = _state.value.feedItems
            val moreItems = generateMockFeedItems(startIndex = currentItems.size)

            _state.update {
                it.copy(feedItems = currentItems + moreItems)
            }
        }
    }

    private fun selectFilter(filter: FeedFilter) {
        _state.update { it.copy(selectedFilter = filter) }
        loadFeed()
    }

    private fun likePost(postId: String) {
        _state.update { state ->
            state.copy(
                feedItems = state.feedItems.map { item ->
                    if (item is FeedItem.MediaPost && item.id == postId) {
                        item.copy(isLiked = true, likesCount = item.likesCount + 1)
                    } else {
                        item
                    }
                },
            )
        }
    }

    private fun unlikePost(postId: String) {
        _state.update { state ->
            state.copy(
                feedItems = state.feedItems.map { item ->
                    if (item is FeedItem.MediaPost && item.id == postId) {
                        item.copy(isLiked = false, likesCount = item.likesCount - 1)
                    } else {
                        item
                    }
                },
            )
        }
    }

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }

    private fun generateMockFeedItems(startIndex: Int = 0): List<FeedItem> {
        val moods = PartyMood.entries.toTypedArray()
        val partyNames = listOf(
            "Summer Beach Bash", "Rooftop Vibes", "Neon Nights",
            "House Party", "Club Night", "Pool Party",
            "Birthday Celebration", "New Year's Eve", "Halloween Horror",
        )
        val hostNames = listOf("Alex M.", "Jordan K.", "Taylor S.", "Morgan B.", "Casey L.")
        val venues = listOf(
            "Skybar Rooftop",
            "Club Paradiso",
            "Beach House",
            "The Warehouse",
            "Downtown Loft",
            "Garden Terrace",
        )

        return (startIndex until startIndex + 10).map { index ->
            if (index % 3 == 0) {
                FeedItem.PartyCard(
                    id = "party_$index",
                    timestamp = Clock.System.now().toEpochMilliseconds() - (index * 3600000L),
                    title = partyNames[index % partyNames.size],
                    hostName = hostNames[index % hostNames.size],
                    hostAvatarUrl = null,
                    coverImageUrl = null,
                    venueName = venues[index % venues.size],
                    attendeesCount = (20..200).random(),
                    isLive = index % 5 == 0,
                    mood = moods[index % moods.size],
                    tags = listOf("music", "dancing", "fun"),
                )
            } else {
                FeedItem.MediaPost(
                    id = "media_$index",
                    timestamp = Clock.System.now().toEpochMilliseconds() - (index * 1800000L),
                    partyId = "party_${index / 3}",
                    partyTitle = partyNames[(index / 3) % partyNames.size],
                    userName = hostNames[index % hostNames.size],
                    userAvatarUrl = null,
                    mediaUrl = "",
                    mediaType = com.partygallery.presentation.state.MediaType.PHOTO,
                    likesCount = (10..500).random(),
                    commentsCount = (0..50).random(),
                    isLiked = index % 4 == 0,
                    mood = moods[index % moods.size],
                    caption = if (index % 2 == 0) "Amazing night! 🎉" else null,
                )
            }
        }
    }
}
