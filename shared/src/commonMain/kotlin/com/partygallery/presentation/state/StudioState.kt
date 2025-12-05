package com.partygallery.presentation.state

import com.partygallery.domain.model.PartyMood

/**
 * Studio screen state.
 *
 * S3-NEW-003: StudioState for content creation and management
 */
data class StudioState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val selectedTab: StudioTab = StudioTab.Create,
    val drafts: List<ContentDraft> = emptyList(),
    val myContent: List<MyContent> = emptyList(),
    val myParties: List<MyParty> = emptyList(),
    val stats: StudioStats = StudioStats(),
)

/**
 * Studio tab options.
 */
enum class StudioTab(val label: String) {
    Create("Create"),
    Drafts("Drafts"),
    Content("My Content"),
    Parties("My Parties"),
}

/**
 * Content draft item.
 */
data class ContentDraft(
    val id: String,
    val thumbnailUrl: String?,
    val mediaType: MediaType,
    val partyId: String?,
    val partyTitle: String?,
    val caption: String?,
    val mood: PartyMood?,
    val createdAt: Long,
    val lastEditedAt: Long,
)

/**
 * User's published content.
 */
data class MyContent(
    val id: String,
    val thumbnailUrl: String?,
    val mediaType: MediaType,
    val partyId: String,
    val partyTitle: String,
    val likesCount: Int,
    val commentsCount: Int,
    val viewsCount: Int,
    val mood: PartyMood?,
    val publishedAt: Long,
)

/**
 * User's created/hosted parties.
 */
data class MyParty(
    val id: String,
    val title: String,
    val coverImageUrl: String?,
    val venueName: String,
    val dateTime: Long,
    val attendeesCount: Int,
    val mediaCount: Int,
    val status: PartyStatus,
    val mood: PartyMood?,
)

/**
 * Party status.
 */
enum class PartyStatus {
    DRAFT,
    PLANNED,
    LIVE,
    ENDED,
    CANCELLED,
}

/**
 * Studio statistics.
 */
data class StudioStats(
    val totalViews: Int = 0,
    val totalLikes: Int = 0,
    val totalContent: Int = 0,
    val totalParties: Int = 0,
)
