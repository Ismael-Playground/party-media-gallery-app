package com.partygallery.presentation.intent

import com.partygallery.presentation.state.MediaFilterOption

/**
 * Intents for Party Detail Screen.
 *
 * S4-002: PartyDetailIntent
 */
sealed class PartyDetailIntent {
    data class LoadParty(val partyId: String) : PartyDetailIntent()
    data object RefreshParty : PartyDetailIntent()
    data class SelectMediaFilter(val filter: MediaFilterOption) : PartyDetailIntent()
    data object LoadMoreMedia : PartyDetailIntent()
    data object ToggleRsvp : PartyDetailIntent()
    data class LikeMedia(val mediaId: String) : PartyDetailIntent()
    data class UnlikeMedia(val mediaId: String) : PartyDetailIntent()
    data object NavigateToUploadMedia : PartyDetailIntent()
    data object NavigateBack : PartyDetailIntent()
}
