package com.app.test.store

import app.cash.turbine.test
import com.partygallery.presentation.intent.StudioIntent
import com.partygallery.presentation.state.StudioEvent
import com.partygallery.presentation.state.StudioTab
import com.partygallery.presentation.store.StudioStore
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

/**
 * Unit tests for StudioStore.
 *
 * Tests cover all intents and state transitions for the studio screen.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StudioStoreTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var testScope: TestScope

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        testScope = TestScope(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createStore(): StudioStore {
        return StudioStore(scope = testScope)
    }

    // ===== Initial State Tests =====

    @Test
    fun initialStateHasCorrectDefaults() = runTest {
        val store = createStore()

        val state = store.state.value
        assertTrue(state.isLoading)
        assertFalse(state.isRefreshing)
        assertNull(state.error)
        assertEquals(StudioTab.MyContent, state.selectedTab)
        assertFalse(state.isCreatingContent)
    }

    @Test
    fun initAutomaticallyLoadsStudioContent() = runTest {
        val store = createStore()

        store.state.test {
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)

            testScope.advanceUntilIdle()

            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertTrue(loadedState.myContent.isNotEmpty())
            assertTrue(loadedState.drafts.isNotEmpty())
            assertTrue(loadedState.recentParties.isNotEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== LoadStudioContent Tests =====

    @Test
    fun loadStudioContentSetsLoadingState() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial state

            store.processIntent(StudioIntent.LoadStudioContent)
            val loadingState = store.state.value
            assertTrue(loadingState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadStudioContentPopulatesData() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            val loadedState = awaitItem()

            assertFalse(loadedState.isLoading)
            assertEquals(6, loadedState.myContent.size)
            assertEquals(3, loadedState.drafts.size)
            assertEquals(4, loadedState.recentParties.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadStudioContentClearsError() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(StudioIntent.LoadStudioContent)
            val state = store.state.value
            assertNull(state.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== RefreshStudioContent Tests =====

    @Test
    fun refreshStudioContentSetsRefreshingState() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(StudioIntent.RefreshStudioContent)
            val refreshingState = awaitItem()
            assertTrue(refreshingState.isRefreshing)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refreshStudioContentCompletesSuccessfully() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(StudioIntent.RefreshStudioContent)
            awaitItem() // Refreshing state

            testScope.advanceUntilIdle()
            val refreshedState = awaitItem()

            assertFalse(refreshedState.isRefreshing)
            assertTrue(refreshedState.myContent.isNotEmpty())
            assertTrue(refreshedState.drafts.isNotEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refreshStudioContentClearsError() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(StudioIntent.RefreshStudioContent)
            val state = store.state.value
            assertNull(state.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== SelectTab Tests =====

    @Test
    fun selectTabUpdatesSelectedTab() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        store.processIntent(StudioIntent.SelectTab(StudioTab.Drafts))
        assertEquals(StudioTab.Drafts, store.state.value.selectedTab)

        store.processIntent(StudioIntent.SelectTab(StudioTab.Parties))
        assertEquals(StudioTab.Parties, store.state.value.selectedTab)

        store.processIntent(StudioIntent.SelectTab(StudioTab.MyContent))
        assertEquals(StudioTab.MyContent, store.state.value.selectedTab)
    }

    @Test
    fun selectTabAllTabs() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        for (tab in StudioTab.entries) {
            store.processIntent(StudioIntent.SelectTab(tab))
            assertEquals(tab, store.state.value.selectedTab)
        }
    }

    // ===== DeleteContent Tests =====

    @Test
    fun deleteContentRemovesContentFromList() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val loadedState = store.state.value
        val initialCount = loadedState.myContent.size
        val contentToDelete = loadedState.myContent.first()

        store.processIntent(StudioIntent.DeleteContent(contentToDelete.id))
        testScope.advanceUntilIdle()

        val updatedState = store.state.value
        assertEquals(initialCount - 1, updatedState.myContent.size)
        assertFalse(updatedState.myContent.any { it.id == contentToDelete.id })
    }

    @Test
    fun deleteContentSendsDeletedEvent() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val contentToDelete = store.state.value.myContent.first()

        store.events.test {
            store.processIntent(StudioIntent.DeleteContent(contentToDelete.id))
            testScope.advanceUntilIdle()

            val deletedEvent = awaitItem()
            assertTrue(deletedEvent is StudioEvent.ContentDeleted)
            assertEquals(contentToDelete.id, (deletedEvent as StudioEvent.ContentDeleted).contentId)

            val successEvent = awaitItem()
            assertTrue(successEvent is StudioEvent.ShowSuccess)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteContentWithInvalidIdDoesNotCrash() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val initialCount = store.state.value.myContent.size

        store.processIntent(StudioIntent.DeleteContent("invalid-id"))
        testScope.advanceUntilIdle()

        assertEquals(initialCount, store.state.value.myContent.size)
    }

    // ===== PublishDraft Tests =====

    @Test
    fun publishDraftMovesDraftToContent() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val loadedState = store.state.value
        val initialContentCount = loadedState.myContent.size
        val initialDraftCount = loadedState.drafts.size
        val draftToPublish = loadedState.drafts.first()

        store.processIntent(StudioIntent.PublishDraft(draftToPublish.id))
        testScope.advanceUntilIdle()

        val updatedState = store.state.value
        assertEquals(initialContentCount + 1, updatedState.myContent.size)
        assertEquals(initialDraftCount - 1, updatedState.drafts.size)
        assertFalse(updatedState.drafts.any { it.id == draftToPublish.id })
    }

    @Test
    fun publishDraftAddsToBeginningOfContentList() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val draftToPublish = store.state.value.drafts.first()
        val draftPartyId = draftToPublish.partyId

        store.processIntent(StudioIntent.PublishDraft(draftToPublish.id))
        testScope.advanceUntilIdle()

        val updatedState = store.state.value
        val firstContent = updatedState.myContent.first()
        assertEquals(draftPartyId ?: "unknown", firstContent.partyId)
    }

    @Test
    fun publishDraftSetsCorrectContentProperties() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val draftToPublish = store.state.value.drafts.first()

        store.processIntent(StudioIntent.PublishDraft(draftToPublish.id))
        testScope.advanceUntilIdle()

        val publishedContent = store.state.value.myContent.first()
        assertEquals(0, publishedContent.likesCount)
        assertEquals(0, publishedContent.commentsCount)
        assertEquals(0, publishedContent.viewsCount)
        assertTrue(publishedContent.isPublished)
        assertEquals(draftToPublish.mediaType, publishedContent.mediaType)
        assertEquals(draftToPublish.caption, publishedContent.caption)
        assertEquals(draftToPublish.mood, publishedContent.mood)
    }

    @Test
    fun publishDraftSendsPublishedEvent() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val draftToPublish = store.state.value.drafts.first()

        store.events.test {
            store.processIntent(StudioIntent.PublishDraft(draftToPublish.id))
            testScope.advanceUntilIdle()

            val publishedEvent = awaitItem()
            assertTrue(publishedEvent is StudioEvent.ContentPublished)

            val successEvent = awaitItem()
            assertTrue(successEvent is StudioEvent.ShowSuccess)
            assertTrue((successEvent as StudioEvent.ShowSuccess).message.contains("published"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun publishDraftWithInvalidIdDoesNothing() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val initialContentCount = store.state.value.myContent.size
        val initialDraftCount = store.state.value.drafts.size

        store.processIntent(StudioIntent.PublishDraft("invalid-id"))
        testScope.advanceUntilIdle()

        val updatedState = store.state.value
        assertEquals(initialContentCount, updatedState.myContent.size)
        assertEquals(initialDraftCount, updatedState.drafts.size)
    }

    // ===== DeleteDraft Tests =====

    @Test
    fun deleteDraftRemovesDraftFromList() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val loadedState = store.state.value
        val initialCount = loadedState.drafts.size
        val draftToDelete = loadedState.drafts.first()

        store.processIntent(StudioIntent.DeleteDraft(draftToDelete.id))
        testScope.advanceUntilIdle()

        val updatedState = store.state.value
        assertEquals(initialCount - 1, updatedState.drafts.size)
        assertFalse(updatedState.drafts.any { it.id == draftToDelete.id })
    }

    @Test
    fun deleteDraftSendsSuccessEvent() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val draftToDelete = store.state.value.drafts.first()

        store.events.test {
            store.processIntent(StudioIntent.DeleteDraft(draftToDelete.id))
            testScope.advanceUntilIdle()

            val event = awaitItem()
            assertTrue(event is StudioEvent.ShowSuccess)
            assertTrue((event as StudioEvent.ShowSuccess).message.contains("deleted"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteDraftWithInvalidIdDoesNotCrash() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val initialCount = store.state.value.drafts.size

        store.processIntent(StudioIntent.DeleteDraft("invalid-id"))
        testScope.advanceUntilIdle()

        assertEquals(initialCount, store.state.value.drafts.size)
    }

    // ===== StartCreateContent Tests =====

    @Test
    fun startCreateContentSendsNavigationEvent() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        store.events.test {
            store.processIntent(StudioIntent.StartCreateContent)
            testScope.advanceUntilIdle()

            val event = awaitItem()
            assertTrue(event is StudioEvent.NavigateToCreateContent)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== CreateContentForParty Tests =====

    @Test
    fun createContentForPartySendsNavigationEvent() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        store.events.test {
            store.processIntent(StudioIntent.CreateContentForParty("party-123"))
            testScope.advanceUntilIdle()

            val event = awaitItem()
            assertTrue(event is StudioEvent.NavigateToCreateContent)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== DismissError Tests =====

    @Test
    fun dismissErrorClearsError() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        store.processIntent(StudioIntent.DismissError)
        assertNull(store.state.value.error)
    }

    // ===== Navigation Intents Tests =====

    @Test
    fun editContentDoesNotChangeState() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val loadedState = store.state.value

        store.processIntent(StudioIntent.EditContent("content-123"))
        testScope.advanceUntilIdle()

        assertEquals(loadedState.myContent.size, store.state.value.myContent.size)
        assertEquals(loadedState.selectedTab, store.state.value.selectedTab)
    }

    @Test
    fun viewContentDetailsDoesNotChangeState() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val loadedState = store.state.value

        store.processIntent(StudioIntent.ViewContentDetails("content-123"))
        testScope.advanceUntilIdle()

        assertEquals(loadedState.myContent.size, store.state.value.myContent.size)
        assertEquals(loadedState.selectedTab, store.state.value.selectedTab)
    }

    @Test
    fun editDraftDoesNotChangeState() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val loadedState = store.state.value

        store.processIntent(StudioIntent.EditDraft("draft-123"))
        testScope.advanceUntilIdle()

        assertEquals(loadedState.drafts.size, store.state.value.drafts.size)
        assertEquals(loadedState.selectedTab, store.state.value.selectedTab)
    }

    // ===== Data Integrity Tests =====

    @Test
    fun myContentHasUniqueIds() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        val ids = store.state.value.myContent.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun draftsHaveUniqueIds() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        val ids = store.state.value.drafts.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun recentPartiesHaveUniqueIds() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        val ids = store.state.value.recentParties.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun myContentHasValidTimestamps() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        store.state.value.myContent.forEach { content ->
            assertTrue(content.createdAt > 0)
        }
    }

    @Test
    fun draftsHaveValidTimestamps() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        store.state.value.drafts.forEach { draft ->
            assertTrue(draft.createdAt > 0)
            assertTrue(draft.lastEditedAt > 0)
            assertTrue(draft.lastEditedAt >= draft.createdAt)
        }
    }

    @Test
    fun recentPartiesHaveValidTimestamps() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        store.state.value.recentParties.forEach { party ->
            assertTrue(party.lastActivityAt > 0)
        }
    }
}
