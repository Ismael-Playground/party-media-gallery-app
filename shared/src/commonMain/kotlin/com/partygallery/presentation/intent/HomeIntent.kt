package com.partygallery.presentation.intent

import com.partygallery.presentation.state.FeedFilter

/**
 * Home screen intents.
 *
 * S3-007: HomeIntent sealed class
 */
sealed class HomeIntent {
    data object LoadFeed : HomeIntent()
    data object RefreshFeed : HomeIntent()
    data object LoadMore : HomeIntent()
    data class SelectFilter(val filter: FeedFilter) : HomeIntent()
    data class LikePost(val postId: String) : HomeIntent()
    data class UnlikePost(val postId: String) : HomeIntent()
    data class OpenParty(val partyId: String) : HomeIntent()
    data class OpenProfile(val userId: String) : HomeIntent()
    data object DismissError : HomeIntent()
}
