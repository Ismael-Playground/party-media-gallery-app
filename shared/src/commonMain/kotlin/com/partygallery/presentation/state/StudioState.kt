package com.partygallery.presentation.state

import com.partygallery.domain.model.PartyMood

/**
 * Studio screen state.
 *
 * S3-NEW-003: StudioState for MVI pattern
 *
 * Represents the complete UI state of the studio screen.
 * Studio is for creating and managing party content - photos, videos, drafts.
 */
data class StudioState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val selectedTab: StudioTab = StudioTab.MyContent,
    val myContent: List<StudioContent> = emptyList(),
    val drafts: List<StudioDraft> = emptyList(),
    val recentParties: List<RecentParty> = emptyList(),
    val isCreatingContent: Boolean = false,
)

/**
 * Studio tab options.
 */
enum class StudioTab(val label: String) {
    MyContent("My Content"),
    Drafts("Drafts"),
    Parties("Parties"),
}

/**
 * User's uploaded content item.
 */
data class StudioContent(
    val id: String,
    val partyId: String,
    val partyTitle: String,
    val mediaUrl: String,
    val thumbnailUrl: String?,
    val mediaType: MediaType,
    val caption: String?,
    val likesCount: Int,
    val commentsCount: Int,
    val viewsCount: Int,
    val mood: PartyMood?,
    val createdAt: Long,
    val isPublished: Boolean,
)

/**
 * Draft content not yet published.
 */
data class StudioDraft(
    val id: String,
    val partyId: String?,
    val partyTitle: String?,
    val mediaUrl: String,
    val thumbnailUrl: String?,
    val mediaType: MediaType,
    val caption: String?,
    val mood: PartyMood?,
    val createdAt: Long,
    val lastEditedAt: Long,
)

/**
 * Recent party for quick content upload.
 */
data class RecentParty(
    val id: String,
    val title: String,
    val hostName: String,
    val coverImageUrl: String?,
    val venueName: String,
    val isLive: Boolean,
    val mood: PartyMood?,
    val myContentCount: Int,
    val lastActivityAt: Long,
)

/**
 * One-time events triggered by studio operations.
 */
sealed class StudioEvent {
    data object NavigateToCreateContent : StudioEvent()
    data object NavigateToEditContent : StudioEvent()
    data class ShowError(val message: String) : StudioEvent()
    data class ShowSuccess(val message: String) : StudioEvent()
    data class ContentPublished(val contentId: String) : StudioEvent()
    data class ContentDeleted(val contentId: String) : StudioEvent()
}
