package com.app.test.store

import app.cash.turbine.test
import com.partygallery.presentation.intent.FavoritesIntent
import com.partygallery.presentation.state.FavoritesEvent
import com.partygallery.presentation.state.FavoritesTab
import com.partygallery.presentation.store.FavoritesStore
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
 * Unit tests for FavoritesStore.
 *
 * Tests cover all intents and state transitions for the favorites screen.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesStoreTest {

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

    private fun createStore(): FavoritesStore {
        return FavoritesStore(scope = testScope)
    }

    // ===== Initial State Tests =====

    @Test
    fun initialStateHasCorrectDefaults() = runTest {
        val store = createStore()

        val state = store.state.value
        assertTrue(state.isLoading)
        assertFalse(state.isRefreshing)
        assertNull(state.error)
        assertEquals(FavoritesTab.Parties, state.selectedTab)
    }

    @Test
    fun initAutomaticallyLoadsFavorites() = runTest {
        val store = createStore()

        store.state.test {
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)

            testScope.advanceUntilIdle()

            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertTrue(loadedState.savedParties.isNotEmpty())
            assertTrue(loadedState.likedMedia.isNotEmpty())
            assertTrue(loadedState.suggestedParties.isNotEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== LoadFavorites Tests =====

    @Test
    fun loadFavoritesSetsLoadingState() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial state

            store.processIntent(FavoritesIntent.LoadFavorites)
            val loadingState = store.state.value
            assertTrue(loadingState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadFavoritesPopulatesData() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            val loadedState = awaitItem()

            assertFalse(loadedState.isLoading)
            assertTrue(loadedState.savedParties.isNotEmpty())
            assertTrue(loadedState.likedMedia.isNotEmpty())
            assertTrue(loadedState.suggestedParties.isNotEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadFavoritesClearsError() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(FavoritesIntent.LoadFavorites)
            val state = store.state.value
            assertNull(state.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== RefreshFavorites Tests =====

    @Test
    fun refreshFavoritesSetsRefreshingState() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(FavoritesIntent.RefreshFavorites)
            val refreshingState = awaitItem()
            assertTrue(refreshingState.isRefreshing)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refreshFavoritesCompletesSuccessfully() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(FavoritesIntent.RefreshFavorites)
            awaitItem() // Refreshing state

            testScope.advanceUntilIdle()
            val refreshedState = awaitItem()

            assertFalse(refreshedState.isRefreshing)
            assertTrue(refreshedState.savedParties.isNotEmpty())
            assertTrue(refreshedState.likedMedia.isNotEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refreshFavoritesClearsError() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(FavoritesIntent.RefreshFavorites)
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

        store.processIntent(FavoritesIntent.SelectTab(FavoritesTab.Media))
        assertEquals(FavoritesTab.Media, store.state.value.selectedTab)

        store.processIntent(FavoritesIntent.SelectTab(FavoritesTab.Suggested))
        assertEquals(FavoritesTab.Suggested, store.state.value.selectedTab)

        store.processIntent(FavoritesIntent.SelectTab(FavoritesTab.Parties))
        assertEquals(FavoritesTab.Parties, store.state.value.selectedTab)
    }

    @Test
    fun selectTabAllTabs() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        for (tab in FavoritesTab.entries) {
            store.processIntent(FavoritesIntent.SelectTab(tab))
            assertEquals(tab, store.state.value.selectedTab)
        }
    }

    // ===== UnsaveParty Tests =====

    @Test
    fun unsavePartyRemovesPartyFromList() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val loadedState = store.state.value
        val initialCount = loadedState.savedParties.size
        val partyToRemove = loadedState.savedParties.first()

        store.processIntent(FavoritesIntent.UnsaveParty(partyToRemove.id))
        testScope.advanceUntilIdle()

        val updatedState = store.state.value
        assertEquals(initialCount - 1, updatedState.savedParties.size)
        assertFalse(updatedState.savedParties.any { it.id == partyToRemove.id })
    }

    @Test
    fun unsavePartySendsSuccessEvent() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val partyToRemove = store.state.value.savedParties.first()

        store.events.test {
            store.processIntent(FavoritesIntent.UnsaveParty(partyToRemove.id))
            testScope.advanceUntilIdle()

            val event = awaitItem()
            assertTrue(event is FavoritesEvent.ShowSuccess)
            assertTrue((event as FavoritesEvent.ShowSuccess).message.contains("removed"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun unsavePartyWithInvalidIdDoesNotCrash() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val initialCount = store.state.value.savedParties.size

        store.processIntent(FavoritesIntent.UnsaveParty("invalid-id"))
        testScope.advanceUntilIdle()

        // Count remains the same
        assertEquals(initialCount, store.state.value.savedParties.size)
    }

    // ===== UnlikeMedia Tests =====

    @Test
    fun unlikeMediaRemovesMediaFromList() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val loadedState = store.state.value
        val initialCount = loadedState.likedMedia.size
        val mediaToRemove = loadedState.likedMedia.first()

        store.processIntent(FavoritesIntent.UnlikeMedia(mediaToRemove.id))
        testScope.advanceUntilIdle()

        val updatedState = store.state.value
        assertEquals(initialCount - 1, updatedState.likedMedia.size)
        assertFalse(updatedState.likedMedia.any { it.id == mediaToRemove.id })
    }

    @Test
    fun unlikeMediaSendsSuccessEvent() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val mediaToRemove = store.state.value.likedMedia.first()

        store.events.test {
            store.processIntent(FavoritesIntent.UnlikeMedia(mediaToRemove.id))
            testScope.advanceUntilIdle()

            val event = awaitItem()
            assertTrue(event is FavoritesEvent.ShowSuccess)
            assertTrue((event as FavoritesEvent.ShowSuccess).message.contains("removed"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun unlikeMediaWithInvalidIdDoesNotCrash() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val initialCount = store.state.value.likedMedia.size

        store.processIntent(FavoritesIntent.UnlikeMedia("invalid-id"))
        testScope.advanceUntilIdle()

        assertEquals(initialCount, store.state.value.likedMedia.size)
    }

    // ===== SaveSuggestedParty Tests =====

    @Test
    fun saveSuggestedPartyMovesSuggestionToSaved() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val loadedState = store.state.value
        val initialSavedCount = loadedState.savedParties.size
        val initialSuggestedCount = loadedState.suggestedParties.size
        val suggestionToSave = loadedState.suggestedParties.first()

        store.processIntent(FavoritesIntent.SaveSuggestedParty(suggestionToSave.id))
        testScope.advanceUntilIdle()

        val updatedState = store.state.value
        assertEquals(initialSavedCount + 1, updatedState.savedParties.size)
        assertEquals(initialSuggestedCount - 1, updatedState.suggestedParties.size)
        assertTrue(updatedState.savedParties.any { it.id == suggestionToSave.id })
        assertFalse(updatedState.suggestedParties.any { it.id == suggestionToSave.id })
    }

    @Test
    fun saveSuggestedPartyAddsToBeginningOfList() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val suggestionToSave = store.state.value.suggestedParties.first()

        store.processIntent(FavoritesIntent.SaveSuggestedParty(suggestionToSave.id))
        testScope.advanceUntilIdle()

        val updatedState = store.state.value
        assertEquals(suggestionToSave.id, updatedState.savedParties.first().id)
    }

    @Test
    fun saveSuggestedPartySendsSuccessEvent() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val suggestionToSave = store.state.value.suggestedParties.first()

        store.events.test {
            store.processIntent(FavoritesIntent.SaveSuggestedParty(suggestionToSave.id))
            testScope.advanceUntilIdle()

            val event = awaitItem()
            assertTrue(event is FavoritesEvent.ShowSuccess)
            assertTrue((event as FavoritesEvent.ShowSuccess).message.contains("saved"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun saveSuggestedPartyWithInvalidIdDoesNothing() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val initialSavedCount = store.state.value.savedParties.size
        val initialSuggestedCount = store.state.value.suggestedParties.size

        store.processIntent(FavoritesIntent.SaveSuggestedParty("invalid-id"))
        testScope.advanceUntilIdle()

        val updatedState = store.state.value
        assertEquals(initialSavedCount, updatedState.savedParties.size)
        assertEquals(initialSuggestedCount, updatedState.suggestedParties.size)
    }

    // ===== DismissSuggestion Tests =====

    @Test
    fun dismissSuggestionRemovesSuggestionFromList() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val loadedState = store.state.value
        val initialCount = loadedState.suggestedParties.size
        val suggestionToDismiss = loadedState.suggestedParties.first()

        store.processIntent(FavoritesIntent.DismissSuggestion(suggestionToDismiss.id))
        testScope.advanceUntilIdle()

        val updatedState = store.state.value
        assertEquals(initialCount - 1, updatedState.suggestedParties.size)
        assertFalse(updatedState.suggestedParties.any { it.id == suggestionToDismiss.id })
    }

    @Test
    fun dismissSuggestionDoesNotAddToSaved() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val initialSavedCount = store.state.value.savedParties.size
        val suggestionToDismiss = store.state.value.suggestedParties.first()

        store.processIntent(FavoritesIntent.DismissSuggestion(suggestionToDismiss.id))
        testScope.advanceUntilIdle()

        // Saved count should remain the same
        assertEquals(initialSavedCount, store.state.value.savedParties.size)
    }

    @Test
    fun dismissSuggestionWithInvalidIdDoesNotCrash() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val initialCount = store.state.value.suggestedParties.size

        store.processIntent(FavoritesIntent.DismissSuggestion("invalid-id"))
        testScope.advanceUntilIdle()

        assertEquals(initialCount, store.state.value.suggestedParties.size)
    }

    // ===== DismissError Tests =====

    @Test
    fun dismissErrorClearsError() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        store.processIntent(FavoritesIntent.DismissError)
        assertNull(store.state.value.error)
    }

    // ===== Navigation Intents Tests =====

    @Test
    fun openPartyDoesNotChangeState() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val loadedState = store.state.value

        store.processIntent(FavoritesIntent.OpenParty("party-123"))
        testScope.advanceUntilIdle()

        // State should not change
        assertEquals(loadedState.savedParties.size, store.state.value.savedParties.size)
        assertEquals(loadedState.selectedTab, store.state.value.selectedTab)
    }

    @Test
    fun openMediaDoesNotChangeState() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val loadedState = store.state.value

        store.processIntent(FavoritesIntent.OpenMedia("media-123"))
        testScope.advanceUntilIdle()

        // State should not change
        assertEquals(loadedState.likedMedia.size, store.state.value.likedMedia.size)
        assertEquals(loadedState.selectedTab, store.state.value.selectedTab)
    }

    // ===== Data Integrity Tests =====

    @Test
    fun savedPartiesHaveUniqueIds() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        val ids = store.state.value.savedParties.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun likedMediaHaveUniqueIds() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        val ids = store.state.value.likedMedia.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun suggestedPartiesHaveUniqueIds() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        val ids = store.state.value.suggestedParties.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun savedPartiesHaveValidSavedAtTimestamps() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        store.state.value.savedParties.forEach { party ->
            assertTrue(party.savedAt > 0)
        }
    }

    @Test
    fun likedMediaHaveValidLikedAtTimestamps() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        store.state.value.likedMedia.forEach { media ->
            assertTrue(media.likedAt > 0)
        }
    }

    @Test
    fun suggestedPartiesHaveValidMatchScores() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        store.state.value.suggestedParties.forEach { party ->
            assertTrue(party.matchScore in 0..100)
        }
    }
}
