package com.partygallery.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.partygallery.ui.components.BottomNavBar
import com.partygallery.ui.theme.PartyGalleryColors
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.Theme

/**
 * Main Screen with bottom navigation.
 *
 * S3-002: MainScreen con BottomNavBar
 *
 * Design: Dark Mode First
 * - Background: #0A0A0A
 * - Primary: #F59E0B (Amber)
 */
@Composable
fun MainScreen(
    userFirstName: String = "User",
    userEmail: String = "",
    onLogout: () -> Unit = {},
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PartyGalleryColors.DarkBackground),
    ) {
        // Content area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            when (selectedTabIndex) {
                0 -> HomeScreen()
                1 -> FavoritesScreen()
                2 -> StudioScreen()
                3 -> ProfileScreen(
                    userName = userFirstName,
                    userEmail = userEmail,
                    onLogout = onLogout,
                )
            }
        }

        // Bottom Navigation
        BottomNavBar(
            selectedIndex = selectedTabIndex,
            onItemSelected = { selectedTabIndex = it },
        )
    }
}

/**
 * Favorites Screen placeholder.
 */
@Composable
fun FavoritesScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PartyGalleryColors.DarkBackground)
            .padding(PartyGallerySpacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "⭐",
                style = Theme.typography.displayLarge,
            )
            Text(
                text = "Favorites",
                style = Theme.typography.headlineMedium,
                color = PartyGalleryColors.DarkOnBackground,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Your favorite parties will appear here",
                style = Theme.typography.bodyMedium,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
            )
        }
    }
}

/**
 * Studio Screen placeholder.
 */
@Composable
fun StudioScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PartyGalleryColors.DarkBackground)
            .padding(PartyGallerySpacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "🎬",
                style = Theme.typography.displayLarge,
            )
            Text(
                text = "Party Studio",
                style = Theme.typography.headlineMedium,
                color = PartyGalleryColors.DarkOnBackground,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Create and edit your party content",
                style = Theme.typography.bodyMedium,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
            )
        }
    }
}

/**
 * Profile Screen placeholder.
 */
@Composable
fun ProfileScreen(
    userName: String,
    userEmail: String,
    onLogout: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PartyGalleryColors.DarkBackground)
            .padding(PartyGallerySpacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "👤",
                style = Theme.typography.displayLarge,
            )
            Text(
                text = userName,
                style = Theme.typography.headlineMedium,
                color = PartyGalleryColors.DarkOnBackground,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = userEmail,
                style = Theme.typography.bodyMedium,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
            )
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.padding(PartyGallerySpacing.lg),
            )
            com.partygallery.ui.components.PartyButton(
                text = "Logout",
                onClick = onLogout,
                variant = com.partygallery.ui.components.PartyButtonVariant.SECONDARY,
            )
        }
    }
}
