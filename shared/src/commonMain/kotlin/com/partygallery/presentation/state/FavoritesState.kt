package com.partygallery.presentation.state

import com.partygallery.domain.model.PartyMood

/**
 * Favorites screen state.
 *
 * S3-NEW-001: FavoritesState for MVI pattern
 *
 * Represents the complete UI state of the favorites screen.
 * Favorites shows: saved parties, liked media posts, and suggested events.
 */
data class FavoritesState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val selectedTab: FavoritesTab = FavoritesTab.Parties,
    val savedParties: List<FavoriteParty> = emptyList(),
    val likedMedia: List<FavoriteMedia> = emptyList(),
    val suggestedParties: List<SuggestedParty> = emptyList(),
)

/**
 * Favorites tab options.
 */
enum class FavoritesTab(val label: String) {
    Parties("Parties"),
    Media("Media"),
    Suggested("For You"),
}

/**
 * Saved party item in favorites.
 */
data class FavoriteParty(
    val id: String,
    val title: String,
    val hostName: String,
    val hostAvatarUrl: String?,
    val coverImageUrl: String?,
    val venueName: String,
    val attendeesCount: Int,
    val isLive: Boolean,
    val mood: PartyMood?,
    val savedAt: Long,
)

/**
 * Liked media item in favorites.
 */
data class FavoriteMedia(
    val id: String,
    val partyId: String,
    val partyTitle: String,
    val mediaUrl: String,
    val mediaType: MediaType,
    val userName: String,
    val userAvatarUrl: String?,
    val likesCount: Int,
    val mood: PartyMood?,
    val likedAt: Long,
)

/**
 * Suggested party for the user.
 */
data class SuggestedParty(
    val id: String,
    val title: String,
    val hostName: String,
    val coverImageUrl: String?,
    val venueName: String,
    val attendeesCount: Int,
    val isLive: Boolean,
    val mood: PartyMood?,
    val matchScore: Int,
    val matchReason: String,
)

/**
 * One-time events triggered by favorites operations.
 */
sealed class FavoritesEvent {
    data object NavigateToParty : FavoritesEvent()
    data class ShowError(val message: String) : FavoritesEvent()
    data class ShowSuccess(val message: String) : FavoritesEvent()
}
