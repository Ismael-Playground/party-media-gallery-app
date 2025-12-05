package com.partygallery.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.partygallery.presentation.intent.HomeIntent
import com.partygallery.presentation.state.FeedFilter
import com.partygallery.presentation.state.FeedItem
import com.partygallery.presentation.store.HomeStore
import com.partygallery.ui.components.AvatarSize
import com.partygallery.ui.components.AvatarWithInitials
import com.partygallery.ui.components.FeaturedMediaCard
import com.partygallery.ui.components.LiveBadge
import com.partygallery.ui.components.MoodTag
import com.partygallery.ui.components.ScrollablePillTabs
import com.partygallery.ui.theme.PartyGalleryColors
import com.partygallery.ui.theme.PartyGalleryShapes
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.Theme

/**
 * Home Feed Screen.
 *
 * S3-009: HomeScreen con LazyColumn
 * S3-012: Pull-to-refresh (simplified for now)
 *
 * Design: Dark Mode First
 * - Background: #0A0A0A
 * - Primary: #F59E0B (Amber)
 */
@Composable
fun HomeScreen(
    store: HomeStore = remember { HomeStore() },
    onPartyClick: (String) -> Unit = {},
    onProfileClick: (String) -> Unit = {},
    statusBarPadding: androidx.compose.ui.unit.Dp = 0.dp,
) {
    val state by store.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PartyGalleryColors.DarkBackground),
    ) {
        // Header
        HomeHeader(topPadding = statusBarPadding)

        // Filter tabs
        FilterTabs(
            selectedFilter = state.selectedFilter,
            onFilterSelected = { store.processIntent(HomeIntent.SelectFilter(it)) },
        )

        // Content
        when {
            state.isLoading -> {
                LoadingContent()
            }
            state.error != null -> {
                ErrorContent(
                    error = state.error!!,
                    onRetry = { store.processIntent(HomeIntent.LoadFeed) },
                )
            }
            state.feedItems.isEmpty() -> {
                EmptyContent()
            }
            else -> {
                FeedContent(
                    items = state.feedItems,
                    isRefreshing = state.isRefreshing,
                    onRefresh = { store.processIntent(HomeIntent.RefreshFeed) },
                    onPartyClick = onPartyClick,
                    onLikeClick = { postId, isLiked ->
                        if (isLiked) {
                            store.processIntent(HomeIntent.UnlikePost(postId))
                        } else {
                            store.processIntent(HomeIntent.LikePost(postId))
                        }
                    },
                    onLoadMore = { store.processIntent(HomeIntent.LoadMore) },
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(topPadding: androidx.compose.ui.unit.Dp = 0.dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = topPadding + PartyGallerySpacing.md,
                start = PartyGallerySpacing.md,
                end = PartyGallerySpacing.md,
                bottom = PartyGallerySpacing.md,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "Party Gallery",
                style = Theme.typography.headlineMedium,
                color = PartyGalleryColors.DarkOnBackground,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Discover the best parties",
                style = Theme.typography.bodyMedium,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
            )
        }
    }
}

@Composable
private fun FilterTabs(selectedFilter: FeedFilter, onFilterSelected: (FeedFilter) -> Unit) {
    val filters = FeedFilter.entries
    val selectedIndex = filters.indexOf(selectedFilter)

    ScrollablePillTabs(
        tabs = filters.map { it.label },
        selectedIndex = selectedIndex,
        onTabSelected = { index -> onFilterSelected(filters[index]) },
    )

    Spacer(modifier = Modifier.height(PartyGallerySpacing.sm))
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = PartyGalleryColors.Primary,
        )
    }
}

@Composable
private fun ErrorContent(error: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = error,
                style = Theme.typography.bodyLarge,
                color = PartyGalleryColors.Error,
            )
            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))
            Text(
                text = "Tap to retry",
                style = Theme.typography.labelLarge,
                color = PartyGalleryColors.Primary,
                modifier = Modifier.clickable(onClick = onRetry),
            )
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "No parties yet",
                style = Theme.typography.headlineSmall,
                color = PartyGalleryColors.DarkOnBackground,
            )
            Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))
            Text(
                text = "Be the first to create one!",
                style = Theme.typography.bodyMedium,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
            )
        }
    }
}

@Composable
private fun FeedContent(
    items: List<FeedItem>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onPartyClick: (String) -> Unit,
    onLikeClick: (String, Boolean) -> Unit,
    onLoadMore: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(PartyGallerySpacing.md),
        verticalArrangement = Arrangement.spacedBy(PartyGallerySpacing.md),
    ) {
        items(
            items = items,
            key = { it.id },
        ) { item ->
            when (item) {
                is FeedItem.PartyCard -> PartyCardItem(
                    item = item,
                    onClick = { onPartyClick(item.id) },
                )
                is FeedItem.MediaPost -> MediaPostItem(
                    item = item,
                    onLikeClick = { onLikeClick(item.id, item.isLiked) },
                    onClick = { onPartyClick(item.partyId) },
                )
            }
        }

        // Load more indicator
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PartyGallerySpacing.md),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = PartyGalleryColors.Primary,
                    strokeWidth = 2.dp,
                )
            }
            // Trigger load more
            onLoadMore()
        }
    }
}

@Composable
private fun PartyCardItem(item: FeedItem.PartyCard, onClick: () -> Unit) {
    FeaturedMediaCard(
        onClick = onClick,
        content = {
            // Placeholder gradient background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                PartyGalleryColors.DarkSurfaceVariant,
                                PartyGalleryColors.DarkSurface,
                            ),
                        ),
                    ),
            )
        },
        overlayContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(PartyGallerySpacing.md),
            ) {
                // Live badge if applicable
                if (item.isLive) {
                    LiveBadge()
                    Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))
                }

                // Title
                Text(
                    text = item.title,
                    style = Theme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))

                // Host info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AvatarWithInitials(
                        name = item.hostName,
                        size = AvatarSize.XS,
                    )
                    Spacer(modifier = Modifier.width(PartyGallerySpacing.xs))
                    Text(
                        text = item.hostName,
                        style = Theme.typography.labelMedium,
                        color = Color.White,
                    )
                }

                Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))

                // Venue & Attendees
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = PartyGalleryColors.DarkOnBackgroundVariant,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.venueName,
                            style = Theme.typography.bodySmall,
                            color = PartyGalleryColors.DarkOnBackgroundVariant,
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = PartyGalleryColors.DarkOnBackgroundVariant,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.attendeesCount} going",
                            style = Theme.typography.bodySmall,
                            color = PartyGalleryColors.DarkOnBackgroundVariant,
                        )
                    }
                }

                // Mood tag
                item.mood?.let { mood ->
                    Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))
                    MoodTag(mood = mood)
                }
            }
        },
    )
}

@Composable
private fun MediaPostItem(item: FeedItem.MediaPost, onLikeClick: () -> Unit, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(PartyGalleryShapes.medium)
            .background(PartyGalleryColors.DarkSurface)
            .clickable(onClick = onClick),
    ) {
        // Header with user info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PartyGallerySpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AvatarWithInitials(
                name = item.userName,
                size = AvatarSize.SMALL,
            )
            Spacer(modifier = Modifier.width(PartyGallerySpacing.sm))
            Column {
                Text(
                    text = item.userName,
                    style = Theme.typography.labelMedium,
                    color = PartyGalleryColors.DarkOnBackground,
                )
                Text(
                    text = "at ${item.partyTitle}",
                    style = Theme.typography.bodySmall,
                    color = PartyGalleryColors.DarkOnBackgroundVariant,
                )
            }
        }

        // Media placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(PartyGalleryColors.DarkSurfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "📷",
                style = Theme.typography.displayMedium,
            )
        }

        // Actions row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PartyGallerySpacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = if (item.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onLikeClick),
                    tint = if (item.isLiked) PartyGalleryColors.Error else PartyGalleryColors.DarkOnBackground,
                )
                Spacer(modifier = Modifier.width(PartyGallerySpacing.xs))
                Text(
                    text = "${item.likesCount}",
                    style = Theme.typography.labelMedium,
                    color = PartyGalleryColors.DarkOnBackground,
                )
                Spacer(modifier = Modifier.width(PartyGallerySpacing.md))
                Text(
                    text = "💬 ${item.commentsCount}",
                    style = Theme.typography.labelMedium,
                    color = PartyGalleryColors.DarkOnBackgroundVariant,
                )
            }

            item.mood?.let { mood ->
                MoodTag(mood = mood)
            }
        }

        // Caption
        item.caption?.let { caption ->
            Text(
                text = caption,
                style = Theme.typography.bodyMedium,
                color = PartyGalleryColors.DarkOnBackground,
                modifier = Modifier.padding(
                    start = PartyGallerySpacing.sm,
                    end = PartyGallerySpacing.sm,
                    bottom = PartyGallerySpacing.sm,
                ),
            )
        }
    }
}
