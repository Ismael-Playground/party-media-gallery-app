package com.partygallery.presentation.state

import com.partygallery.domain.model.PartyMood

/**
 * Favorites screen state.
 *
 * S3-NEW-001: FavoritesState con saved content data
 */
data class FavoritesState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val selectedTab: FavoritesTab = FavoritesTab.All,
    val savedParties: List<SavedParty> = emptyList(),
    val savedMedia: List<SavedMedia> = emptyList(),
    val suggestedParties: List<SuggestedParty> = emptyList(),
)

/**
 * Favorites tab options.
 */
enum class FavoritesTab(val label: String) {
    All("All"),
    Parties("Parties"),
    Media("Media"),
    Suggestions("For You"),
}

/**
 * Saved party item.
 */
data class SavedParty(
    val id: String,
    val title: String,
    val hostName: String,
    val hostAvatarUrl: String?,
    val coverImageUrl: String?,
    val venueName: String,
    val dateTime: Long,
    val mood: PartyMood?,
    val savedAt: Long,
)

/**
 * Saved media item.
 */
data class SavedMedia(
    val id: String,
    val partyId: String,
    val partyTitle: String,
    val mediaUrl: String,
    val mediaType: MediaType,
    val userName: String,
    val userAvatarUrl: String?,
    val mood: PartyMood?,
    val savedAt: Long,
)

/**
 * Suggested party based on user preferences.
 */
data class SuggestedParty(
    val id: String,
    val title: String,
    val hostName: String,
    val hostAvatarUrl: String?,
    val coverImageUrl: String?,
    val venueName: String,
    val dateTime: Long,
    val attendeesCount: Int,
    val isLive: Boolean,
    val mood: PartyMood?,
    val matchScore: Float,
    val matchReasons: List<String>,
)
