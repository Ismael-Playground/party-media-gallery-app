package com.partygallery.presentation.store

import com.partygallery.domain.model.PartyMood
import com.partygallery.presentation.intent.StudioIntent
import com.partygallery.presentation.state.MediaType
import com.partygallery.presentation.state.RecentParty
import com.partygallery.presentation.state.StudioContent
import com.partygallery.presentation.state.StudioDraft
import com.partygallery.presentation.state.StudioEvent
import com.partygallery.presentation.state.StudioState
import com.partygallery.presentation.state.StudioTab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * Studio Store - MVI State Management
 *
 * S3-NEW-003: StudioStore with MVI pattern
 *
 * Manages studio screen state following unidirectional data flow:
 * Intent -> Store -> State -> UI -> Intent
 *
 * Note: Uses Dispatchers.Default for Desktop compatibility
 * (Dispatchers.Main requires platform-specific dispatcher)
 */
class StudioStore(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(StudioState())
    val state: StateFlow<StudioState> = _state.asStateFlow()

    private val _events = Channel<StudioEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        processIntent(StudioIntent.LoadStudioContent)
    }

    /**
     * Process user intents and update state accordingly.
     */
    fun processIntent(intent: StudioIntent) {
        when (intent) {
            is StudioIntent.LoadStudioContent -> loadStudioContent()
            is StudioIntent.RefreshStudioContent -> refreshStudioContent()
            is StudioIntent.SelectTab -> selectTab(intent.tab)
            is StudioIntent.DeleteContent -> deleteContent(intent.contentId)
            is StudioIntent.EditContent -> { /* Navigation handled by UI */ }
            is StudioIntent.ViewContentDetails -> { /* Navigation handled by UI */ }
            is StudioIntent.PublishDraft -> publishDraft(intent.draftId)
            is StudioIntent.EditDraft -> { /* Navigation handled by UI */ }
            is StudioIntent.DeleteDraft -> deleteDraft(intent.draftId)
            is StudioIntent.StartCreateContent -> startCreateContent()
            is StudioIntent.CreateContentForParty -> createContentForParty(intent.partyId)
            is StudioIntent.DismissError -> dismissError()
        }
    }

    // ============================================
    // Loading Handlers
    // ============================================

    private fun loadStudioContent() {
        scope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Simulate network delay
            delay(600)

            // Load mock data
            val myContent = generateMockContent()
            val drafts = generateMockDrafts()
            val recentParties = generateMockRecentParties()

            _state.update {
                it.copy(
                    isLoading = false,
                    myContent = myContent,
                    drafts = drafts,
                    recentParties = recentParties,
                )
            }
        }
    }

    private fun refreshStudioContent() {
        scope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }

            delay(800)

            val myContent = generateMockContent()
            val drafts = generateMockDrafts()
            val recentParties = generateMockRecentParties()

            _state.update {
                it.copy(
                    isRefreshing = false,
                    myContent = myContent,
                    drafts = drafts,
                    recentParties = recentParties,
                )
            }
        }
    }

    // ============================================
    // Tab Navigation
    // ============================================

    private fun selectTab(tab: StudioTab) {
        _state.update { it.copy(selectedTab = tab) }
    }

    // ============================================
    // Content Actions
    // ============================================

    private fun deleteContent(contentId: String) {
        _state.update { state ->
            state.copy(
                myContent = state.myContent.filter { it.id != contentId },
            )
        }
        scope.launch {
            _events.send(StudioEvent.ContentDeleted(contentId))
            _events.send(StudioEvent.ShowSuccess("Content deleted"))
        }
    }

    // ============================================
    // Draft Actions
    // ============================================

    private fun publishDraft(draftId: String) {
        val draft = _state.value.drafts.find { it.id == draftId }

        if (draft != null) {
            val newContent = StudioContent(
                id = "content_${Clock.System.now().toEpochMilliseconds()}",
                partyId = draft.partyId ?: "unknown",
                partyTitle = draft.partyTitle ?: "Unknown Party",
                mediaUrl = draft.mediaUrl,
                thumbnailUrl = draft.thumbnailUrl,
                mediaType = draft.mediaType,
                caption = draft.caption,
                likesCount = 0,
                commentsCount = 0,
                viewsCount = 0,
                mood = draft.mood,
                createdAt = Clock.System.now().toEpochMilliseconds(),
                isPublished = true,
            )

            _state.update { state ->
                state.copy(
                    myContent = listOf(newContent) + state.myContent,
                    drafts = state.drafts.filter { it.id != draftId },
                )
            }

            scope.launch {
                _events.send(StudioEvent.ContentPublished(newContent.id))
                _events.send(StudioEvent.ShowSuccess("Content published!"))
            }
        }
    }

    private fun deleteDraft(draftId: String) {
        _state.update { state ->
            state.copy(
                drafts = state.drafts.filter { it.id != draftId },
            )
        }
        scope.launch {
            _events.send(StudioEvent.ShowSuccess("Draft deleted"))
        }
    }

    // ============================================
    // Create Actions
    // ============================================

    private fun startCreateContent() {
        scope.launch {
            _events.send(StudioEvent.NavigateToCreateContent)
        }
    }

    private fun createContentForParty(partyId: String) {
        scope.launch {
            _events.send(StudioEvent.NavigateToCreateContent)
        }
    }

    // ============================================
    // State Management
    // ============================================

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }

    // ============================================
    // Mock Data Generation
    // ============================================

    private fun generateMockContent(): List<StudioContent> {
        val moods = PartyMood.entries.toTypedArray()
        val partyTitles = listOf(
            "Summer Beach Bash",
            "Rooftop Vibes",
            "Neon Nights",
            "Pool Party",
        )
        val captions = listOf(
            "What a night!",
            "Best party ever",
            "Living the moment",
            "Good vibes only",
            null,
        )

        return (0 until 6).map { index ->
            StudioContent(
                id = "content_$index",
                partyId = "party_${index % 4}",
                partyTitle = partyTitles[index % partyTitles.size],
                mediaUrl = "",
                thumbnailUrl = null,
                mediaType = if (index % 3 == 0) MediaType.VIDEO else MediaType.PHOTO,
                caption = captions[index % captions.size],
                likesCount = (50..500).random(),
                commentsCount = (5..50).random(),
                viewsCount = (100..1000).random(),
                mood = moods[index % moods.size],
                createdAt = Clock.System.now().toEpochMilliseconds() - (index * 86400000L),
                isPublished = true,
            )
        }
    }

    private fun generateMockDrafts(): List<StudioDraft> {
        val moods = PartyMood.entries.toTypedArray()
        val partyTitles = listOf("Upcoming Bash", "Secret Party", null)

        return (0 until 3).map { index ->
            StudioDraft(
                id = "draft_$index",
                partyId = if (index < 2) "party_$index" else null,
                partyTitle = partyTitles[index % partyTitles.size],
                mediaUrl = "",
                thumbnailUrl = null,
                mediaType = if (index == 0) MediaType.VIDEO else MediaType.PHOTO,
                caption = if (index == 0) "Work in progress..." else null,
                mood = moods[index % moods.size],
                createdAt = Clock.System.now().toEpochMilliseconds() - (index * 3600000L),
                lastEditedAt = Clock.System.now().toEpochMilliseconds() - (index * 1800000L),
            )
        }
    }

    private fun generateMockRecentParties(): List<RecentParty> {
        val moods = PartyMood.entries.toTypedArray()
        val partyNames = listOf(
            "Rooftop Sunset",
            "Neon Dreams",
            "Beach Vibes",
            "House Party Deluxe",
        )
        val hostNames = listOf("Alex M.", "Jordan K.", "Taylor S.", "Morgan B.")
        val venues = listOf(
            "Skybar Rooftop",
            "Club Paradiso",
            "Malibu Beach House",
            "The Warehouse",
        )

        return partyNames.mapIndexed { index, name ->
            RecentParty(
                id = "recent_party_$index",
                title = name,
                hostName = hostNames[index % hostNames.size],
                coverImageUrl = null,
                venueName = venues[index % venues.size],
                isLive = index == 0,
                mood = moods[index % moods.size],
                myContentCount = (0..5).random(),
                lastActivityAt = Clock.System.now().toEpochMilliseconds() - (index * 86400000L),
            )
        }
    }
}
