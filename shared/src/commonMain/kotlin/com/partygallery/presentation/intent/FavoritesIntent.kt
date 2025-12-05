package com.partygallery.presentation.intent

import com.partygallery.presentation.state.FavoritesTab

/**
 * Favorites screen intents.
 *
 * S3-NEW-001: FavoritesIntent sealed class for MVI pattern
 */
sealed class FavoritesIntent {
    data object LoadFavorites : FavoritesIntent()
    data object RefreshFavorites : FavoritesIntent()
    data object LoadMore : FavoritesIntent()
    data class SelectTab(val tab: FavoritesTab) : FavoritesIntent()
    data class RemoveFromFavorites(val itemId: String) : FavoritesIntent()
    data class OpenParty(val partyId: String) : FavoritesIntent()
    data class OpenMedia(val mediaId: String) : FavoritesIntent()
    data class OpenProfile(val userId: String) : FavoritesIntent()
    data object DismissError : FavoritesIntent()
}
