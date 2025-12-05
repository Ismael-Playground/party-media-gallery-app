package com.partygallery.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.partygallery.ui.theme.PartyGalleryColors
import com.partygallery.ui.theme.PartyGalleryShapes
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.Theme

/**
 * Live Badge Component
 *
 * Pulsating red badge indicating a live event/stream.
 * Following Dark Mode First design system.
 *
 * @param modifier Modifier
 * @param showText Whether to show "LIVE" text
 */
@Composable
fun LiveBadge(modifier: Modifier = Modifier, showText: Boolean = true) {
    val infiniteTransition = rememberInfiniteTransition(label = "livePulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAlpha",
    )

    Row(
        modifier = modifier
            .clip(PartyGalleryShapes.chip)
            .background(PartyGalleryColors.Error.copy(alpha = 0.2f))
            .padding(
                horizontal = PartyGallerySpacing.xs,
                vertical = PartyGallerySpacing.xxs,
            ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Pulsating dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .alpha(alpha)
                .clip(CircleShape)
                .background(PartyGalleryColors.Error),
        )

        if (showText) {
            Text(
                text = "LIVE",
                style = Theme.typography.labelSmall,
                color = PartyGalleryColors.Error,
            )
        }
    }
}

/**
 * Small Live Indicator
 *
 * Just the pulsating dot, for compact spaces.
 */
@Composable
fun LiveIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "livePulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAlpha",
    )

    Box(
        modifier = modifier
            .size(10.dp)
            .alpha(alpha)
            .clip(CircleShape)
            .background(PartyGalleryColors.Error),
    )
}

/**
 * Live Badge with viewer count
 *
 * Shows live indicator plus number of viewers.
 */
@Composable
fun LiveBadgeWithCount(viewerCount: Int, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "livePulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAlpha",
    )

    Row(
        modifier = modifier
            .clip(PartyGalleryShapes.chip)
            .background(PartyGalleryColors.Error.copy(alpha = 0.2f))
            .padding(
                horizontal = PartyGallerySpacing.xs,
                vertical = PartyGallerySpacing.xxs,
            ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Pulsating dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .alpha(alpha)
                .clip(CircleShape)
                .background(PartyGalleryColors.Error),
        )

        Text(
            text = "LIVE",
            style = Theme.typography.labelSmall,
            color = PartyGalleryColors.Error,
        )

        // Separator
        Text(
            text = "•",
            style = Theme.typography.labelSmall,
            color = PartyGalleryColors.Error.copy(alpha = 0.5f),
        )

        // Viewer count
        Text(
            text = formatViewerCount(viewerCount),
            style = Theme.typography.labelSmall,
            color = Color.White,
        )
    }
}

/**
 * Recording indicator (non-pulsating, solid)
 */
@Composable
fun RecordingIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(PartyGalleryShapes.chip)
            .background(PartyGalleryColors.Error)
            .padding(
                horizontal = PartyGallerySpacing.xs,
                vertical = PartyGallerySpacing.xxs,
            ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color.White),
        )

        Text(
            text = "REC",
            style = Theme.typography.labelSmall,
            color = Color.White,
        )
    }
}

/**
 * Format viewer count for display
 */
private fun formatViewerCount(count: Int): String {
    return when {
        count >= 1_000_000 -> {
            val value = count / 1_000_000.0
            "${(value * 10).toInt() / 10.0}M"
        }
        count >= 1_000 -> {
            val value = count / 1_000.0
            "${(value * 10).toInt() / 10.0}K"
        }
        else -> count.toString()
    }
}
