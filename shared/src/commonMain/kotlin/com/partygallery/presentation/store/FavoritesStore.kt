package com.partygallery.presentation.store

import com.partygallery.domain.model.PartyMood
import com.partygallery.presentation.intent.FavoritesIntent
import com.partygallery.presentation.state.FavoritesState
import com.partygallery.presentation.state.FavoritesTab
import com.partygallery.presentation.state.MediaType
import com.partygallery.presentation.state.SavedMedia
import com.partygallery.presentation.state.SavedParty
import com.partygallery.presentation.state.SuggestedParty
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
 * Favorites screen MVI store.
 *
 * S3-NEW-001: FavoritesStore MVI
 *
 * Manages saved parties, media, and personalized suggestions.
 */
class FavoritesStore(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    init {
        processIntent(FavoritesIntent.LoadFavorites)
    }

    fun processIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.LoadFavorites -> loadFavorites()
            is FavoritesIntent.RefreshFavorites -> refreshFavorites()
            is FavoritesIntent.LoadMore -> loadMore()
            is FavoritesIntent.SelectTab -> selectTab(intent.tab)
            is FavoritesIntent.RemoveFromFavorites -> removeFromFavorites(intent.itemId)
            is FavoritesIntent.OpenParty -> { /* Navigation handled by UI */ }
            is FavoritesIntent.OpenMedia -> { /* Navigation handled by UI */ }
            is FavoritesIntent.OpenProfile -> { /* Navigation handled by UI */ }
            is FavoritesIntent.DismissError -> dismissError()
        }
    }

    private fun loadFavorites() {
        scope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            delay(600)

            val savedParties = generateMockSavedParties()
            val savedMedia = generateMockSavedMedia()
            val suggestions = generateMockSuggestions()

            _state.update {
                it.copy(
                    isLoading = false,
                    savedParties = savedParties,
                    savedMedia = savedMedia,
                    suggestedParties = suggestions,
                )
            }
        }
    }

    private fun refreshFavorites() {
        scope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }

            delay(800)

            val savedParties = generateMockSavedParties()
            val savedMedia = generateMockSavedMedia()
            val suggestions = generateMockSuggestions()

            _state.update {
                it.copy(
                    isRefreshing = false,
                    savedParties = savedParties,
                    savedMedia = savedMedia,
                    suggestedParties = suggestions,
                )
            }
        }
    }

    private fun loadMore() {
        scope.launch {
            val currentParties = _state.value.savedParties
            val moreParties = generateMockSavedParties(startIndex = currentParties.size)

            _state.update {
                it.copy(savedParties = currentParties + moreParties)
            }
        }
    }

    private fun selectTab(tab: FavoritesTab) {
        _state.update { it.copy(selectedTab = tab) }
    }

    private fun removeFromFavorites(itemId: String) {
        _state.update { state ->
            state.copy(
                savedParties = state.savedParties.filter { it.id != itemId },
                savedMedia = state.savedMedia.filter { it.id != itemId },
            )
        }
    }

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }

    private fun generateMockSavedParties(startIndex: Int = 0): List<SavedParty> {
        val moods = PartyMood.entries.toTypedArray()
        val partyNames = listOf(
            "Beach Sunset Vibes", "Rooftop Chill", "Underground Beats",
            "Yacht Party", "Garden Soiree", "Loft Sessions",
        )
        val hostNames = listOf("Alex M.", "Jordan K.", "Taylor S.", "Morgan B.")
        val venues = listOf(
            "Malibu Beach Club", "Sky Lounge", "The Warehouse",
            "Harbor Marina", "Botanical Garden", "Art District Loft",
        )

        return (startIndex until startIndex + 6).map { index ->
            SavedParty(
                id = "saved_party_$index",
                title = partyNames[index % partyNames.size],
                hostName = hostNames[index % hostNames.size],
                hostAvatarUrl = null,
                coverImageUrl = null,
                venueName = venues[index % venues.size],
                dateTime = Clock.System.now().toEpochMilliseconds() + (index * 86400000L),
                mood = moods[index % moods.size],
                savedAt = Clock.System.now().toEpochMilliseconds() - (index * 3600000L),
            )
        }
    }

    private fun generateMockSavedMedia(): List<SavedMedia> {
        val moods = PartyMood.entries.toTypedArray()
        val partyTitles = listOf("Summer Bash", "Neon Nights", "Rooftop Party")
        val userNames = listOf("Alex M.", "Jordan K.", "Taylor S.")

        return (0 until 8).map { index ->
            SavedMedia(
                id = "saved_media_$index",
                partyId = "party_${index / 3}",
                partyTitle = partyTitles[index % partyTitles.size],
                mediaUrl = "",
                mediaType = if (index % 3 == 0) MediaType.VIDEO else MediaType.PHOTO,
                userName = userNames[index % userNames.size],
                userAvatarUrl = null,
                mood = moods[index % moods.size],
                savedAt = Clock.System.now().toEpochMilliseconds() - (index * 1800000L),
            )
        }
    }

    private fun generateMockSuggestions(): List<SuggestedParty> {
        val moods = PartyMood.entries.toTypedArray()
        val partyNames = listOf(
            "Techno Thursday", "Jazz & Wine", "Hip Hop Brunch",
            "Sunset Sessions", "Art Gallery Opening",
        )
        val hostNames = listOf("DJ Max", "The Wine Club", "Collective Arts", "Beach House Events")
        val venues = listOf(
            "Club Electric", "The Wine Bar", "Urban Gallery",
            "Beachfront Terrace", "Downtown Art Space",
        )
        val matchReasons = listOf(
            listOf("Based on your music taste", "Popular in your area"),
            listOf("Friends are going", "Similar to parties you've attended"),
            listOf("Matches your interests", "Trending now"),
            listOf("Recommended for you", "Top rated"),
        )

        return (0 until 5).map { index ->
            SuggestedParty(
                id = "suggested_$index",
                title = partyNames[index],
                hostName = hostNames[index % hostNames.size],
                hostAvatarUrl = null,
                coverImageUrl = null,
                venueName = venues[index],
                dateTime = Clock.System.now().toEpochMilliseconds() + (index * 172800000L),
                attendeesCount = (30..150).random(),
                isLive = index == 0,
                mood = moods[index % moods.size],
                matchScore = (0.75f..0.98f).random(),
                matchReasons = matchReasons[index % matchReasons.size],
            )
        }
    }

    private fun ClosedFloatingPointRange<Float>.random(): Float {
        return start + (kotlin.random.Random.nextFloat() * (endInclusive - start))
    }
}
