package com.partygallery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.partygallery.ui.theme.PartyGalleryColors
import com.partygallery.ui.theme.Theme

/**
 * Avatar sizes following the design system
 */
enum class AvatarSize(val size: Dp, val borderWidth: Dp) {
    /** Extra small - for dense lists, counters */
    XS(size = 24.dp, borderWidth = 1.dp),

    /** Small - for comments, chat messages */
    SMALL(size = 32.dp, borderWidth = 2.dp),

    /** Medium - for cards, list items */
    MEDIUM(size = 40.dp, borderWidth = 2.dp),

    /** Large - for profile headers */
    LARGE(size = 56.dp, borderWidth = 3.dp),

    /** Extra Large - for profile pages */
    XL(size = 80.dp, borderWidth = 3.dp),

    /** XXL - for large profile display */
    XXL(size = 120.dp, borderWidth = 4.dp),
}

/**
 * Gradient border for highlighted avatars
 *
 * Amber gradient following brand colors.
 */
object AvatarGradients {
    // Amber gradient: #F59E0B -> #FCD34D -> Orange 400
    val amberBorder = Brush.linearGradient(
        colors = listOf(
            PartyGalleryColors.Primary,
            PartyGalleryColors.Tertiary,
            Color(0xFFFB923C),
        ),
    )

    // Live gradient: #EF4444 -> Pink 500
    val liveBorder = Brush.linearGradient(
        colors = listOf(
            PartyGalleryColors.Error,
            Color(0xFFEC4899),
        ),
    )
}

/**
 * Avatar Component
 *
 * Circular avatar with optional gradient border for highlighted users.
 * Supports various sizes and customization options.
 *
 * @param modifier Modifier
 * @param size Avatar size preset
 * @param showBorder Whether to show the gradient border
 * @param isLive Whether user is currently live (uses live border)
 * @param onClick Click handler
 * @param content Avatar content (typically AsyncImage or placeholder)
 */
@Composable
fun Avatar(
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.MEDIUM,
    showBorder: Boolean = false,
    isLive: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val totalSize = if (showBorder) {
        size.size + (size.borderWidth * 2)
    } else {
        size.size
    }

    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    val gradient = when {
        isLive -> AvatarGradients.liveBorder
        showBorder -> AvatarGradients.amberBorder
        else -> null
    }

    Box(
        modifier = modifier
            .size(totalSize)
            .then(clickModifier),
        contentAlignment = Alignment.Center,
    ) {
        // Gradient border background
        if (showBorder && gradient != null) {
            Box(
                modifier = Modifier
                    .size(totalSize)
                    .clip(CircleShape)
                    .background(gradient),
            )
        }

        // Avatar content container
        Box(
            modifier = Modifier
                .size(size.size)
                .clip(CircleShape)
                .background(Theme.colors.surfaceVariant),
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}

/**
 * Avatar with initials fallback
 *
 * Shows initials when no image is available.
 *
 * @param name User name to extract initials from
 * @param modifier Modifier
 * @param size Avatar size
 * @param showBorder Show gradient border
 * @param isLive Is user live
 * @param onClick Click handler
 * @param imageContent Optional image content
 */
@Composable
fun AvatarWithInitials(
    name: String,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.MEDIUM,
    showBorder: Boolean = false,
    isLive: Boolean = false,
    onClick: (() -> Unit)? = null,
    imageContent: (@Composable BoxScope.() -> Unit)? = null,
) {
    Avatar(
        modifier = modifier,
        size = size,
        showBorder = showBorder,
        isLive = isLive,
        onClick = onClick,
    ) {
        if (imageContent != null) {
            imageContent()
        } else {
            // Show initials
            Text(
                text = getInitials(name),
                style = when (size) {
                    AvatarSize.XS -> Theme.typography.labelSmall
                    AvatarSize.SMALL -> Theme.typography.labelSmall
                    AvatarSize.MEDIUM -> Theme.typography.labelMedium
                    AvatarSize.LARGE -> Theme.typography.titleMedium
                    AvatarSize.XL -> Theme.typography.titleLarge
                    AvatarSize.XXL -> Theme.typography.headlineMedium
                },
                color = Theme.colors.onBackground,
            )
        }
    }
}

/**
 * Story-style avatar with ring
 *
 * Avatar with a ring indicating new story/content.
 */
@Composable
fun StoryAvatar(
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.LARGE,
    hasNewStory: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val ringSize = size.size + 8.dp
    val ringColor = if (hasNewStory) {
        AvatarGradients.amberBorder
    } else {
        Brush.linearGradient(
            colors = listOf(
                Theme.colors.border,
                Theme.colors.border,
            ),
        )
    }

    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(ringSize)
            .then(clickModifier),
        contentAlignment = Alignment.Center,
    ) {
        // Ring
        Box(
            modifier = Modifier
                .size(ringSize)
                .clip(CircleShape)
                .background(ringColor),
        )

        // Gap (background color)
        Box(
            modifier = Modifier
                .size(size.size + 4.dp)
                .clip(CircleShape)
                .background(Theme.colors.background),
        )

        // Avatar
        Box(
            modifier = Modifier
                .size(size.size)
                .clip(CircleShape)
                .background(Theme.colors.surfaceVariant),
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}

/**
 * Avatar group - shows multiple avatars stacked
 *
 * @param count Total number of avatars to represent
 * @param maxVisible Maximum visible avatars before showing "+N"
 * @param size Avatar size
 * @param avatarContents Content for each visible avatar
 */
@Composable
fun AvatarGroup(
    count: Int,
    maxVisible: Int = 3,
    size: AvatarSize = AvatarSize.SMALL,
    modifier: Modifier = Modifier,
    avatarContents: List<@Composable BoxScope.() -> Unit>,
) {
    val visibleCount = minOf(count, maxVisible)
    val remainingCount = count - visibleCount
    val overlapOffset = size.size * 0.6f

    Box(modifier = modifier) {
        // Visible avatars
        avatarContents.take(visibleCount).forEachIndexed { index, content ->
            Box(
                modifier = Modifier.padding(start = overlapOffset * index),
            ) {
                Avatar(
                    size = size,
                    modifier = Modifier.border(
                        width = 2.dp,
                        color = Theme.colors.background,
                        shape = CircleShape,
                    ),
                    content = content,
                )
            }
        }

        // Remaining count indicator
        if (remainingCount > 0) {
            Box(
                modifier = Modifier
                    .padding(start = overlapOffset * visibleCount)
                    .size(size.size)
                    .clip(CircleShape)
                    .background(Theme.colors.primary)
                    .border(
                        width = 2.dp,
                        color = Theme.colors.background,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "+$remainingCount",
                    style = Theme.typography.labelSmall,
                    color = Theme.colors.onPrimary,
                )
            }
        }
    }
}

/**
 * Extract initials from a name
 */
private fun getInitials(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotEmpty() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "${parts.first().first()}${parts.last().first()}".uppercase()
    }
}
