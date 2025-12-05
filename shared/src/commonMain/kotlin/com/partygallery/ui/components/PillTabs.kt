package com.partygallery.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.Theme

/**
 * Pill Tabs Component
 *
 * Horizontal scrollable tab selector with pill-shaped items.
 * Selected tab has filled background, unselected has border only.
 *
 * Following Dark Mode First design system.
 *
 * @param tabs List of tab labels
 * @param selectedIndex Currently selected tab index
 * @param onTabSelected Callback when a tab is selected
 * @param modifier Modifier
 */
@Composable
fun PillTabs(tabs: List<String>, selectedIndex: Int, onTabSelected: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = PartyGallerySpacing.md),
        horizontalArrangement = Arrangement.spacedBy(PartyGallerySpacing.xs),
    ) {
        tabs.forEachIndexed { index, tab ->
            PillTab(
                text = tab,
                isSelected = index == selectedIndex,
                onClick = { onTabSelected(index) },
            )
        }
    }
}

/**
 * Single Pill Tab Item
 */
@Composable
private fun PillTab(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            Theme.colors.surfaceVariant
        } else {
            Color.Transparent
        },
        label = "tabBackground",
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            Theme.colors.onBackground
        } else {
            Theme.colors.onBackgroundVariant
        },
        label = "tabText",
    )

    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .then(
                if (!isSelected) {
                    Modifier.border(
                        width = 1.dp,
                        color = Theme.colors.border,
                        shape = shape,
                    )
                } else {
                    Modifier
                },
            )
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(
                horizontal = PartyGallerySpacing.md,
                vertical = PartyGallerySpacing.xs,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = Theme.typography.labelMedium,
            color = textColor,
        )
    }
}

/**
 * Scrollable Pill Tabs
 *
 * For cases where tabs may overflow horizontally.
 */
@Composable
fun ScrollablePillTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.lazy.LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PartyGallerySpacing.xs),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = PartyGallerySpacing.md,
        ),
    ) {
        items(tabs.size) { index ->
            PillTab(
                text = tabs[index],
                isSelected = index == selectedIndex,
                onClick = { onTabSelected(index) },
            )
        }
    }
}

/**
 * Icon Pill Tabs
 *
 * Pill tabs with icons instead of text.
 */
@Composable
fun IconPillTabs(
    icons: List<@Composable () -> Unit>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = PartyGallerySpacing.md),
        horizontalArrangement = Arrangement.spacedBy(PartyGallerySpacing.xs),
    ) {
        icons.forEachIndexed { index, icon ->
            IconPillTab(
                icon = icon,
                isSelected = index == selectedIndex,
                onClick = { onTabSelected(index) },
            )
        }
    }
}

@Composable
private fun IconPillTab(
    icon: @Composable () -> Unit,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            Theme.colors.surfaceVariant
        } else {
            Color.Transparent
        },
        label = "tabBackground",
    )

    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .then(
                if (!isSelected) {
                    Modifier.border(
                        width = 1.dp,
                        color = Theme.colors.border,
                        shape = shape,
                    )
                } else {
                    Modifier
                },
            )
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(PartyGallerySpacing.xs),
        contentAlignment = Alignment.Center,
    ) {
        icon()
    }
}
