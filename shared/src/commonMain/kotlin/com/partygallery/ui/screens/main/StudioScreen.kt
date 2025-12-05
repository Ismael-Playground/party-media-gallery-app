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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.PartyMode
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.partygallery.presentation.intent.StudioIntent
import com.partygallery.presentation.state.ContentDraft
import com.partygallery.presentation.state.MediaType
import com.partygallery.presentation.state.MyContent
import com.partygallery.presentation.state.MyParty
import com.partygallery.presentation.state.PartyStatus
import com.partygallery.presentation.state.StudioState
import com.partygallery.presentation.state.StudioStats
import com.partygallery.presentation.state.StudioTab
import com.partygallery.presentation.store.StudioStore
import com.partygallery.ui.components.LiveBadge
import com.partygallery.ui.components.MoodTag
import com.partygallery.ui.components.ScrollablePillTabs
import com.partygallery.ui.theme.PartyGalleryColors
import com.partygallery.ui.theme.PartyGalleryShapes
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.Theme

/**
 * Studio Screen.
 *
 * S3-NEW-004: StudioScreen UI
 *
 * The creative hub for content creation and management.
 * Design: Dark Mode First
 */
@Composable
fun StudioScreen(
    store: StudioStore = remember { StudioStore() },
    onCreateParty: () -> Unit = {},
    onOpenCamera: () -> Unit = {},
    onOpenGallery: () -> Unit = {},
    onPartyClick: (String) -> Unit = {},
    onContentClick: (String) -> Unit = {},
    statusBarPadding: androidx.compose.ui.unit.Dp = 0.dp,
) {
    val state by store.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PartyGalleryColors.DarkBackground),
        ) {
            // Header
            StudioHeader(topPadding = statusBarPadding)

            // Stats bar
            StatsBar(stats = state.stats)

            // Tabs
            StudioTabs(
                selectedTab = state.selectedTab,
                onTabSelected = { store.processIntent(StudioIntent.SelectTab(it)) },
            )

            // Content
            when {
                state.isLoading -> LoadingContent()
                state.error != null -> ErrorContent(
                    error = state.error!!,
                    onRetry = { store.processIntent(StudioIntent.LoadStudio) },
                )
                else -> StudioContent(
                    state = state,
                    onDraftClick = { store.processIntent(StudioIntent.EditDraft(it)) },
                    onDraftDelete = { store.processIntent(StudioIntent.DeleteDraft(it)) },
                    onDraftPublish = { store.processIntent(StudioIntent.PublishDraft(it)) },
                    onContentClick = onContentClick,
                    onContentDelete = { store.processIntent(StudioIntent.DeleteContent(it)) },
                    onPartyClick = onPartyClick,
                    onPartyEdit = { store.processIntent(StudioIntent.EditParty(it)) },
                    onOpenCamera = onOpenCamera,
                    onOpenGallery = onOpenGallery,
                    onCreateParty = onCreateParty,
                )
            }
        }

        // FAB
        if (!state.isLoading && state.selectedTab == StudioTab.Create) {
            FloatingActionButton(
                onClick = onCreateParty,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(PartyGallerySpacing.lg),
                containerColor = PartyGalleryColors.Primary,
                contentColor = Color.White,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create",
                )
            }
        }
    }
}

@Composable
private fun StudioHeader(topPadding: androidx.compose.ui.unit.Dp = 0.dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = topPadding + PartyGallerySpacing.md,
                start = PartyGallerySpacing.md,
                end = PartyGallerySpacing.md,
                bottom = PartyGallerySpacing.sm,
            ),
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
                text = "Create & manage content",
                style = Theme.typography.bodyMedium,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
            )
        }
        Icon(
            imageVector = Icons.Default.PartyMode,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = PartyGalleryColors.Primary,
        )
    }
}

@Composable
private fun StatsBar(stats: StudioStats) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PartyGallerySpacing.md, vertical = PartyGallerySpacing.sm),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        StatItem(
            icon = Icons.Default.Visibility,
            value = formatNumber(stats.totalViews),
            label = "Views",
        )
        StatItem(
            icon = Icons.Default.Favorite,
            value = formatNumber(stats.totalLikes),
            label = "Likes",
        )
        StatItem(
            icon = Icons.Default.Image,
            value = stats.totalContent.toString(),
            label = "Content",
        )
        StatItem(
            icon = Icons.Default.PartyMode,
            value = stats.totalParties.toString(),
            label = "Parties",
        )
    }
}

@Composable
private fun StatItem(icon: ImageVector, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = PartyGalleryColors.Primary,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = Theme.typography.titleMedium,
            color = PartyGalleryColors.DarkOnBackground,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = Theme.typography.labelSmall,
            color = PartyGalleryColors.DarkOnBackgroundVariant,
        )
    }
}

@Composable
private fun StudioTabs(selectedTab: StudioTab, onTabSelected: (StudioTab) -> Unit) {
    val tabs = StudioTab.entries
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
private fun StudioContent(
    state: StudioState,
    onDraftClick: (String) -> Unit,
    onDraftDelete: (String) -> Unit,
    onDraftPublish: (String) -> Unit,
    onContentClick: (String) -> Unit,
    onContentDelete: (String) -> Unit,
    onPartyClick: (String) -> Unit,
    onPartyEdit: (String) -> Unit,
    onOpenCamera: () -> Unit,
    onOpenGallery: () -> Unit,
    onCreateParty: () -> Unit,
) {
    when (state.selectedTab) {
        StudioTab.Create -> CreateContent(
            onOpenCamera = onOpenCamera,
            onOpenGallery = onOpenGallery,
            onCreateParty = onCreateParty,
        )
        StudioTab.Drafts -> DraftsContent(
            drafts = state.drafts,
            onDraftClick = onDraftClick,
            onDraftDelete = onDraftDelete,
            onDraftPublish = onDraftPublish,
        )
        StudioTab.Content -> MyContentGrid(
            content = state.myContent,
            onContentClick = onContentClick,
            onContentDelete = onContentDelete,
        )
        StudioTab.Parties -> MyPartiesContent(
            parties = state.myParties,
            onPartyClick = onPartyClick,
            onPartyEdit = onPartyEdit,
        )
    }
}

@Composable
private fun CreateContent(
    onOpenCamera: () -> Unit,
    onOpenGallery: () -> Unit,
    onCreateParty: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(PartyGallerySpacing.md),
        verticalArrangement = Arrangement.spacedBy(PartyGallerySpacing.md),
    ) {
        item {
            Text(
                text = "What do you want to create?",
                style = Theme.typography.titleMedium,
                color = PartyGalleryColors.DarkOnBackground,
                fontWeight = FontWeight.SemiBold,
            )
        }

        item {
            CreateOptionCard(
                icon = Icons.Default.CameraAlt,
                title = "Take Photo/Video",
                description = "Capture a moment right now",
                onClick = onOpenCamera,
            )
        }

        item {
            CreateOptionCard(
                icon = Icons.Default.Image,
                title = "Choose from Gallery",
                description = "Upload existing photos or videos",
                onClick = onOpenGallery,
            )
        }

        item {
            CreateOptionCard(
                icon = Icons.Default.PartyMode,
                title = "Create Party",
                description = "Plan a new party event",
                onClick = onCreateParty,
            )
        }

        item {
            CreateOptionCard(
                icon = Icons.Default.LiveTv,
                title = "Go Live",
                description = "Start streaming your party",
                onClick = { /* Coming soon */ },
                isComingSoon = true,
            )
        }
    }
}

@Composable
private fun CreateOptionCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    isComingSoon: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(PartyGalleryShapes.medium)
            .background(PartyGalleryColors.DarkSurface)
            .clickable(enabled = !isComingSoon, onClick = onClick)
            .padding(PartyGallerySpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    if (isComingSoon) PartyGalleryColors.DarkSurfaceVariant
                    else PartyGalleryColors.Primary.copy(alpha = 0.2f),
                    PartyGalleryShapes.medium,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (isComingSoon) PartyGalleryColors.DarkOnBackgroundVariant
                else PartyGalleryColors.Primary,
            )
        }

        Spacer(modifier = Modifier.width(PartyGallerySpacing.md))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = Theme.typography.titleSmall,
                    color = if (isComingSoon) PartyGalleryColors.DarkOnBackgroundVariant
                    else PartyGalleryColors.DarkOnBackground,
                )
                if (isComingSoon) {
                    Spacer(modifier = Modifier.width(PartyGallerySpacing.xs))
                    Box(
                        modifier = Modifier
                            .background(PartyGalleryColors.DarkSurfaceVariant, PartyGalleryShapes.small)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = "Soon",
                            style = Theme.typography.labelSmall,
                            color = PartyGalleryColors.DarkOnBackgroundVariant,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = Theme.typography.bodySmall,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
            )
        }
    }
}

@Composable
private fun DraftsContent(
    drafts: List<ContentDraft>,
    onDraftClick: (String) -> Unit,
    onDraftDelete: (String) -> Unit,
    onDraftPublish: (String) -> Unit,
) {
    if (drafts.isEmpty()) {
        EmptyStateContent(
            icon = "📝",
            title = "No drafts",
            subtitle = "Your unpublished content will appear here",
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(PartyGallerySpacing.md),
            verticalArrangement = Arrangement.spacedBy(PartyGallerySpacing.sm),
        ) {
            items(drafts) { draft ->
                DraftCard(
                    draft = draft,
                    onClick = { onDraftClick(draft.id) },
                    onDelete = { onDraftDelete(draft.id) },
                    onPublish = { onDraftPublish(draft.id) },
                )
            }
        }
    }
}

@Composable
private fun DraftCard(
    draft: ContentDraft,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onPublish: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(PartyGalleryShapes.medium)
            .background(PartyGalleryColors.DarkSurface)
            .clickable(onClick = onClick)
            .padding(PartyGallerySpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(PartyGalleryShapes.small)
                .background(PartyGalleryColors.DarkSurfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (draft.mediaType == MediaType.VIDEO) "🎬" else "📷",
                style = Theme.typography.headlineSmall,
            )
        }

        Spacer(modifier = Modifier.width(PartyGallerySpacing.sm))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = draft.partyTitle ?: "Untitled",
                style = Theme.typography.titleSmall,
                color = PartyGalleryColors.DarkOnBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            draft.caption?.let {
                Text(
                    text = it,
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
        Row {
            IconButton(onClick = onPublish) {
                Icon(
                    imageVector = Icons.Default.Publish,
                    contentDescription = "Publish",
                    tint = PartyGalleryColors.Primary,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = PartyGalleryColors.Error,
                )
            }
        }
    }
}

@Composable
private fun MyContentGrid(
    content: List<MyContent>,
    onContentClick: (String) -> Unit,
    onContentDelete: (String) -> Unit,
) {
    if (content.isEmpty()) {
        EmptyStateContent(
            icon = "🖼️",
            title = "No content yet",
            subtitle = "Your published photos and videos will appear here",
        )
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(PartyGallerySpacing.md),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(content) { item ->
                ContentGridItem(
                    content = item,
                    onClick = { onContentClick(item.id) },
                )
            }
        }
    }
}

@Composable
private fun ContentGridItem(content: MyContent, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(PartyGalleryShapes.small)
            .background(PartyGalleryColors.DarkSurfaceVariant)
            .clickable(onClick = onClick),
    ) {
        // Placeholder
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (content.mediaType == MediaType.VIDEO) "🎬" else "📷",
                style = Theme.typography.titleLarge,
            )
        }

        // Stats overlay
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color.White,
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = formatNumber(content.likesCount),
                    style = Theme.typography.labelSmall,
                    color = Color.White,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color.White,
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = formatNumber(content.viewsCount),
                    style = Theme.typography.labelSmall,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun MyPartiesContent(
    parties: List<MyParty>,
    onPartyClick: (String) -> Unit,
    onPartyEdit: (String) -> Unit,
) {
    if (parties.isEmpty()) {
        EmptyStateContent(
            icon = "🎉",
            title = "No parties yet",
            subtitle = "Create your first party to get started",
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(PartyGallerySpacing.md),
            verticalArrangement = Arrangement.spacedBy(PartyGallerySpacing.sm),
        ) {
            items(parties) { party ->
                MyPartyCard(
                    party = party,
                    onClick = { onPartyClick(party.id) },
                    onEdit = { onPartyEdit(party.id) },
                )
            }
        }
    }
}

@Composable
private fun MyPartyCard(
    party: MyParty,
    onClick: () -> Unit,
    onEdit: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(PartyGalleryShapes.medium)
            .background(PartyGalleryColors.DarkSurface)
            .clickable(onClick = onClick)
            .padding(PartyGallerySpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Cover
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(PartyGalleryShapes.small)
                .background(PartyGalleryColors.DarkSurfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "🎉", style = Theme.typography.headlineSmall)
            if (party.status == PartyStatus.LIVE) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                ) {
                    LiveBadge()
                }
            }
        }

        Spacer(modifier = Modifier.width(PartyGallerySpacing.sm))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = party.title,
                    style = Theme.typography.titleSmall,
                    color = PartyGalleryColors.DarkOnBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(modifier = Modifier.width(PartyGallerySpacing.xs))
                PartyStatusBadge(status = party.status)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = party.venueName,
                style = Theme.typography.bodySmall,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${party.attendeesCount} going",
                    style = Theme.typography.labelSmall,
                    color = PartyGalleryColors.DarkOnBackgroundVariant,
                )
                Spacer(modifier = Modifier.width(PartyGallerySpacing.md))
                Text(
                    text = "${party.mediaCount} media",
                    style = Theme.typography.labelSmall,
                    color = PartyGalleryColors.DarkOnBackgroundVariant,
                )
                party.mood?.let { mood ->
                    Spacer(modifier = Modifier.width(PartyGallerySpacing.sm))
                    MoodTag(mood = mood)
                }
            }
        }

        IconButton(onClick = onEdit) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = PartyGalleryColors.Primary,
            )
        }
    }
}

@Composable
private fun PartyStatusBadge(status: PartyStatus) {
    val (color, text) = when (status) {
        PartyStatus.DRAFT -> PartyGalleryColors.DarkOnBackgroundVariant to "Draft"
        PartyStatus.PLANNED -> PartyGalleryColors.Primary to "Planned"
        PartyStatus.LIVE -> PartyGalleryColors.Error to "Live"
        PartyStatus.ENDED -> PartyGalleryColors.DarkOnBackgroundVariant to "Ended"
        PartyStatus.CANCELLED -> PartyGalleryColors.Error.copy(alpha = 0.5f) to "Cancelled"
    }

    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.2f), PartyGalleryShapes.small)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = text,
            style = Theme.typography.labelSmall,
            color = color,
        )
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

private fun formatNumber(number: Int): String {
    return when {
        number >= 1000000 -> "${number / 1000000}M"
        number >= 1000 -> "${number / 1000}K"
        else -> number.toString()
    }
}
