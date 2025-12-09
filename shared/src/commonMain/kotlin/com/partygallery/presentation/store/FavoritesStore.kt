package com.partygallery.presentation.store

import com.partygallery.domain.model.PartyMood
import com.partygallery.presentation.intent.FavoritesIntent
import com.partygallery.presentation.state.FavoriteMedia
import com.partygallery.presentation.state.FavoriteParty
import com.partygallery.presentation.state.FavoritesEvent
import com.partygallery.presentation.state.FavoritesState
import com.partygallery.presentation.state.FavoritesTab
import com.partygallery.presentation.state.MediaType
import com.partygallery.presentation.state.SuggestedParty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * Favorites Store - MVI State Management
 *
 * S3-NEW-001: FavoritesStore with MVI pattern
 *
 * Manages favorites screen state following unidirectional data flow:
 * Intent -> Store -> State -> UI -> Intent
 *
 * Note: Uses Dispatchers.Default for Desktop compatibility
 * (Dispatchers.Main requires platform-specific dispatcher)
 */
class FavoritesStore(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    private val _events = Channel<FavoritesEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        processIntent(FavoritesIntent.LoadFavorites)
    }

    /**
     * Process user intents and update state accordingly.
     */
    fun processIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.LoadFavorites -> loadFavorites()
            is FavoritesIntent.RefreshFavorites -> refreshFavorites()
            is FavoritesIntent.SelectTab -> selectTab(intent.tab)
            is FavoritesIntent.UnsaveParty -> unsaveParty(intent.partyId)
            is FavoritesIntent.OpenParty -> { /* Navigation handled by UI */ }
            is FavoritesIntent.UnlikeMedia -> unlikeMedia(intent.mediaId)
            is FavoritesIntent.OpenMedia -> { /* Navigation handled by UI */ }
            is FavoritesIntent.SaveSuggestedParty -> saveSuggestedParty(intent.partyId)
            is FavoritesIntent.DismissSuggestion -> dismissSuggestion(intent.partyId)
            is FavoritesIntent.DismissError -> dismissError()
        }
    }

    // ============================================
    // Loading Handlers
    // ============================================

    private fun loadFavorites() {
        scope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Simulate network delay
            delay(600)

            // Load mock data
            val savedParties = generateMockSavedParties()
            val likedMedia = generateMockLikedMedia()
            val suggestedParties = generateMockSuggestedParties()

            _state.update {
                it.copy(
                    isLoading = false,
                    savedParties = savedParties,
                    likedMedia = likedMedia,
                    suggestedParties = suggestedParties,
                )
            }
        }
    }

    private fun refreshFavorites() {
        scope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }

            delay(800)

            val savedParties = generateMockSavedParties()
            val likedMedia = generateMockLikedMedia()
            val suggestedParties = generateMockSuggestedParties()

            _state.update {
                it.copy(
                    isRefreshing = false,
                    savedParties = savedParties,
                    likedMedia = likedMedia,
                    suggestedParties = suggestedParties,
                )
            }
        }
    }

    // ============================================
    // Tab Navigation
    // ============================================

    private fun selectTab(tab: FavoritesTab) {
        _state.update { it.copy(selectedTab = tab) }
    }

    // ============================================
    // Party Actions
    // ============================================

    private fun unsaveParty(partyId: String) {
        _state.update { state ->
            state.copy(
                savedParties = state.savedParties.filter { it.id != partyId },
            )
        }
        scope.launch {
            _events.send(FavoritesEvent.ShowSuccess("Party removed from favorites"))
        }
    }

    // ============================================
    // Media Actions
    // ============================================

    private fun unlikeMedia(mediaId: String) {
        _state.update { state ->
            state.copy(
                likedMedia = state.likedMedia.filter { it.id != mediaId },
            )
        }
        scope.launch {
            _events.send(FavoritesEvent.ShowSuccess("Media removed from favorites"))
        }
    }

    // ============================================
    // Suggested Party Actions
    // ============================================

    private fun saveSuggestedParty(partyId: String) {
        val suggestedParty = _state.value.suggestedParties.find { it.id == partyId }

        if (suggestedParty != null) {
            val newFavorite = FavoriteParty(
                id = suggestedParty.id,
                title = suggestedParty.title,
                hostName = suggestedParty.hostName,
                hostAvatarUrl = null,
                coverImageUrl = suggestedParty.coverImageUrl,
                venueName = suggestedParty.venueName,
                attendeesCount = suggestedParty.attendeesCount,
                isLive = suggestedParty.isLive,
                mood = suggestedParty.mood,
                savedAt = Clock.System.now().toEpochMilliseconds(),
            )

            _state.update { state ->
                state.copy(
                    savedParties = listOf(newFavorite) + state.savedParties,
                    suggestedParties = state.suggestedParties.filter { it.id != partyId },
                )
            }

            scope.launch {
                _events.send(FavoritesEvent.ShowSuccess("Party saved to favorites"))
            }
        }
    }

    private fun dismissSuggestion(partyId: String) {
        _state.update { state ->
            state.copy(
                suggestedParties = state.suggestedParties.filter { it.id != partyId },
            )
        }
    }

    // ============================================
    // State Management
    // ============================================

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }

    // ============================================
    // Mock Data Generation
    // ============================================

    private fun generateMockSavedParties(): List<FavoriteParty> {
        val moods = PartyMood.entries.toTypedArray()
        val partyNames = listOf(
            "Rooftop Sunset",
            "Neon Dreams",
            "Beach Vibes",
            "House Party Deluxe",
            "Club Paradiso Night",
        )
        val hostNames = listOf("Alex M.", "Jordan K.", "Taylor S.", "Morgan B.")
        val venues = listOf(
            "Skybar Rooftop",
            "Club Paradiso",
            "Malibu Beach House",
            "The Warehouse",
        )

        return partyNames.mapIndexed { index, name ->
            FavoriteParty(
                id = "saved_party_$index",
                title = name,
                hostName = hostNames[index % hostNames.size],
                hostAvatarUrl = null,
                coverImageUrl = null,
                venueName = venues[index % venues.size],
                attendeesCount = (30..150).random(),
                isLive = index == 0,
                mood = moods[index % moods.size],
                savedAt = Clock.System.now().toEpochMilliseconds() - (index * 86400000L),
            )
        }
    }

    private fun generateMockLikedMedia(): List<FavoriteMedia> {
        val moods = PartyMood.entries.toTypedArray()
        val userNames = listOf("Sarah J.", "Mike T.", "Emily R.", "Chris P.", "Anna K.")
        val partyTitles = listOf(
            "Summer Beach Bash",
            "Rooftop Vibes",
            "Neon Nights",
            "Pool Party",
        )

        return (0 until 8).map { index ->
            FavoriteMedia(
                id = "liked_media_$index",
                partyId = "party_${index % 4}",
                partyTitle = partyTitles[index % partyTitles.size],
                mediaUrl = "",
                mediaType = if (index % 3 == 0) MediaType.VIDEO else MediaType.PHOTO,
                userName = userNames[index % userNames.size],
                userAvatarUrl = null,
                likesCount = (50..500).random(),
                mood = moods[index % moods.size],
                likedAt = Clock.System.now().toEpochMilliseconds() - (index * 3600000L),
            )
        }
    }

    private fun generateMockSuggestedParties(): List<SuggestedParty> {
        val moods = PartyMood.entries.toTypedArray()
        val partyNames = listOf(
            "Underground Techno",
            "Reggaeton Nights",
            "Jazz & Wine",
            "Indie Rock Fest",
            "Hip Hop Showcase",
        )
        val hostNames = listOf("DJ Nova", "Carlos M.", "Sophie L.", "Marcus B.", "Luna K.")
        val venues = listOf(
            "The Underground",
            "Latin Club",
            "Jazz Lounge",
            "Indie Warehouse",
            "Hip Hop Arena",
        )
        val matchReasons = listOf(
            "Based on your music taste",
            "Friends are going",
            "Popular in your area",
            "Similar to parties you've attended",
            "Trending this weekend",
        )

        return partyNames.mapIndexed { index, name ->
            SuggestedParty(
                id = "suggested_party_$index",
                title = name,
                hostName = hostNames[index],
                coverImageUrl = null,
                venueName = venues[index],
                attendeesCount = (20..100).random(),
                isLive = index == 1,
                mood = moods[index % moods.size],
                matchScore = (70..95).random(),
                matchReason = matchReasons[index],
            )
        }
    }
}
