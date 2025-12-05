package com.partygallery.presentation.store

import com.partygallery.domain.model.PartyMood
import com.partygallery.presentation.intent.StudioIntent
import com.partygallery.presentation.state.ContentDraft
import com.partygallery.presentation.state.MediaType
import com.partygallery.presentation.state.MyContent
import com.partygallery.presentation.state.MyParty
import com.partygallery.presentation.state.PartyStatus
import com.partygallery.presentation.state.StudioState
import com.partygallery.presentation.state.StudioStats
import com.partygallery.presentation.state.StudioTab
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
 * Studio screen MVI store.
 *
 * S3-NEW-003: StudioStore MVI
 *
 * Manages content creation, drafts, and party management.
 */
class StudioStore(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(StudioState())
    val state: StateFlow<StudioState> = _state.asStateFlow()

    init {
        processIntent(StudioIntent.LoadStudio)
    }

    fun processIntent(intent: StudioIntent) {
        when (intent) {
            is StudioIntent.LoadStudio -> loadStudio()
            is StudioIntent.RefreshStudio -> refreshStudio()
            is StudioIntent.SelectTab -> selectTab(intent.tab)
            is StudioIntent.OpenCamera -> { /* Handled by UI */ }
            is StudioIntent.OpenGallery -> { /* Handled by UI */ }
            is StudioIntent.CreateParty -> { /* Handled by UI */ }
            is StudioIntent.StartLiveStream -> { /* Handled by UI */ }
            is StudioIntent.DeleteDraft -> deleteDraft(intent.draftId)
            is StudioIntent.EditDraft -> { /* Handled by UI */ }
            is StudioIntent.PublishDraft -> publishDraft(intent.draftId)
            is StudioIntent.OpenContent -> { /* Handled by UI */ }
            is StudioIntent.DeleteContent -> deleteContent(intent.contentId)
            is StudioIntent.OpenParty -> { /* Handled by UI */ }
            is StudioIntent.EditParty -> { /* Handled by UI */ }
            is StudioIntent.DismissError -> dismissError()
        }
    }

    private fun loadStudio() {
        scope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            delay(500)

            val drafts = generateMockDrafts()
            val content = generateMockContent()
            val parties = generateMockParties()
            val stats = generateMockStats(content, parties)

            _state.update {
                it.copy(
                    isLoading = false,
                    drafts = drafts,
                    myContent = content,
                    myParties = parties,
                    stats = stats,
                )
            }
        }
    }

    private fun refreshStudio() {
        scope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }

            delay(600)

            val drafts = generateMockDrafts()
            val content = generateMockContent()
            val parties = generateMockParties()
            val stats = generateMockStats(content, parties)

            _state.update {
                it.copy(
                    isRefreshing = false,
                    drafts = drafts,
                    myContent = content,
                    myParties = parties,
                    stats = stats,
                )
            }
        }
    }

    private fun selectTab(tab: StudioTab) {
        _state.update { it.copy(selectedTab = tab) }
    }

    private fun deleteDraft(draftId: String) {
        _state.update { state ->
            state.copy(drafts = state.drafts.filter { it.id != draftId })
        }
    }

    private fun publishDraft(draftId: String) {
        scope.launch {
            val draft = _state.value.drafts.find { it.id == draftId } ?: return@launch

            // Remove from drafts
            _state.update { state ->
                state.copy(drafts = state.drafts.filter { it.id != draftId })
            }

            // Add to content
            val newContent = MyContent(
                id = "content_${Clock.System.now().toEpochMilliseconds()}",
                thumbnailUrl = draft.thumbnailUrl,
                mediaType = draft.mediaType,
                partyId = draft.partyId ?: "",
                partyTitle = draft.partyTitle ?: "My Party",
                likesCount = 0,
                commentsCount = 0,
                viewsCount = 0,
                mood = draft.mood,
                publishedAt = Clock.System.now().toEpochMilliseconds(),
            )

            _state.update { state ->
                state.copy(myContent = listOf(newContent) + state.myContent)
            }
        }
    }

    private fun deleteContent(contentId: String) {
        _state.update { state ->
            state.copy(myContent = state.myContent.filter { it.id != contentId })
        }
    }

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }

    private fun generateMockDrafts(): List<ContentDraft> {
        val moods = PartyMood.entries.toTypedArray()

        return (0 until 3).map { index ->
            ContentDraft(
                id = "draft_$index",
                thumbnailUrl = null,
                mediaType = if (index % 2 == 0) MediaType.PHOTO else MediaType.VIDEO,
                partyId = "party_$index",
                partyTitle = listOf("Beach Bash", "Rooftop Party", "Club Night")[index],
                caption = if (index == 0) "Check out this amazing view!" else null,
                mood = moods[index % moods.size],
                createdAt = Clock.System.now().toEpochMilliseconds() - (index * 3600000L),
                lastEditedAt = Clock.System.now().toEpochMilliseconds() - (index * 1800000L),
            )
        }
    }

    private fun generateMockContent(): List<MyContent> {
        val moods = PartyMood.entries.toTypedArray()
        val partyTitles = listOf(
            "Summer Sunset", "Neon Nights", "Yacht Party",
            "Garden Soiree", "Underground Beats",
        )

        return (0 until 8).map { index ->
            MyContent(
                id = "content_$index",
                thumbnailUrl = null,
                mediaType = if (index % 3 == 0) MediaType.VIDEO else MediaType.PHOTO,
                partyId = "party_$index",
                partyTitle = partyTitles[index % partyTitles.size],
                likesCount = (10..500).random(),
                commentsCount = (0..50).random(),
                viewsCount = (100..5000).random(),
                mood = moods[index % moods.size],
                publishedAt = Clock.System.now().toEpochMilliseconds() - (index * 86400000L),
            )
        }
    }

    private fun generateMockParties(): List<MyParty> {
        val moods = PartyMood.entries.toTypedArray()
        val partyTitles = listOf(
            "My Birthday Bash", "Rooftop Chill", "Summer BBQ",
            "New Year's Eve", "Halloween Party",
        )
        val venues = listOf(
            "My Place", "Sky Lounge", "Beach House",
            "Downtown Club", "The Warehouse",
        )
        val statuses = listOf(PartyStatus.ENDED, PartyStatus.PLANNED, PartyStatus.DRAFT, PartyStatus.LIVE)

        return (0 until 5).map { index ->
            MyParty(
                id = "my_party_$index",
                title = partyTitles[index],
                coverImageUrl = null,
                venueName = venues[index],
                dateTime = Clock.System.now().toEpochMilliseconds() +
                    ((index - 2) * 604800000L), // Some past, some future
                attendeesCount = (15..100).random(),
                mediaCount = (5..50).random(),
                status = statuses[index % statuses.size],
                mood = moods[index % moods.size],
            )
        }
    }

    private fun generateMockStats(content: List<MyContent>, parties: List<MyParty>): StudioStats {
        return StudioStats(
            totalViews = content.sumOf { it.viewsCount },
            totalLikes = content.sumOf { it.likesCount },
            totalContent = content.size,
            totalParties = parties.size,
        )
    }
}
