package com.partygallery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.partygallery.ui.theme.PartyGalleryShapes
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.Theme

/**
 * Dark Fade Gradient - For overlaying text on images
 *
 * Provides visibility for text over media content.
 * Usage: Apply to bottom portion of MediaCard.
 */
object MediaCardGradients {
    val darkFadeBottom = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color(0x99000000),
            Color(0xF0000000),
        ),
    )

    val darkFadeTop = Brush.verticalGradient(
        colors = listOf(
            Color(0xF0000000),
            Color(0x99000000),
            Color.Transparent,
        ),
    )

    val scrim = Brush.verticalGradient(
        colors = listOf(
            Color(0x00000000),
            Color(0xCC000000),
        ),
    )
}

/**
 * Featured Media Card
 *
 * Full-featured card for media content with:
 * - Image/video background
 * - Gradient overlay for text visibility
 * - User info (avatar, name, timestamp)
 * - Engagement metrics (likes, comments)
 *
 * Designed following Dark Mode First principles.
 *
 * @param modifier Modifier for the card
 * @param aspectRatio Aspect ratio of the card (default 16:10)
 * @param onClick Click handler
 * @param content Content to display (typically an AsyncImage)
 * @param overlayContent Optional content for the gradient overlay area
 */
@Composable
fun FeaturedMediaCard(
    modifier: Modifier = Modifier,
    aspectRatio: Float = 16f / 10f,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
    overlayContent: @Composable (BoxScope.() -> Unit)? = null,
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(PartyGalleryShapes.mediaCard)
            .background(Theme.colors.surfaceVariant)
            .then(clickModifier),
    ) {
        // Background content (image/video)
        content()

        // Gradient overlay at bottom
        if (overlayContent != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.BottomCenter)
                    .background(MediaCardGradients.darkFadeBottom),
            )

            // Overlay content
            overlayContent()
        }
    }
}

/**
 * Media Card with User Info
 *
 * Pre-built card with user info and engagement metrics.
 *
 * @param modifier Modifier
 * @param userName User display name
 * @param userAvatarUrl User avatar URL (optional)
 * @param timestamp Formatted timestamp
 * @param likeCount Number of likes
 * @param commentCount Number of comments
 * @param isLiked Whether the current user has liked this
 * @param onLikeClick Like button click handler
 * @param onClick Card click handler
 * @param content Media content (image/video)
 */
@Composable
fun MediaCardWithInfo(
    modifier: Modifier = Modifier,
    userName: String,
    userAvatarUrl: String? = null,
    timestamp: String,
    likeCount: Int = 0,
    commentCount: Int = 0,
    isLiked: Boolean = false,
    onLikeClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    avatarContent: @Composable (() -> Unit)? = null,
    likeIcon: ImageVector? = null,
    commentIcon: ImageVector? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    FeaturedMediaCard(
        modifier = modifier,
        onClick = onClick,
        content = content,
        overlayContent = {
            // Bottom info row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(PartyGallerySpacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                // User info (left side)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Avatar
                    if (avatarContent != null) {
                        avatarContent()
                        Spacer(modifier = Modifier.width(PartyGallerySpacing.xs))
                    }

                    Column {
                        Text(
                            text = userName,
                            style = Theme.typography.labelMedium,
                            color = Color.White,
                        )
                        Text(
                            text = timestamp,
                            style = Theme.typography.bodySmall,
                            color = Theme.colors.onBackgroundVariant,
                        )
                    }
                }

                // Engagement metrics (right side)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PartyGallerySpacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Likes
                    if (likeIcon != null) {
                        Row(
                            modifier = if (onLikeClick != null) {
                                Modifier.clickable(onClick = onLikeClick)
                            } else {
                                Modifier
                            },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = likeIcon,
                                contentDescription = "Like",
                                modifier = Modifier.size(18.dp),
                                tint = if (isLiked) Theme.colors.error else Color.White,
                            )
                            Text(
                                text = formatCount(likeCount),
                                style = Theme.typography.counter,
                                color = Color.White,
                            )
                        }
                    }

                    // Comments
                    if (commentIcon != null && commentCount > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = commentIcon,
                                contentDescription = "Comments",
                                modifier = Modifier.size(18.dp),
                                tint = Color.White,
                            )
                            Text(
                                text = formatCount(commentCount),
                                style = Theme.typography.counter,
                                color = Color.White,
                            )
                        }
                    }
                }
            }
        },
    )
}

/**
 * Compact Media Card
 *
 * Smaller card for grid layouts without overlay.
 */
@Composable
fun CompactMediaCard(
    modifier: Modifier = Modifier,
    aspectRatio: Float = 1f,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .clip(PartyGalleryShapes.mediaCard)
            .background(Theme.colors.surfaceVariant)
            .then(clickModifier),
        content = content,
    )
}

/**
 * Format count for display (e.g., 1.2K, 5.3M)
 */
private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
