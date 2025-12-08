package com.partygallery.presentation.intent

import com.partygallery.presentation.state.FavoritesTab

/**
 * Favorites Screen Intents
 *
 * S3-NEW-001: FavoritesIntent sealed class for MVI pattern
 *
 * Represents all user actions that can occur on the favorites screen.
 * Each intent triggers a state change in the FavoritesStore.
 */
sealed class FavoritesIntent {

    // ============================================
    // Loading Actions
    // ============================================

    data object LoadFavorites : FavoritesIntent()
    data object RefreshFavorites : FavoritesIntent()

    // ============================================
    // Tab Navigation
    // ============================================

    data class SelectTab(val tab: FavoritesTab) : FavoritesIntent()

    // ============================================
    // Party Actions
    // ============================================

    data class UnsaveParty(val partyId: String) : FavoritesIntent()
    data class OpenParty(val partyId: String) : FavoritesIntent()

    // ============================================
    // Media Actions
    // ============================================

    data class UnlikeMedia(val mediaId: String) : FavoritesIntent()
    data class OpenMedia(val mediaId: String) : FavoritesIntent()

    // ============================================
    // Suggested Party Actions
    // ============================================

    data class SaveSuggestedParty(val partyId: String) : FavoritesIntent()
    data class DismissSuggestion(val partyId: String) : FavoritesIntent()

    // ============================================
    // State Management
    // ============================================

    data object DismissError : FavoritesIntent()
}
