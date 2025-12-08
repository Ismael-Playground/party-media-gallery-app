package com.partygallery.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.partygallery.ui.screens.favorites.FavoritesScreen
import com.partygallery.ui.screens.party.PartyDetailScreen
import com.partygallery.ui.screens.studio.StudioScreen
import com.partygallery.ui.theme.PartyGalleryColors
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.Theme

/**
 * Navigation state for MainScreen.
 */
sealed class MainNavigation {
    data object Home : MainNavigation()
    data object Favorites : MainNavigation()
    data object Studio : MainNavigation()
    data object Profile : MainNavigation()
    data class PartyDetail(val partyId: String) : MainNavigation()
    data class MediaUpload(val partyId: String) : MainNavigation()
}

/**
 * Main Screen with bottom navigation.
 *
 * S3-002: MainScreen con BottomNavBar
 * S4-005: Navigation to PartyDetail
 *
 * Design: Dark Mode First
 * - Background: #0A0A0A
 * - Primary: #F59E0B (Amber)
 */
@Composable
fun MainScreen(userFirstName: String = "User", userEmail: String = "", onLogout: () -> Unit = {}) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var currentNavigation by remember { mutableStateOf<MainNavigation>(MainNavigation.Home) }

    // Handle back navigation from detail screens
    val navigateBack: () -> Unit = {
        currentNavigation = when (selectedTabIndex) {
            0 -> MainNavigation.Home
            1 -> MainNavigation.Favorites
            2 -> MainNavigation.Studio
            3 -> MainNavigation.Profile
            else -> MainNavigation.Home
        }
    }

    // Handle party click from feed
    val onPartyClick: (String) -> Unit = { partyId ->
        currentNavigation = MainNavigation.PartyDetail(partyId)
    }

    // Handle media upload from party detail
    val onUploadMedia: (String) -> Unit = { partyId ->
        currentNavigation = MainNavigation.MediaUpload(partyId)
    }

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
            when (val nav = currentNavigation) {
                is MainNavigation.Home -> {
                    HomeScreen(
                        onPartyClick = onPartyClick,
                    )
                }
                is MainNavigation.Favorites -> FavoritesScreen(
                    onPartyClick = onPartyClick,
                )
                is MainNavigation.Studio -> StudioScreen()
                is MainNavigation.Profile -> ProfileScreen(
                    userName = userFirstName,
                    userEmail = userEmail,
                    onLogout = onLogout,
                )
                is MainNavigation.PartyDetail -> PartyDetailScreen(
                    partyId = nav.partyId,
                    onNavigateBack = navigateBack,
                    onUploadMedia = onUploadMedia,
                )
                is MainNavigation.MediaUpload -> MediaUploadScreen(
                    partyId = nav.partyId,
                    onNavigateBack = navigateBack,
                    onUploadComplete = navigateBack,
                )
            }
        }

        // Bottom Navigation - hide when in detail screens
        if (currentNavigation !is MainNavigation.PartyDetail &&
            currentNavigation !is MainNavigation.MediaUpload
        ) {
            BottomNavBar(
                selectedIndex = selectedTabIndex,
                onItemSelected = { index ->
                    selectedTabIndex = index
                    currentNavigation = when (index) {
                        0 -> MainNavigation.Home
                        1 -> MainNavigation.Favorites
                        2 -> MainNavigation.Studio
                        3 -> MainNavigation.Profile
                        else -> MainNavigation.Home
                    }
                },
            )
        }
    }
}

/**
 * Profile Screen placeholder.
 */
@Composable
fun ProfileScreen(userName: String, userEmail: String, onLogout: () -> Unit) {
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

/**
 * Media Upload Screen placeholder.
 *
 * S4-006: MediaUploadScreen placeholder
 */
@Composable
fun MediaUploadScreen(partyId: String, onNavigateBack: () -> Unit, onUploadComplete: () -> Unit) {
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
                text = "📤",
                style = Theme.typography.displayLarge,
            )
            Text(
                text = "Upload Media",
                style = Theme.typography.headlineMedium,
                color = PartyGalleryColors.DarkOnBackground,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Share your party moments",
                style = Theme.typography.bodyMedium,
                color = PartyGalleryColors.DarkOnBackgroundVariant,
            )
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.padding(PartyGallerySpacing.lg),
            )
            com.partygallery.ui.components.PartyButton(
                text = "Select Photo/Video",
                onClick = { /* TODO: Implement media picker */ },
                variant = com.partygallery.ui.components.PartyButtonVariant.PRIMARY,
            )
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.padding(PartyGallerySpacing.sm),
            )
            com.partygallery.ui.components.PartyButton(
                text = "Back",
                onClick = onNavigateBack,
                variant = com.partygallery.ui.components.PartyButtonVariant.SECONDARY,
            )
        }
    }
}
