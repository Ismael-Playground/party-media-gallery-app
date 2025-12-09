package com.app.test.store

import app.cash.turbine.test
import com.partygallery.domain.model.MediaType
import com.partygallery.domain.model.PartyMood
import com.partygallery.presentation.intent.PartyDetailIntent
import com.partygallery.presentation.state.MediaFilterOption
import com.partygallery.presentation.store.PartyDetailStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for PartyDetailStore.
 *
 * Tests cover all intents and state transitions for the party detail screen.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PartyDetailStoreTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var testScope: TestScope
    private val testPartyId = "party-123"

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        testScope = TestScope(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createStore(partyId: String = testPartyId): PartyDetailStore {
        return PartyDetailStore(partyId = partyId, scope = testScope)
    }

    // ===== Initial State Tests =====

    @Test
    fun initialStateHasCorrectDefaults() = runTest {
        val store = createStore()

        val state = store.state.value
        assertTrue(state.isLoading)
        assertNull(state.error)
        assertNull(state.party)
        assertTrue(state.mediaItems.isEmpty())
        assertFalse(state.isLoadingMedia)
        assertEquals(MediaFilterOption.All, state.selectedMediaFilter)
        assertFalse(state.isUserAttending)
        assertFalse(state.isRsvpLoading)
    }

    @Test
    fun initAutomaticallyLoadsParty() = runTest {
        val store = createStore()

        store.state.test {
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)

            testScope.advanceUntilIdle()

            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertNotNull(loadedState.party)
            assertTrue(loadedState.mediaItems.isNotEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== LoadParty Tests =====

    @Test
    fun loadPartySetsLoadingState() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial state

            store.processIntent(PartyDetailIntent.LoadParty(testPartyId))
            val loadingState = store.state.value
            assertTrue(loadingState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadPartyPopulatesPartyData() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            val loadedState = awaitItem()

            assertNotNull(loadedState.party)
            assertEquals(testPartyId, loadedState.party!!.id)
            assertNotNull(loadedState.party!!.host)
            assertNotNull(loadedState.party!!.venue)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadPartyPopulatesMediaItems() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            val loadedState = awaitItem()

            assertTrue(loadedState.mediaItems.isNotEmpty())
            assertEquals(12, loadedState.mediaItems.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadPartySetsIsUserAttendingFromParty() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            val loadedState = awaitItem()

            assertEquals(loadedState.party?.isUserAttending, loadedState.isUserAttending)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadPartyClearsError() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(PartyDetailIntent.LoadParty(testPartyId))
            val state = store.state.value
            assertNull(state.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== RefreshParty Tests =====

    @Test
    fun refreshPartySetsLoadingState() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(PartyDetailIntent.RefreshParty)
            val refreshingState = awaitItem()
            assertTrue(refreshingState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refreshPartyUpdatesPartyData() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(PartyDetailIntent.RefreshParty)
            awaitItem() // Loading state

            testScope.advanceUntilIdle()
            val refreshedState = awaitItem()

            assertFalse(refreshedState.isLoading)
            assertNotNull(refreshedState.party)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== SelectMediaFilter Tests =====

    @Test
    fun selectMediaFilterUpdatesFilter() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        store.processIntent(PartyDetailIntent.SelectMediaFilter(MediaFilterOption.Photos))
        testScope.advanceUntilIdle()

        assertEquals(MediaFilterOption.Photos, store.state.value.selectedMediaFilter)
    }

    @Test
    fun selectMediaFilterAllFilters() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        for (filter in MediaFilterOption.entries) {
            store.processIntent(PartyDetailIntent.SelectMediaFilter(filter))
            testScope.advanceUntilIdle()

            assertEquals(filter, store.state.value.selectedMediaFilter)
        }
    }

    @Test
    fun selectMediaFilterPhotosShowsOnlyPhotos() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        store.processIntent(PartyDetailIntent.SelectMediaFilter(MediaFilterOption.Photos))
        testScope.advanceUntilIdle()

        val mediaItems = store.state.value.mediaItems
        assertTrue(mediaItems.isNotEmpty())
        assertTrue(mediaItems.all { it.type == MediaType.PHOTO })
    }

    @Test
    fun selectMediaFilterVideosShowsOnlyVideos() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        store.processIntent(PartyDetailIntent.SelectMediaFilter(MediaFilterOption.Videos))
        testScope.advanceUntilIdle()

        val mediaItems = store.state.value.mediaItems
        assertTrue(mediaItems.isNotEmpty())
        assertTrue(mediaItems.all { it.type == MediaType.VIDEO })
    }

    @Test
    fun selectMediaFilterHypeShowsOnlyHypeMood() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        store.processIntent(PartyDetailIntent.SelectMediaFilter(MediaFilterOption.Hype))
        testScope.advanceUntilIdle()

        val mediaItems = store.state.value.mediaItems
        // May be empty if no HYPE mood items generated
        assertTrue(mediaItems.all { it.mood == PartyMood.HYPE })
    }

    @Test
    fun selectMediaFilterSetsLoadingMediaState() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(PartyDetailIntent.SelectMediaFilter(MediaFilterOption.Photos))
            val filteringState = awaitItem()
            assertTrue(filteringState.isLoadingMedia)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== LoadMoreMedia Tests =====

    @Test
    fun loadMoreMediaAppendsItems() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val initialCount = store.state.value.mediaItems.size

        store.processIntent(PartyDetailIntent.LoadMoreMedia)
        testScope.advanceUntilIdle()

        assertTrue(store.state.value.mediaItems.size > initialCount)
        assertEquals(initialCount + 12, store.state.value.mediaItems.size)
    }

    @Test
    fun loadMoreMediaMultipleTimesAccumulatesItems() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        store.processIntent(PartyDetailIntent.LoadMoreMedia)
        testScope.advanceUntilIdle()

        store.processIntent(PartyDetailIntent.LoadMoreMedia)
        testScope.advanceUntilIdle()

        assertEquals(36, store.state.value.mediaItems.size) // 12 + 12 + 12
    }

    // ===== ToggleRsvp Tests =====

    @Test
    fun toggleRsvpSetsRsvpLoadingState() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(PartyDetailIntent.ToggleRsvp)
            val rsvpLoadingState = awaitItem()
            assertTrue(rsvpLoadingState.isRsvpLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun toggleRsvpTogglesAttendingState() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val initialAttending = store.state.value.isUserAttending

        store.processIntent(PartyDetailIntent.ToggleRsvp)
        testScope.advanceUntilIdle()

        assertEquals(!initialAttending, store.state.value.isUserAttending)
    }

    @Test
    fun toggleRsvpUpdatesAttendeesCount() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val initialCount = store.state.value.party?.attendeesCount ?: 0
        val wasAttending = store.state.value.isUserAttending

        store.processIntent(PartyDetailIntent.ToggleRsvp)
        testScope.advanceUntilIdle()

        val newCount = store.state.value.party?.attendeesCount ?: 0
        if (wasAttending) {
            assertEquals(initialCount - 1, newCount)
        } else {
            assertEquals(initialCount + 1, newCount)
        }
    }

    @Test
    fun toggleRsvpClearsLoadingState() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        store.processIntent(PartyDetailIntent.ToggleRsvp)
        testScope.advanceUntilIdle()

        assertFalse(store.state.value.isRsvpLoading)
    }

    @Test
    fun toggleRsvpTwiceReturnsToOriginalState() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val initialAttending = store.state.value.isUserAttending

        store.processIntent(PartyDetailIntent.ToggleRsvp)
        testScope.advanceUntilIdle()

        store.processIntent(PartyDetailIntent.ToggleRsvp)
        testScope.advanceUntilIdle()

        assertEquals(initialAttending, store.state.value.isUserAttending)
    }

    // ===== LikeMedia Tests =====

    @Test
    fun likeMediaUpdatesMediaState() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val mediaToLike = store.state.value.mediaItems.first { !it.socialMetrics.isLikedByUser }
        val initialLikes = mediaToLike.socialMetrics.likesCount

        store.processIntent(PartyDetailIntent.LikeMedia(mediaToLike.id))
        testScope.advanceUntilIdle()

        val updatedMedia = store.state.value.mediaItems.first { it.id == mediaToLike.id }
        assertTrue(updatedMedia.socialMetrics.isLikedByUser)
        assertEquals(initialLikes + 1, updatedMedia.socialMetrics.likesCount)
    }

    @Test
    fun likeMediaDoesNotAffectOtherMedia() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val mediaItems = store.state.value.mediaItems
        val targetMedia = mediaItems.first()
        val otherMedia = mediaItems.drop(1)

        store.processIntent(PartyDetailIntent.LikeMedia(targetMedia.id))
        testScope.advanceUntilIdle()

        val updatedOtherMedia = store.state.value.mediaItems.filter { it.id != targetMedia.id }

        otherMedia.forEachIndexed { index, original ->
            val updated = updatedOtherMedia[index]
            assertEquals(original.socialMetrics.isLikedByUser, updated.socialMetrics.isLikedByUser)
            assertEquals(original.socialMetrics.likesCount, updated.socialMetrics.likesCount)
        }
    }

    @Test
    fun likeMediaWithInvalidIdDoesNotCrash() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val originalItems = store.state.value.mediaItems

        store.processIntent(PartyDetailIntent.LikeMedia("invalid-id"))
        testScope.advanceUntilIdle()

        // Media items should remain unchanged
        val currentItems = store.state.value.mediaItems
        originalItems.forEachIndexed { index, original ->
            assertEquals(original.socialMetrics.likesCount, currentItems[index].socialMetrics.likesCount)
        }
    }

    // ===== UnlikeMedia Tests =====

    @Test
    fun unlikeMediaUpdatesMediaState() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val mediaToUnlike = store.state.value.mediaItems.first { it.socialMetrics.isLikedByUser }
        val initialLikes = mediaToUnlike.socialMetrics.likesCount

        store.processIntent(PartyDetailIntent.UnlikeMedia(mediaToUnlike.id))
        testScope.advanceUntilIdle()

        val updatedMedia = store.state.value.mediaItems.first { it.id == mediaToUnlike.id }
        assertFalse(updatedMedia.socialMetrics.isLikedByUser)
        assertEquals((initialLikes - 1).coerceAtLeast(0), updatedMedia.socialMetrics.likesCount)
    }

    @Test
    fun unlikeMediaDoesNotGoBelowZero() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        // Find media and like it, then unlike multiple times
        val targetMedia = store.state.value.mediaItems.first()

        // Unlike to force likes to potentially go negative
        repeat(5) {
            store.processIntent(PartyDetailIntent.UnlikeMedia(targetMedia.id))
            testScope.advanceUntilIdle()
        }

        val updatedMedia = store.state.value.mediaItems.first { it.id == targetMedia.id }
        assertTrue(updatedMedia.socialMetrics.likesCount >= 0)
    }

    @Test
    fun unlikeMediaDoesNotAffectOtherMedia() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val mediaItems = store.state.value.mediaItems
        val targetMedia = mediaItems.first()
        val otherMedia = mediaItems.drop(1)

        store.processIntent(PartyDetailIntent.UnlikeMedia(targetMedia.id))
        testScope.advanceUntilIdle()

        val updatedOtherMedia = store.state.value.mediaItems.filter { it.id != targetMedia.id }

        otherMedia.forEachIndexed { index, original ->
            val updated = updatedOtherMedia[index]
            assertEquals(original.socialMetrics.isLikedByUser, updated.socialMetrics.isLikedByUser)
            assertEquals(original.socialMetrics.likesCount, updated.socialMetrics.likesCount)
        }
    }

    // ===== Navigation Intents Tests =====

    @Test
    fun navigateToUploadMediaDoesNotChangeState() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val loadedState = store.state.value

        store.processIntent(PartyDetailIntent.NavigateToUploadMedia)
        testScope.advanceUntilIdle()

        assertEquals(loadedState.mediaItems.size, store.state.value.mediaItems.size)
        assertEquals(loadedState.selectedMediaFilter, store.state.value.selectedMediaFilter)
    }

    @Test
    fun navigateBackDoesNotChangeState() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()
        val loadedState = store.state.value

        store.processIntent(PartyDetailIntent.NavigateBack)
        testScope.advanceUntilIdle()

        assertEquals(loadedState.party?.id, store.state.value.party?.id)
        assertEquals(loadedState.selectedMediaFilter, store.state.value.selectedMediaFilter)
    }

    // ===== Data Integrity Tests =====

    @Test
    fun mediaItemsHaveUniqueIds() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        val ids = store.state.value.mediaItems.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun mediaItemsBelongToCorrectParty() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        store.state.value.mediaItems.forEach { media ->
            assertEquals(testPartyId, media.partyEventId)
        }
    }

    @Test
    fun partyHasValidData() = runTest {
        val store = createStore()

        testScope.advanceUntilIdle()

        val party = store.state.value.party
        assertNotNull(party)
        assertNotNull(party.id)
        assertNotNull(party.title)
        assertNotNull(party.host)
        assertNotNull(party.venue)
        assertTrue(party.attendeesCount >= 0)
        assertTrue(party.mediaCount >= 0)
    }

    @Test
    fun differentPartyIdsLoadDifferentParties() = runTest {
        val store1 = createStore("party-1")
        val store2 = createStore("party-2")

        testScope.advanceUntilIdle()

        assertEquals("party-1", store1.state.value.party?.id)
        assertEquals("party-2", store2.state.value.party?.id)
    }
}
