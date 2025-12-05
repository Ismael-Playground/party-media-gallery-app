package com.partygallery.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.partygallery.ui.theme.PartyGalleryColors
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.Theme

/**
 * Bottom Navigation Bar for Party Gallery.
 *
 * S3-002: MainScreen con BottomNavBar
 *
 * Design: Dark Mode First
 * - Background: #0A0A0A (DarkBackground)
 * - Active: #F59E0B (Primary Amber)
 * - Inactive: #A1A1AA (OnBackgroundVariant)
 */
@Composable
fun BottomNavBar(selectedIndex: Int, onItemSelected: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(PartyGalleryColors.DarkBackground)
            .padding(vertical = PartyGallerySpacing.sm),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BottomNavItems.entries.forEachIndexed { index, item ->
            BottomNavItem(
                item = item,
                isSelected = selectedIndex == index,
                onClick = { onItemSelected(index) },
            )
        }
    }
}

/**
 * Individual navigation item.
 */
@Composable
private fun BottomNavItem(
    item: BottomNavItems,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) PartyGalleryColors.Primary else PartyGalleryColors.DarkOnBackgroundVariant,
        animationSpec = tween(durationMillis = 200),
    )

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = PartyGallerySpacing.md, vertical = PartyGallerySpacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.label,
            modifier = Modifier.size(24.dp),
            tint = contentColor,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.label,
            style = Theme.typography.labelSmall,
            color = contentColor,
        )
    }
}

/**
 * Bottom navigation items enum with icons.
 */
enum class BottomNavItems(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    Home(
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
    ),
    Favorites(
        label = "Favorites",
        selectedIcon = Icons.Filled.Star,
        unselectedIcon = Icons.Outlined.Star,
    ),
    Studio(
        label = "Studio",
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Filled.Add,
    ),
    Profile(
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
    ),
}
