package com.partygallery.ui.screens.studio

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.partygallery.presentation.intent.StudioIntent
import com.partygallery.presentation.state.MediaType
import com.partygallery.presentation.state.RecentParty
import com.partygallery.presentation.state.StudioContent
import com.partygallery.presentation.state.StudioDraft
import com.partygallery.presentation.state.StudioTab
import com.partygallery.presentation.store.StudioStore
import com.partygallery.ui.components.LiveBadge
import com.partygallery.ui.components.MoodTag
import com.partygallery.ui.components.ScrollablePillTabs
import com.partygallery.ui.theme.PartyGalleryColors
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.Theme

/**
 * Studio Screen - Content creation and management hub.
 *
 * S3-NEW-004: StudioScreen UI implementation
 *
 * Features:
 * - My Content tab with published photos/videos
 * - Drafts tab for unpublished content
 * - Parties tab for quick upload to recent parties
 * - Create new content FAB
 *
 * Design: Dark Mode First
 * - Background: #0A0A0A
 * - Primary: #F59E0B (Amber)
 */
@Composable
fun StudioScreen(
    onCreateContent: () -> Unit = {},
    onContentClick: (String) -> Unit = {},
) {
    val store = remember { StudioStore() }
    val state by store.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PartyGalleryColors.DarkBackground),
    ) {
        // Header
        StudioHeader(
            onCreateClick = { store.processIntent(StudioIntent.StartCreateContent) },
        )

        // Tabs
        StudioTabs(
            selectedTab = state.selectedTab,
            onTabSelected = { tab -> store.processIntent(StudioIntent.SelectTab(tab)) },
        )

        // Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color = PartyGalleryColors.Primary,
                        )
                    }
                }
                else -> {
                    when (state.selectedTab) {
                        StudioTab.MyContent -> MyContentGrid(
                            content = state.myContent,
                            onContentClick = onContentClick,
                            onDeleteClick = { id -> store.processIntent(StudioIntent.DeleteContent(id)) },
                        )
                        StudioTab.Drafts -> DraftsContent(
                            drafts = state.drafts,
                            onPublishClick = { id -> store.processIntent(StudioIntent.PublishDraft(id)) },
                            onDeleteClick = { id -> store.processIntent(StudioIntent.DeleteDraft(id)) },
                        )
                        StudioTab.Parties -> RecentPartiesContent(
                            parties = state.recentParties,
                            onPartyClick = { id -> store.processIntent(StudioIntent.CreateContentForParty(id)) },
                        )
                    }
                }
            }
        }
    }
}

/**
 * Studio header with title and create button.
 */
@Composable
private fun StudioHeader(
    onCreateClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PartyGallerySpacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "Studio",
                style = Theme.typography.headlineMedium,
                color = PartyGalleryColors.DarkOnBackground,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Create and manage your content",
                style = Theme.typography.bodySmall,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(PartyGalleryColors.Primary)
                .clickable(onClick = onCreateClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Create content",
                tint = PartyGalleryColors.DarkBackground,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

/**
 * Studio tabs for navigation.
 */
@Composable
private fun StudioTabs(
    selectedTab: StudioTab,
    onTabSelected: (StudioTab) -> Unit,
) {
    val tabs = StudioTab.entries.map { it.label }
    val selectedIndex = StudioTab.entries.indexOf(selectedTab)

    ScrollablePillTabs(
        tabs = tabs,
        selectedIndex = selectedIndex,
        onTabSelected = { index ->
            onTabSelected(StudioTab.entries[index])
        },
        modifier = Modifier.padding(horizontal = PartyGallerySpacing.md),
    )

    Spacer(modifier = Modifier.height(PartyGallerySpacing.sm))
}

/**
 * Grid display of user's published content.
 */
@Composable
private fun MyContentGrid(
    content: List<StudioContent>,
    onContentClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
) {
    if (content.isEmpty()) {
        EmptyStateView(
            icon = Icons.Filled.Star,
            title = "No content yet",
            subtitle = "Start creating to see your content here",
        )
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(PartyGallerySpacing.md),
            horizontalArrangement = Arrangement.spacedBy(PartyGallerySpacing.sm),
            verticalArrangement = Arrangement.spacedBy(PartyGallerySpacing.sm),
        ) {
            items(content) { item ->
                ContentGridItem(
                    content = item,
                    onClick = { onContentClick(item.id) },
                    onDeleteClick = { onDeleteClick(item.id) },
                )
            }
        }
    }
}

/**
 * Single content item in the grid.
 */
@Composable
private fun ContentGridItem(
    content: StudioContent,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(PartyGalleryColors.DarkSurface)
            .clickable(onClick = onClick),
    ) {
        // Placeholder for media thumbnail
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PartyGalleryColors.DarkSurfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (content.mediaType == MediaType.VIDEO) Icons.Filled.PlayArrow else Icons.Filled.Star,
                contentDescription = null,
                tint = PartyGalleryColors.DarkOnBackgroundVariant,
                modifier = Modifier.size(32.dp),
            )
        }

        // Video indicator
        if (content.mediaType == MediaType.VIDEO) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(PartyGallerySpacing.xs)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Video",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp),
                )
            }
        }

        // Stats overlay at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                    ),
                )
                .padding(PartyGallerySpacing.xs),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PartyGallerySpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp),
                    )
                    Text(
                        text = "${content.likesCount}",
                        style = Theme.typography.labelSmall,
                        color = Color.White,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp),
                    )
                    Text(
                        text = "${content.viewsCount}",
                        style = Theme.typography.labelSmall,
                        color = Color.White,
                    )
                }
            }
        }

        // Mood tag
        content.mood?.let { mood ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(PartyGallerySpacing.xs),
            ) {
                MoodTag(mood = mood)
            }
        }
    }
}

/**
 * List of draft content.
 */
@Composable
private fun DraftsContent(
    drafts: List<StudioDraft>,
    onPublishClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
) {
    if (drafts.isEmpty()) {
        EmptyStateView(
            icon = Icons.Filled.Refresh,
            title = "No drafts",
            subtitle = "Your unpublished content will appear here",
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(PartyGallerySpacing.md),
            verticalArrangement = Arrangement.spacedBy(PartyGallerySpacing.sm),
        ) {
            items(drafts) { draft ->
                DraftItem(
                    draft = draft,
                    onPublishClick = { onPublishClick(draft.id) },
                    onDeleteClick = { onDeleteClick(draft.id) },
                )
            }
        }
    }
}

/**
 * Single draft item.
 */
@Composable
private fun DraftItem(
    draft: StudioDraft,
    onPublishClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PartyGalleryColors.DarkSurface)
            .padding(PartyGallerySpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Thumbnail placeholder
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PartyGalleryColors.DarkSurfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (draft.mediaType == MediaType.VIDEO) Icons.Filled.PlayArrow else Icons.Filled.Star,
                contentDescription = null,
                tint = PartyGalleryColors.DarkOnBackgroundVariant,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(modifier = Modifier.width(PartyGallerySpacing.sm))

        // Info
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = draft.partyTitle ?: "Unassigned",
                style = Theme.typography.bodyMedium,
                color = PartyGalleryColors.DarkOnBackground,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            draft.caption?.let { caption ->
                Text(
                    text = caption,
                    style = Theme.typography.bodySmall,
                    color = PartyGalleryColors.DarkOnBackgroundVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            draft.mood?.let { mood ->
                Spacer(modifier = Modifier.height(4.dp))
                MoodTag(mood = mood)
            }
        }

        // Actions
        Row(
            horizontalArrangement = Arrangement.spacedBy(PartyGallerySpacing.xs),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PartyGalleryColors.Primary)
                    .clickable(onClick = onPublishClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Publish",
                    tint = PartyGalleryColors.DarkBackground,
                    modifier = Modifier.size(18.dp),
                )
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PartyGalleryColors.DarkSurfaceVariant)
                    .clickable(onClick = onDeleteClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Delete",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

/**
 * List of recent parties for quick content upload.
 */
@Composable
private fun RecentPartiesContent(
    parties: List<RecentParty>,
    onPartyClick: (String) -> Unit,
) {
    if (parties.isEmpty()) {
        EmptyStateView(
            icon = Icons.Filled.Star,
            title = "No recent parties",
            subtitle = "Join a party to start uploading content",
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(PartyGallerySpacing.md),
            verticalArrangement = Arrangement.spacedBy(PartyGallerySpacing.sm),
        ) {
            items(parties) { party ->
                RecentPartyItem(
                    party = party,
                    onClick = { onPartyClick(party.id) },
                )
            }
        }
    }
}

/**
 * Single recent party item.
 */
@Composable
private fun RecentPartyItem(
    party: RecentParty,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PartyGalleryColors.DarkSurface)
            .clickable(onClick = onClick)
            .padding(PartyGallerySpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Cover placeholder
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PartyGalleryColors.DarkSurfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            party.mood?.let { mood ->
                Text(
                    text = when (mood.name) {
                        "HYPE" -> "🔥"
                        "CHILL" -> "😎"
                        "WILD" -> "🤪"
                        "ROMANTIC" -> "💕"
                        "CRAZY" -> "🎉"
                        "ELEGANT" -> "✨"
                        else -> "🎉"
                    },
                    style = Theme.typography.headlineMedium,
                )
            }
        }

        Spacer(modifier = Modifier.width(PartyGallerySpacing.sm))

        // Info
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PartyGallerySpacing.xs),
            ) {
                Text(
                    text = party.title,
                    style = Theme.typography.bodyMedium,
                    color = PartyGalleryColors.DarkOnBackground,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (party.isLive) {
                    LiveBadge()
                }
            }
            Text(
                text = "by ${party.hostName}",
                style = Theme.typography.bodySmall,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
            )
            Text(
                text = "${party.myContentCount} uploads",
                style = Theme.typography.labelSmall,
                color = PartyGalleryColors.Primary,
            )
        }

        // Upload button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PartyGalleryColors.Primary)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Upload to party",
                tint = PartyGalleryColors.DarkBackground,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/**
 * Empty state view for tabs with no content.
 */
@Composable
private fun EmptyStateView(
    icon: ImageVector,
    title: String,
    subtitle: String,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PartyGalleryColors.DarkOnBackgroundVariant,
                modifier = Modifier.size(64.dp),
            )
            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))
            Text(
                text = title,
                style = Theme.typography.titleMedium,
                color = PartyGalleryColors.DarkOnBackground,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))
            Text(
                text = subtitle,
                style = Theme.typography.bodySmall,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
            )
        }
    }
}
