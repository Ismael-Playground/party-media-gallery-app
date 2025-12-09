package com.partygallery.ui.screens.favorites

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.unit.dp
import com.partygallery.presentation.intent.FavoritesIntent
import com.partygallery.presentation.state.FavoriteMedia
import com.partygallery.presentation.state.FavoriteParty
import com.partygallery.presentation.state.FavoritesTab
import com.partygallery.presentation.state.MediaType
import com.partygallery.presentation.state.SuggestedParty
import com.partygallery.presentation.store.FavoritesStore
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
 * Favorites Screen.
 *
 * S3-NEW-002: FavoritesScreen UI
 *
 * Features:
 * - Tab navigation: Parties, Media, For You
 * - Saved parties list
 * - Liked media grid
 * - Suggested parties based on preferences
 *
 * Design: Dark Mode First
 * - Background: #0A0A0A
 * - Primary: #F59E0B (Amber)
 */
@Composable
fun FavoritesScreen(
    store: FavoritesStore = remember { FavoritesStore() },
    onPartyClick: (String) -> Unit = {},
    onMediaClick: (String) -> Unit = {},
) {
    val state by store.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PartyGalleryColors.DarkBackground),
    ) {
        // Header
        FavoritesHeader()

        // Tab navigation
        FavoritesTabs(
            selectedTab = state.selectedTab,
            onTabSelected = { store.processIntent(FavoritesIntent.SelectTab(it)) },
        )

        // Content based on selected tab
        when {
            state.isLoading -> LoadingContent()
            state.error != null -> ErrorContent(
                error = state.error!!,
                onRetry = { store.processIntent(FavoritesIntent.LoadFavorites) },
            )
            else -> {
                when (state.selectedTab) {
                    FavoritesTab.Parties -> SavedPartiesContent(
                        parties = state.savedParties,
                        onPartyClick = onPartyClick,
                        onUnsave = { store.processIntent(FavoritesIntent.UnsaveParty(it)) },
                    )
                    FavoritesTab.Media -> LikedMediaContent(
                        media = state.likedMedia,
                        onMediaClick = onMediaClick,
                        onUnlike = { store.processIntent(FavoritesIntent.UnlikeMedia(it)) },
                    )
                    FavoritesTab.Suggested -> SuggestedPartiesContent(
                        parties = state.suggestedParties,
                        onPartyClick = onPartyClick,
                        onSave = { store.processIntent(FavoritesIntent.SaveSuggestedParty(it)) },
                        onDismiss = { store.processIntent(FavoritesIntent.DismissSuggestion(it)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritesHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PartyGallerySpacing.md),
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
                text = "Your saved parties and content",
                style = Theme.typography.bodyMedium,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
            )
        }
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = PartyGalleryColors.Primary,
            modifier = Modifier.size(28.dp),
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

// ============================================
// Saved Parties Tab
// ============================================

@Composable
private fun SavedPartiesContent(
    parties: List<FavoriteParty>,
    onPartyClick: (String) -> Unit,
    onUnsave: (String) -> Unit,
) {
    if (parties.isEmpty()) {
        EmptyStateContent(
            icon = "⭐",
            title = "No saved parties",
            subtitle = "Save parties to see them here",
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(PartyGallerySpacing.md),
            verticalArrangement = Arrangement.spacedBy(PartyGallerySpacing.md),
        ) {
            items(parties, key = { it.id }) { party ->
                SavedPartyCard(
                    party = party,
                    onClick = { onPartyClick(party.id) },
                    onUnsave = { onUnsave(party.id) },
                )
            }
        }
    }
}

@Composable
private fun SavedPartyCard(party: FavoriteParty, onClick: () -> Unit, onUnsave: () -> Unit) {
    FeaturedMediaCard(
        onClick = onClick,
        modifier = Modifier.height(180.dp),
        content = {
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
            // Unsave button
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove from favorites",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(PartyGallerySpacing.sm)
                    .size(24.dp)
                    .clickable(onClick = onUnsave),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(PartyGallerySpacing.md),
            ) {
                if (party.isLive) {
                    LiveBadge()
                    Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))
                }

                Text(
                    text = party.title,
                    style = Theme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarWithInitials(name = party.hostName, size = AvatarSize.XS)
                    Spacer(modifier = Modifier.width(PartyGallerySpacing.xs))
                    Text(
                        text = party.hostName,
                        style = Theme.typography.labelMedium,
                        color = Color.White,
                    )
                }

                Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))

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
                            text = party.venueName,
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
                            text = "${party.attendeesCount} going",
                            style = Theme.typography.bodySmall,
                            color = PartyGalleryColors.DarkOnBackgroundVariant,
                        )
                    }
                }

                party.mood?.let { mood ->
                    Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))
                    MoodTag(mood = mood)
                }
            }
        },
    )
}

// ============================================
// Liked Media Tab
// ============================================

@Composable
private fun LikedMediaContent(media: List<FavoriteMedia>, onMediaClick: (String) -> Unit, onUnlike: (String) -> Unit) {
    if (media.isEmpty()) {
        EmptyStateContent(
            icon = "❤️",
            title = "No liked media",
            subtitle = "Like photos and videos to see them here",
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(PartyGallerySpacing.md),
            verticalArrangement = Arrangement.spacedBy(PartyGallerySpacing.md),
        ) {
            items(media, key = { it.id }) { item ->
                LikedMediaCard(
                    media = item,
                    onClick = { onMediaClick(item.id) },
                    onUnlike = { onUnlike(item.id) },
                )
            }
        }
    }
}

@Composable
private fun LikedMediaCard(media: FavoriteMedia, onClick: () -> Unit, onUnlike: () -> Unit) {
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
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarWithInitials(name = media.userName, size = AvatarSize.SMALL)
                Spacer(modifier = Modifier.width(PartyGallerySpacing.sm))
                Column {
                    Text(
                        text = media.userName,
                        style = Theme.typography.labelMedium,
                        color = PartyGalleryColors.DarkOnBackground,
                    )
                    Text(
                        text = "at ${media.partyTitle}",
                        style = Theme.typography.bodySmall,
                        color = PartyGalleryColors.DarkOnBackgroundVariant,
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Unlike",
                tint = PartyGalleryColors.DarkOnBackgroundVariant,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onUnlike),
            )
        }

        // Media placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(PartyGalleryColors.DarkSurfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (media.mediaType == MediaType.VIDEO) "🎬" else "📷",
                style = Theme.typography.displayMedium,
            )
        }

        // Info row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PartyGallerySpacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "❤️ ${media.likesCount}",
                style = Theme.typography.labelMedium,
                color = PartyGalleryColors.DarkOnBackground,
            )
            media.mood?.let { mood ->
                MoodTag(mood = mood)
            }
        }
    }
}

// ============================================
// Suggested Parties Tab
// ============================================

@Composable
private fun SuggestedPartiesContent(
    parties: List<SuggestedParty>,
    onPartyClick: (String) -> Unit,
    onSave: (String) -> Unit,
    onDismiss: (String) -> Unit,
) {
    if (parties.isEmpty()) {
        EmptyStateContent(
            icon = "✨",
            title = "No suggestions yet",
            subtitle = "We're learning your preferences",
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(PartyGallerySpacing.md),
            verticalArrangement = Arrangement.spacedBy(PartyGallerySpacing.md),
        ) {
            items(parties, key = { it.id }) { party ->
                SuggestedPartyCard(
                    party = party,
                    onClick = { onPartyClick(party.id) },
                    onSave = { onSave(party.id) },
                    onDismiss = { onDismiss(party.id) },
                )
            }
        }
    }
}

@Composable
private fun SuggestedPartyCard(party: SuggestedParty, onClick: () -> Unit, onSave: () -> Unit, onDismiss: () -> Unit) {
    FeaturedMediaCard(
        onClick = onClick,
        modifier = Modifier.height(200.dp),
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                PartyGalleryColors.Primary.copy(alpha = 0.3f),
                                PartyGalleryColors.DarkSurface,
                            ),
                        ),
                    ),
            )
        },
        overlayContent = {
            // Action buttons
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(PartyGallerySpacing.sm),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Save",
                    tint = PartyGalleryColors.Primary,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable(onClick = onSave),
                )
                Spacer(modifier = Modifier.width(PartyGallerySpacing.sm))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onDismiss),
                )
            }

            // Match reason badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(PartyGallerySpacing.sm)
                    .background(
                        PartyGalleryColors.Primary,
                        PartyGalleryShapes.small,
                    )
                    .padding(horizontal = PartyGallerySpacing.sm, vertical = 4.dp),
            ) {
                Text(
                    text = "${party.matchScore}% match",
                    style = Theme.typography.labelSmall,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(PartyGallerySpacing.md),
            ) {
                if (party.isLive) {
                    LiveBadge()
                    Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))
                }

                Text(
                    text = party.title,
                    style = Theme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))

                Text(
                    text = party.matchReason,
                    style = Theme.typography.bodySmall,
                    color = PartyGalleryColors.Primary,
                )

                Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))

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
                            text = party.venueName,
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
                            text = "${party.attendeesCount} going",
                            style = Theme.typography.bodySmall,
                            color = PartyGalleryColors.DarkOnBackgroundVariant,
                        )
                    }
                }

                party.mood?.let { mood ->
                    Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))
                    MoodTag(mood = mood)
                }
            }
        },
    )
}

// ============================================
// Empty State
// ============================================

@Composable
private fun EmptyStateContent(icon: String, title: String, subtitle: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = icon,
                style = Theme.typography.displayLarge,
            )
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
