package com.partygallery.presentation.state

import com.partygallery.domain.model.MediaContent
import com.partygallery.domain.model.PartyEvent
import com.partygallery.domain.model.PartyMood

/**
 * Party detail screen state.
 *
 * S4-001: PartyDetailState
 */
data class PartyDetailState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val party: PartyEvent? = null,
    val mediaItems: List<MediaContent> = emptyList(),
    val isLoadingMedia: Boolean = false,
    val selectedMediaFilter: MediaFilterOption = MediaFilterOption.All,
    val isUserAttending: Boolean = false,
    val isRsvpLoading: Boolean = false,
)

/**
 * Media filter options for party detail gallery.
 */
enum class MediaFilterOption(val label: String) {
    All("All"),
    Photos("Photos"),
    Videos("Videos"),
    Hype("Hype"),
    Chill("Chill"),
    Wild("Wild"),
}

/**
 * Map MediaFilterOption to PartyMood for filtering.
 */
fun MediaFilterOption.toMood(): PartyMood? = when (this) {
    MediaFilterOption.All -> null
    MediaFilterOption.Photos -> null
    MediaFilterOption.Videos -> null
    MediaFilterOption.Hype -> PartyMood.HYPE
    MediaFilterOption.Chill -> PartyMood.CHILL
    MediaFilterOption.Wild -> PartyMood.WILD
}
