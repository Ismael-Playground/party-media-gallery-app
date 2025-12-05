package com.partygallery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.partygallery.ui.theme.PartyGalleryShapes
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.Theme

/**
 * Card elevation levels
 */
enum class PartyCardElevation {
    /** Flat - same as background */
    FLAT,
    /** Low - surfaceVariant */
    LOW,
    /** Medium - surfaceContainer */
    MEDIUM,
    /** High - surfaceContainerHigh */
    HIGH
}

/**
 * Party Gallery Card Component
 *
 * Container component for grouping related content.
 * Follows the Dark Mode First design system with subtle surface variations.
 *
 * @param modifier Modifier for the card
 * @param elevation Surface elevation level
 * @param padding Internal padding
 * @param onClick Optional click handler
 * @param showBorder Whether to show a subtle border
 * @param content Card content
 */
@Composable
fun PartyCard(
    modifier: Modifier = Modifier,
    elevation: PartyCardElevation = PartyCardElevation.LOW,
    padding: Dp = PartyGallerySpacing.cardPadding,
    onClick: (() -> Unit)? = null,
    showBorder: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = Theme.colors

    val backgroundColor = when (elevation) {
        PartyCardElevation.FLAT -> colors.background
        PartyCardElevation.LOW -> colors.surfaceVariant
        PartyCardElevation.MEDIUM -> colors.surfaceContainer
        PartyCardElevation.HIGH -> colors.surfaceContainerHigh
    }

    val borderModifier = if (showBorder) {
        Modifier.border(
            width = 1.dp,
            color = colors.border,
            shape = PartyGalleryShapes.mediaCard
        )
    } else {
        Modifier
    }

    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(PartyGalleryShapes.mediaCard)
            .then(borderModifier)
            .background(backgroundColor)
            .then(clickModifier)
            .padding(padding),
        content = content
    )
}

/**
 * Media Card - Specialized card for displaying media content
 *
 * No padding, designed for images/videos to fill the entire card.
 */
@Composable
fun MediaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = Theme.colors

    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(PartyGalleryShapes.mediaCard)
            .background(colors.surfaceVariant)
            .then(clickModifier),
        content = content
    )
}
