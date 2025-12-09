package com.app.test.store

import app.cash.turbine.test
import com.partygallery.presentation.intent.HomeIntent
import com.partygallery.presentation.state.FeedFilter
import com.partygallery.presentation.state.FeedItem
import com.partygallery.presentation.store.HomeStore
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
 * Unit tests for HomeStore.
 *
 * Tests cover all intents and state transitions for the home feed.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeStoreTest {

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

    private fun createStore(): HomeStore {
        return HomeStore(scope = testScope)
    }

    // ===== Initial State Tests =====

    @Test
    fun initialStateHasCorrectDefaults() = runTest {
        val store = createStore()

        // Initial state before feed loads
        val state = store.state.value
        assertTrue(state.isLoading)
        assertFalse(state.isRefreshing)
        assertNull(state.error)
        assertEquals(FeedFilter.All, state.selectedFilter)
    }

    @Test
    fun initAutomaticallyLoadsFeed() = runTest {
        val store = createStore()

        store.state.test {
            // Initial state with isLoading = true
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)

            // Advance time to allow feed to load
            testScope.advanceUntilIdle()

            // After load completes
            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertTrue(loadedState.feedItems.isNotEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== LoadFeed Tests =====

    @Test
    fun loadFeedSetsLoadingState() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial state

            // Trigger explicit LoadFeed
            store.processIntent(HomeIntent.LoadFeed)

            // Should still be loading (initial load)
            val loadingState = store.state.value
            assertTrue(loadingState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadFeedPopulatesFeedItems() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            val loadedState = awaitItem()

            assertFalse(loadedState.isLoading)
            assertTrue(loadedState.feedItems.isNotEmpty())
            assertEquals(10, loadedState.feedItems.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadFeedClearsError() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            // LoadFeed clears error
            store.processIntent(HomeIntent.LoadFeed)
            val state = store.state.value
            assertNull(state.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== RefreshFeed Tests =====

    @Test
    fun refreshFeedSetsRefreshingState() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(HomeIntent.RefreshFeed)
            val refreshingState = awaitItem()
            assertTrue(refreshingState.isRefreshing)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refreshFeedCompletesSuccessfully() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(HomeIntent.RefreshFeed)
            awaitItem() // Refreshing state

            testScope.advanceUntilIdle()
            val refreshedState = awaitItem()

            assertFalse(refreshedState.isRefreshing)
            assertTrue(refreshedState.feedItems.isNotEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refreshFeedClearsError() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(HomeIntent.RefreshFeed)
            val state = store.state.value
            assertNull(state.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== LoadMore Tests =====

    @Test
    fun loadMoreAppendsItems() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            val loadedState = awaitItem()
            val initialCount = loadedState.feedItems.size

            store.processIntent(HomeIntent.LoadMore)
            testScope.advanceUntilIdle()
            val moreState = awaitItem()

            assertTrue(moreState.feedItems.size > initialCount)
            assertEquals(initialCount + 10, moreState.feedItems.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadMoreMultipleTimesAccumulatesItems() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded (10 items)

            store.processIntent(HomeIntent.LoadMore)
            testScope.advanceUntilIdle()
            awaitItem() // 20 items

            store.processIntent(HomeIntent.LoadMore)
            testScope.advanceUntilIdle()
            val finalState = awaitItem()

            assertEquals(30, finalState.feedItems.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== SelectFilter Tests =====

    @Test
    fun selectFilterUpdatesSelectedFilter() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(HomeIntent.SelectFilter(FeedFilter.Live))
            val filteredState = awaitItem()

            assertEquals(FeedFilter.Live, filteredState.selectedFilter)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun selectFilterReloadsFeed() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(HomeIntent.SelectFilter(FeedFilter.Trending))

            // selectFilter updates filter then calls loadFeed which sets isLoading
            // These may come as separate emissions or combined
            var filterState = awaitItem()
            assertEquals(FeedFilter.Trending, filterState.selectedFilter)

            // If loading hasn't started yet, await loading state
            if (!filterState.isLoading) {
                filterState = awaitItem()
            }
            assertTrue(filterState.isLoading)

            testScope.advanceUntilIdle()
            val reloadedState = awaitItem()

            assertFalse(reloadedState.isLoading)
            assertTrue(reloadedState.feedItems.isNotEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun selectFilterAllFilters() = runTest {
        val store = createStore()

        // Wait for initial load
        testScope.advanceUntilIdle()

        // Test all filter options
        for (filter in FeedFilter.entries) {
            store.processIntent(HomeIntent.SelectFilter(filter))
            testScope.advanceUntilIdle()

            val state = store.state.value
            assertEquals(filter, state.selectedFilter)
            assertFalse(state.isLoading)
            assertTrue(state.feedItems.isNotEmpty())
        }
    }

    // ===== LikePost Tests =====

    @Test
    fun likePostUpdatesPostState() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            val loadedState = awaitItem()

            // Find first MediaPost
            val mediaPost = loadedState.feedItems.filterIsInstance<FeedItem.MediaPost>().first()
            val initialLikes = mediaPost.likesCount

            store.processIntent(HomeIntent.LikePost(mediaPost.id))
            val likedState = awaitItem()

            val updatedPost = likedState.feedItems.filterIsInstance<FeedItem.MediaPost>()
                .first { it.id == mediaPost.id }

            assertTrue(updatedPost.isLiked)
            assertEquals(initialLikes + 1, updatedPost.likesCount)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun likePostDoesNotAffectOtherPosts() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            val loadedState = awaitItem()

            val mediaPosts = loadedState.feedItems.filterIsInstance<FeedItem.MediaPost>()
            val targetPost = mediaPosts.first()
            val otherPosts = mediaPosts.drop(1)

            store.processIntent(HomeIntent.LikePost(targetPost.id))
            val likedState = awaitItem()

            val updatedOtherPosts = likedState.feedItems.filterIsInstance<FeedItem.MediaPost>()
                .filter { it.id != targetPost.id }

            // Other posts should remain unchanged
            otherPosts.forEachIndexed { index, original ->
                val updated = updatedOtherPosts[index]
                assertEquals(original.isLiked, updated.isLiked)
                assertEquals(original.likesCount, updated.likesCount)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun likePostWithInvalidIdDoesNothing() = runTest {
        val store = createStore()

        // Wait for initial load
        testScope.advanceUntilIdle()
        val loadedState = store.state.value
        val originalPosts = loadedState.feedItems.filterIsInstance<FeedItem.MediaPost>()

        store.processIntent(HomeIntent.LikePost("invalid-id"))
        testScope.advanceUntilIdle()

        // State should remain unchanged
        val currentState = store.state.value
        val updatedPosts = currentState.feedItems.filterIsInstance<FeedItem.MediaPost>()

        updatedPosts.forEachIndexed { index, post ->
            assertEquals(originalPosts[index].isLiked, post.isLiked)
            assertEquals(originalPosts[index].likesCount, post.likesCount)
        }
    }

    // ===== UnlikePost Tests =====

    @Test
    fun unlikePostUpdatesPostState() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            val loadedState = awaitItem()

            // Find first MediaPost and like it first
            val mediaPost = loadedState.feedItems.filterIsInstance<FeedItem.MediaPost>().first()

            store.processIntent(HomeIntent.LikePost(mediaPost.id))
            val likedState = awaitItem()
            val likedPost = likedState.feedItems.filterIsInstance<FeedItem.MediaPost>()
                .first { it.id == mediaPost.id }
            val likesAfterLike = likedPost.likesCount

            store.processIntent(HomeIntent.UnlikePost(mediaPost.id))
            val unlikedState = awaitItem()

            val unlikedPost = unlikedState.feedItems.filterIsInstance<FeedItem.MediaPost>()
                .first { it.id == mediaPost.id }

            assertFalse(unlikedPost.isLiked)
            assertEquals(likesAfterLike - 1, unlikedPost.likesCount)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun unlikePostDoesNotAffectOtherPosts() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            val loadedState = awaitItem()

            val mediaPosts = loadedState.feedItems.filterIsInstance<FeedItem.MediaPost>()
            val targetPost = mediaPosts.first()
            val otherPosts = mediaPosts.drop(1)

            store.processIntent(HomeIntent.UnlikePost(targetPost.id))
            val unlikedState = awaitItem()

            val updatedOtherPosts = unlikedState.feedItems.filterIsInstance<FeedItem.MediaPost>()
                .filter { it.id != targetPost.id }

            otherPosts.forEachIndexed { index, original ->
                val updated = updatedOtherPosts[index]
                assertEquals(original.isLiked, updated.isLiked)
                assertEquals(original.likesCount, updated.likesCount)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== DismissError Tests =====

    @Test
    fun dismissErrorClearsError() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            awaitItem() // Loaded

            store.processIntent(HomeIntent.DismissError)
            val state = store.state.value

            assertNull(state.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== Navigation Intents Tests =====

    @Test
    fun openPartyDoesNotChangeState() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            val loadedState = awaitItem()

            store.processIntent(HomeIntent.OpenParty("party-123"))

            // State should not change - no emission expected
            // We verify by checking current value is same
            assertEquals(loadedState.feedItems.size, store.state.value.feedItems.size)
            assertEquals(loadedState.selectedFilter, store.state.value.selectedFilter)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun openProfileDoesNotChangeState() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            val loadedState = awaitItem()

            store.processIntent(HomeIntent.OpenProfile("user-123"))

            // State should not change
            assertEquals(loadedState.feedItems.size, store.state.value.feedItems.size)
            assertEquals(loadedState.selectedFilter, store.state.value.selectedFilter)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ===== Feed Content Tests =====

    @Test
    fun feedContainsBothPartyCardsAndMediaPosts() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            val loadedState = awaitItem()

            val partyCards = loadedState.feedItems.filterIsInstance<FeedItem.PartyCard>()
            val mediaPosts = loadedState.feedItems.filterIsInstance<FeedItem.MediaPost>()

            assertTrue(partyCards.isNotEmpty())
            assertTrue(mediaPosts.isNotEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun feedItemsHaveUniqueIds() = runTest {
        val store = createStore()

        store.state.test {
            awaitItem() // Initial
            testScope.advanceUntilIdle()
            val loadedState = awaitItem()

            val ids = loadedState.feedItems.map { it.id }
            val uniqueIds = ids.toSet()

            assertEquals(ids.size, uniqueIds.size)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
