package com.partygallery.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.partygallery.presentation.intent.FavoritesIntent
import com.partygallery.presentation.state.FavoritesState
import com.partygallery.presentation.state.FavoritesTab
import com.partygallery.presentation.state.MediaType
import com.partygallery.presentation.state.SavedMedia
import com.partygallery.presentation.state.SavedParty
import com.partygallery.presentation.state.SuggestedParty
import com.partygallery.presentation.store.FavoritesStore
import com.partygallery.ui.components.AvatarSize
import com.partygallery.ui.components.AvatarWithInitials
import com.partygallery.ui.components.LiveBadge
import com.partygallery.ui.components.MoodTag
import com.partygallery.ui.components.ScrollablePillTabs
import com.partygallery.ui.theme.PartyGalleryColors
import com.partygallery.ui.theme.PartyGalleryShapes
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.Theme

/**
 * Favorites Screen.
 *
 * S3-NEW-002: FavoritesScreen UI
 *
 * Shows saved parties, saved media, and personalized suggestions.
 * Design: Dark Mode First
 */
@Composable
fun FavoritesScreen(
    store: FavoritesStore = remember { FavoritesStore() },
    onPartyClick: (String) -> Unit = {},
    onMediaClick: (String) -> Unit = {},
    statusBarPadding: androidx.compose.ui.unit.Dp = 0.dp,
) {
    val state by store.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PartyGalleryColors.DarkBackground),
    ) {
        // Header
        FavoritesHeader(topPadding = statusBarPadding)

        // Filter tabs
        FavoritesTabs(
            selectedTab = state.selectedTab,
            onTabSelected = { store.processIntent(FavoritesIntent.SelectTab(it)) },
        )

        // Content
        when {
            state.isLoading -> LoadingContent()
            state.error != null -> ErrorContent(
                error = state.error!!,
                onRetry = { store.processIntent(FavoritesIntent.LoadFavorites) },
            )
            else -> FavoritesContent(
                state = state,
                onPartyClick = onPartyClick,
                onMediaClick = onMediaClick,
                onRemoveFavorite = { store.processIntent(FavoritesIntent.RemoveFromFavorites(it)) },
            )
        }
    }
}

@Composable
private fun FavoritesHeader(topPadding: androidx.compose.ui.unit.Dp = 0.dp) {
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
                text = "Favorites",
                style = Theme.typography.headlineMedium,
                color = PartyGalleryColors.DarkOnBackground,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Your saved parties & moments",
                style = Theme.typography.bodyMedium,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
            )
        }
        Icon(
            imageVector = Icons.Default.Bookmark,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = PartyGalleryColors.Primary,
        )
    }
}

@Composable
private fun FavoritesTabs(selectedTab: FavoritesTab, onTabSelected: (FavoritesTab) -> Unit) {
    val tabs = FavoritesTab.entries
    val selectedIndex = tabs.indexOf(selectedTab)

    ScrollablePillTabs(
        tabs = tabs.map { it.label },
        selectedIndex = selectedIndex,
        onTabSelected = { index -> onTabSelected(tabs[index]) },
    )

    Spacer(modifier = Modifier.height(PartyGallerySpacing.sm))
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = PartyGalleryColors.Primary)
    }
}

@Composable
private fun ErrorContent(error: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
private fun FavoritesContent(
    state: FavoritesState,
    onPartyClick: (String) -> Unit,
    onMediaClick: (String) -> Unit,
    onRemoveFavorite: (String) -> Unit,
) {
    when (state.selectedTab) {
        FavoritesTab.All -> AllFavoritesContent(
            state = state,
            onPartyClick = onPartyClick,
            onMediaClick = onMediaClick,
        )
        FavoritesTab.Parties -> PartiesContent(
            parties = state.savedParties,
            onPartyClick = onPartyClick,
            onRemove = onRemoveFavorite,
        )
        FavoritesTab.Media -> MediaGridContent(
            media = state.savedMedia,
            onMediaClick = onMediaClick,
        )
        FavoritesTab.Suggestions -> SuggestionsContent(
            suggestions = state.suggestedParties,
            onPartyClick = onPartyClick,
        )
    }
}

@Composable
private fun AllFavoritesContent(
    state: FavoritesState,
    onPartyClick: (String) -> Unit,
    onMediaClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = PartyGallerySpacing.sm),
    ) {
        // Suggestions section
        if (state.suggestedParties.isNotEmpty()) {
            item {
                SectionHeader(title = "For You", subtitle = "Based on your interests")
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = PartyGallerySpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(PartyGallerySpacing.sm),
                ) {
                    items(state.suggestedParties) { suggestion ->
                        SuggestionCard(
                            suggestion = suggestion,
                            onClick = { onPartyClick(suggestion.id) },
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(PartyGallerySpacing.lg)) }
        }

        // Saved parties section
        if (state.savedParties.isNotEmpty()) {
            item {
                SectionHeader(title = "Saved Parties", subtitle = "${state.savedParties.size} saved")
            }
            items(state.savedParties.take(3)) { party ->
                SavedPartyCard(
                    party = party,
                    onClick = { onPartyClick(party.id) },
                )
            }
            item { Spacer(modifier = Modifier.height(PartyGallerySpacing.lg)) }
        }

        // Saved media section
        if (state.savedMedia.isNotEmpty()) {
            item {
                SectionHeader(title = "Saved Media", subtitle = "${state.savedMedia.size} items")
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = PartyGallerySpacing.md),
                    horizontalArrangement = Arrangement.spacedBy(PartyGallerySpacing.sm),
                ) {
                    items(state.savedMedia) { media ->
                        SavedMediaThumbnail(
                            media = media,
                            onClick = { onMediaClick(media.id) },
                        )
                    }
                }
            }
        }

        // Empty state
        if (state.savedParties.isEmpty() && state.savedMedia.isEmpty() && state.suggestedParties.isEmpty()) {
            item { EmptyFavoritesContent() }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = PartyGallerySpacing.md,
                vertical = PartyGallerySpacing.sm,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = title,
                style = Theme.typography.titleMedium,
                color = PartyGalleryColors.DarkOnBackground,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                style = Theme.typography.bodySmall,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
            )
        }
        Text(
            text = "See all",
            style = Theme.typography.labelMedium,
            color = PartyGalleryColors.Primary,
        )
    }
}

@Composable
private fun SuggestionCard(suggestion: SuggestedParty, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .clip(PartyGalleryShapes.medium)
            .background(PartyGalleryColors.DarkSurface)
            .clickable(onClick = onClick),
    ) {
        // Cover placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(PartyGalleryColors.DarkSurfaceVariant),
            contentAlignment = Alignment.TopEnd,
        ) {
            if (suggestion.isLive) {
                Box(modifier = Modifier.padding(PartyGallerySpacing.xs)) {
                    LiveBadge()
                }
            }
            // Match score badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(PartyGallerySpacing.xs)
                    .background(
                        PartyGalleryColors.Primary.copy(alpha = 0.9f),
                        PartyGalleryShapes.small,
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.White,
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${(suggestion.matchScore * 100).toInt()}%",
                        style = Theme.typography.labelSmall,
                        color = Color.White,
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(PartyGallerySpacing.sm),
        ) {
            Text(
                text = suggestion.title,
                style = Theme.typography.labelMedium,
                color = PartyGalleryColors.DarkOnBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = suggestion.venueName,
                style = Theme.typography.bodySmall,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            suggestion.mood?.let { mood ->
                Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))
                MoodTag(mood = mood)
            }
        }
    }
}

@Composable
private fun SavedPartyCard(party: SavedParty, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PartyGallerySpacing.md, vertical = PartyGallerySpacing.xs)
            .clip(PartyGalleryShapes.medium)
            .background(PartyGalleryColors.DarkSurface)
            .clickable(onClick = onClick)
            .padding(PartyGallerySpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Cover placeholder
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(PartyGalleryShapes.small)
                .background(PartyGalleryColors.DarkSurfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "🎉", style = Theme.typography.headlineSmall)
        }

        Spacer(modifier = Modifier.width(PartyGallerySpacing.sm))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = party.title,
                style = Theme.typography.titleSmall,
                color = PartyGalleryColors.DarkOnBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarWithInitials(
                    name = party.hostName,
                    size = AvatarSize.XS,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = party.hostName,
                    style = Theme.typography.bodySmall,
                    color = PartyGalleryColors.DarkOnBackgroundVariant,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = PartyGalleryColors.DarkOnBackgroundVariant,
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = party.venueName,
                    style = Theme.typography.bodySmall,
                    color = PartyGalleryColors.DarkOnBackgroundVariant,
                )
            }
        }

        party.mood?.let { mood ->
            MoodTag(mood = mood)
        }
    }
}

@Composable
private fun SavedMediaThumbnail(media: SavedMedia, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(PartyGalleryShapes.medium)
            .background(PartyGalleryColors.DarkSurfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (media.mediaType == MediaType.VIDEO) "🎬" else "📷",
            style = Theme.typography.headlineMedium,
        )
        // Mood tag overlay
        media.mood?.let { mood ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp),
            ) {
                MoodTag(mood = mood)
            }
        }
    }
}

@Composable
private fun PartiesContent(
    parties: List<SavedParty>,
    onPartyClick: (String) -> Unit,
    onRemove: (String) -> Unit,
) {
    if (parties.isEmpty()) {
        EmptyStateContent(
            icon = "📌",
            title = "No saved parties",
            subtitle = "Save parties to see them here",
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = PartyGallerySpacing.sm),
        ) {
            items(parties) { party ->
                SavedPartyCard(
                    party = party,
                    onClick = { onPartyClick(party.id) },
                )
            }
        }
    }
}

@Composable
private fun MediaGridContent(
    media: List<SavedMedia>,
    onMediaClick: (String) -> Unit,
) {
    if (media.isEmpty()) {
        EmptyStateContent(
            icon = "🖼️",
            title = "No saved media",
            subtitle = "Save photos and videos to see them here",
        )
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(PartyGallerySpacing.md),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(media) { item ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(PartyGalleryShapes.small)
                        .background(PartyGalleryColors.DarkSurfaceVariant)
                        .clickable { onMediaClick(item.id) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (item.mediaType == MediaType.VIDEO) "🎬" else "📷",
                        style = Theme.typography.titleLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionsContent(
    suggestions: List<SuggestedParty>,
    onPartyClick: (String) -> Unit,
) {
    if (suggestions.isEmpty()) {
        EmptyStateContent(
            icon = "✨",
            title = "No suggestions yet",
            subtitle = "We're learning your preferences",
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(PartyGallerySpacing.md),
            verticalArrangement = Arrangement.spacedBy(PartyGallerySpacing.sm),
        ) {
            items(suggestions) { suggestion ->
                SuggestionListCard(
                    suggestion = suggestion,
                    onClick = { onPartyClick(suggestion.id) },
                )
            }
        }
    }
}

@Composable
private fun SuggestionListCard(suggestion: SuggestedParty, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(PartyGalleryShapes.medium)
            .background(PartyGalleryColors.DarkSurface)
            .clickable(onClick = onClick),
    ) {
        // Cover placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(PartyGalleryColors.DarkSurfaceVariant),
            contentAlignment = Alignment.TopEnd,
        ) {
            Row(
                modifier = Modifier.padding(PartyGallerySpacing.sm),
                horizontalArrangement = Arrangement.spacedBy(PartyGallerySpacing.xs),
            ) {
                if (suggestion.isLive) LiveBadge()
                Box(
                    modifier = Modifier
                        .background(PartyGalleryColors.Primary, PartyGalleryShapes.small)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.White,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${(suggestion.matchScore * 100).toInt()}% match",
                            style = Theme.typography.labelSmall,
                            color = Color.White,
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(PartyGallerySpacing.md)) {
            Text(
                text = suggestion.title,
                style = Theme.typography.titleMedium,
                color = PartyGalleryColors.DarkOnBackground,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarWithInitials(name = suggestion.hostName, size = AvatarSize.XS)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = suggestion.hostName,
                    style = Theme.typography.bodySmall,
                    color = PartyGalleryColors.DarkOnBackgroundVariant,
                )
                Spacer(modifier = Modifier.width(PartyGallerySpacing.md))
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = PartyGalleryColors.DarkOnBackgroundVariant,
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = suggestion.venueName,
                    style = Theme.typography.bodySmall,
                    color = PartyGalleryColors.DarkOnBackgroundVariant,
                )
            }
            Spacer(modifier = Modifier.height(PartyGallerySpacing.sm))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row {
                    suggestion.matchReasons.forEach { reason ->
                        Box(
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .background(
                                    PartyGalleryColors.DarkSurfaceVariant,
                                    PartyGalleryShapes.small,
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = reason,
                                style = Theme.typography.labelSmall,
                                color = PartyGalleryColors.DarkOnBackgroundVariant,
                            )
                        }
                    }
                }
                suggestion.mood?.let { MoodTag(mood = it) }
            }
        }
    }
}

@Composable
private fun EmptyFavoritesContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "💝", style = Theme.typography.displayLarge)
            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))
            Text(
                text = "No favorites yet",
                style = Theme.typography.headlineSmall,
                color = PartyGalleryColors.DarkOnBackground,
            )
            Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))
            Text(
                text = "Save parties and media to see them here",
                style = Theme.typography.bodyMedium,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
            )
        }
    }
}

@Composable
private fun EmptyStateContent(icon: String, title: String, subtitle: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = icon, style = Theme.typography.displayLarge)
            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))
            Text(
                text = title,
                style = Theme.typography.headlineSmall,
                color = PartyGalleryColors.DarkOnBackground,
            )
            Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))
            Text(
                text = subtitle,
                style = Theme.typography.bodyMedium,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
            )
        }
    }
}
