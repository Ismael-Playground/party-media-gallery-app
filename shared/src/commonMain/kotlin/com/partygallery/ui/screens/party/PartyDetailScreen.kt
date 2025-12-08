package com.partygallery.ui.screens.party

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.partygallery.domain.model.MediaContent
import com.partygallery.domain.model.MediaType
import com.partygallery.domain.model.PartyEvent
import com.partygallery.presentation.intent.PartyDetailIntent
import com.partygallery.presentation.state.MediaFilterOption
import com.partygallery.presentation.store.PartyDetailStore
import com.partygallery.ui.components.AvatarSize
import com.partygallery.ui.components.AvatarWithInitials
import com.partygallery.ui.components.LiveBadge
import com.partygallery.ui.components.MoodTag
import com.partygallery.ui.components.PartyButton
import com.partygallery.ui.components.PartyButtonVariant
import com.partygallery.ui.components.ScrollablePillTabs
import com.partygallery.ui.theme.PartyGalleryColors
import com.partygallery.ui.theme.PartyGalleryShapes
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.Theme

/**
 * Party Detail Screen.
 *
 * S4-004: PartyDetailScreen
 *
 * Shows party information, media gallery, and actions.
 */
@Composable
fun PartyDetailScreen(
    partyId: String,
    store: PartyDetailStore = remember { PartyDetailStore(partyId) },
    onNavigateBack: () -> Unit = {},
    onUploadMedia: (String) -> Unit = {},
    onMediaClick: (String) -> Unit = {},
) {
    val state by store.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PartyGalleryColors.DarkBackground),
    ) {
        when {
            state.isLoading -> LoadingContent()
            state.error != null -> ErrorContent(
                error = state.error!!,
                onRetry = { store.processIntent(PartyDetailIntent.LoadParty(partyId)) },
            )
            state.party != null -> PartyDetailContent(
                party = state.party!!,
                mediaItems = state.mediaItems,
                selectedFilter = state.selectedMediaFilter,
                isUserAttending = state.isUserAttending,
                isRsvpLoading = state.isRsvpLoading,
                isLoadingMedia = state.isLoadingMedia,
                onNavigateBack = onNavigateBack,
                onFilterSelected = { store.processIntent(PartyDetailIntent.SelectMediaFilter(it)) },
                onToggleRsvp = { store.processIntent(PartyDetailIntent.ToggleRsvp) },
                onMediaClick = onMediaClick,
                onLikeMedia = { mediaId, isLiked ->
                    if (isLiked) {
                        store.processIntent(PartyDetailIntent.UnlikeMedia(mediaId))
                    } else {
                        store.processIntent(PartyDetailIntent.LikeMedia(mediaId))
                    }
                },
            )
        }

        // FAB for uploading media
        if (state.party != null) {
            FloatingActionButton(
                onClick = { onUploadMedia(partyId) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(PartyGallerySpacing.lg),
                containerColor = PartyGalleryColors.Primary,
                contentColor = PartyGalleryColors.DarkBackground,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Upload media",
                )
            }
        }
    }
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
private fun PartyDetailContent(
    party: PartyEvent,
    mediaItems: List<MediaContent>,
    selectedFilter: MediaFilterOption,
    isUserAttending: Boolean,
    isRsvpLoading: Boolean,
    isLoadingMedia: Boolean,
    onNavigateBack: () -> Unit,
    onFilterSelected: (MediaFilterOption) -> Unit,
    onToggleRsvp: () -> Unit,
    onMediaClick: (String) -> Unit,
    onLikeMedia: (String, Boolean) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        // Header section (spans all columns)
        item(span = { GridItemSpan(3) }) {
            PartyHeader(
                party = party,
                isUserAttending = isUserAttending,
                isRsvpLoading = isRsvpLoading,
                onNavigateBack = onNavigateBack,
                onToggleRsvp = onToggleRsvp,
            )
        }

        // Filter tabs (spans all columns)
        item(span = { GridItemSpan(3) }) {
            MediaFilterTabs(
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected,
            )
        }

        // Loading indicator
        if (isLoadingMedia) {
            item(span = { GridItemSpan(3) }) {
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
            }
        }

        // Media grid
        items(
            items = mediaItems,
            key = { it.id },
        ) { media ->
            MediaGridItem(
                media = media,
                onClick = { onMediaClick(media.id) },
                onLikeClick = { onLikeMedia(media.id, media.socialMetrics.isLikedByUser) },
            )
        }

        // Empty state
        if (mediaItems.isEmpty() && !isLoadingMedia) {
            item(span = { GridItemSpan(3) }) {
                EmptyMediaContent()
            }
        }
    }
}

@Composable
private fun PartyHeader(
    party: PartyEvent,
    isUserAttending: Boolean,
    isRsvpLoading: Boolean,
    onNavigateBack: () -> Unit,
    onToggleRsvp: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
    ) {
        // Cover image placeholder with gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PartyGalleryColors.DarkSurfaceVariant,
                            PartyGalleryColors.DarkSurface,
                        ),
                    ),
                ),
        )

        // Gradient overlay for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f),
                        ),
                    ),
                ),
        )

        // Back button
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(PartyGallerySpacing.sm),
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
            )
        }

        // Party info at bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(PartyGallerySpacing.md),
        ) {
            // Live badge
            if (party.isLive) {
                LiveBadge()
                Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))
            }

            // Title
            Text(
                text = party.title,
                style = Theme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))

            // Host info
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarWithInitials(
                    name = party.host?.displayName ?: "Host",
                    size = AvatarSize.XS,
                )
                Spacer(modifier = Modifier.width(PartyGallerySpacing.xs))
                Text(
                    text = "Hosted by ${party.host?.displayName ?: "Unknown"}",
                    style = Theme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                )
            }

            Spacer(modifier = Modifier.height(PartyGallerySpacing.sm))

            // Location and attendees
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = PartyGalleryColors.DarkOnBackgroundVariant,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = party.venue.name,
                        style = Theme.typography.bodySmall,
                        color = PartyGalleryColors.DarkOnBackgroundVariant,
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = PartyGalleryColors.DarkOnBackgroundVariant,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${party.attendeesCount} attending",
                        style = Theme.typography.bodySmall,
                        color = PartyGalleryColors.DarkOnBackgroundVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

            // RSVP button
            PartyButton(
                text = when {
                    isRsvpLoading -> "Loading..."
                    isUserAttending -> "Attending"
                    else -> "Join Party"
                },
                onClick = onToggleRsvp,
                enabled = !isRsvpLoading,
                variant = if (isUserAttending) PartyButtonVariant.SECONDARY else PartyButtonVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun MediaFilterTabs(selectedFilter: MediaFilterOption, onFilterSelected: (MediaFilterOption) -> Unit) {
    val filters = MediaFilterOption.entries
    val selectedIndex = filters.indexOf(selectedFilter)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PartyGalleryColors.DarkBackground)
            .padding(vertical = PartyGallerySpacing.sm),
    ) {
        ScrollablePillTabs(
            tabs = filters.map { it.label },
            selectedIndex = selectedIndex,
            onTabSelected = { index -> onFilterSelected(filters[index]) },
        )
    }
}

@Composable
private fun MediaGridItem(media: MediaContent, onClick: () -> Unit, onLikeClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(PartyGalleryShapes.small)
            .background(PartyGalleryColors.DarkSurfaceVariant)
            .clickable(onClick = onClick),
    ) {
        // Media placeholder
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (media.type == MediaType.VIDEO) "🎬" else "📷",
                style = Theme.typography.headlineMedium,
            )
        }

        // Video indicator
        if (media.type == MediaType.VIDEO) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Video",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(20.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = PartyGalleryShapes.small,
                    )
                    .padding(2.dp),
                tint = Color.White,
            )
        }

        // Like button overlay
        IconButton(
            onClick = onLikeClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(32.dp),
        ) {
            Icon(
                imageVector = if (media.socialMetrics.isLikedByUser) {
                    Icons.Filled.Favorite
                } else {
                    Icons.Outlined.FavoriteBorder
                },
                contentDescription = "Like",
                modifier = Modifier.size(18.dp),
                tint = if (media.socialMetrics.isLikedByUser) {
                    PartyGalleryColors.Error
                } else {
                    Color.White
                },
            )
        }

        // Mood tag
        media.mood?.let { mood ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp),
            ) {
                MoodTag(mood = mood, compact = true)
            }
        }
    }
}

@Composable
private fun EmptyMediaContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PartyGallerySpacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "📸",
                style = Theme.typography.displayMedium,
            )
            Spacer(modifier = Modifier.height(PartyGallerySpacing.sm))
            Text(
                text = "No media yet",
                style = Theme.typography.headlineSmall,
                color = PartyGalleryColors.DarkOnBackground,
            )
            Text(
                text = "Be the first to share!",
                style = Theme.typography.bodyMedium,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
            )
        }
    }
}
