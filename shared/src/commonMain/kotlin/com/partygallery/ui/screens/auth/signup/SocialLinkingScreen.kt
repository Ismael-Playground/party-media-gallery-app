package com.partygallery.ui.screens.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.partygallery.presentation.intent.SignUpIntent
import com.partygallery.presentation.state.SocialPlatform
import com.partygallery.presentation.store.SignUpStore
import com.partygallery.ui.components.PartyButton
import com.partygallery.ui.components.PartyButtonSize
import com.partygallery.ui.components.PartyButtonVariant
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.PartyGalleryTypography
import com.partygallery.ui.theme.Theme

/**
 * Social Linking Screen - Step 5 of SignUp Flow
 *
 * S2-015: Fifth step of signup flow
 *
 * Allows user to:
 * - Connect Instagram
 * - Connect TikTok
 * - Connect Twitter/X
 * - Connect Facebook
 * - Connect Pinterest
 *
 * All optional, user can skip
 */
@Composable
fun SocialLinkingScreen(
    signUpStore: SignUpStore,
    onBackPressed: () -> Unit = {},
) {
    val state by signUpStore.state.collectAsState()
    val colors = Theme.colors

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = PartyGallerySpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Header
            SignUpStepHeader(
                stepNumber = 5,
                totalSteps = 6,
                title = "Connect Your Socials",
                subtitle = "Link your accounts to share party content seamlessly",
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Info text
            Text(
                text = "Connected accounts will sync your party content automatically",
                style = PartyGalleryTypography.bodySmall,
                color = colors.onBackgroundVariant,
                modifier = Modifier.padding(horizontal = PartyGallerySpacing.md),
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Social platforms
            Column(
                verticalArrangement = Arrangement.spacedBy(PartyGallerySpacing.md),
            ) {
                SocialPlatformItem(
                    platform = SocialPlatform.INSTAGRAM,
                    isConnected = state.socialLinks.instagram != null,
                    isConnecting = state.connectingSocial == SocialPlatform.INSTAGRAM,
                    username = state.socialLinks.instagram,
                    onConnect = { signUpStore.processIntent(SignUpIntent.ConnectSocial(SocialPlatform.INSTAGRAM)) },
                    onDisconnect = { signUpStore.processIntent(SignUpIntent.DisconnectSocial(SocialPlatform.INSTAGRAM)) },
                )

                SocialPlatformItem(
                    platform = SocialPlatform.TIKTOK,
                    isConnected = state.socialLinks.tiktok != null,
                    isConnecting = state.connectingSocial == SocialPlatform.TIKTOK,
                    username = state.socialLinks.tiktok,
                    onConnect = { signUpStore.processIntent(SignUpIntent.ConnectSocial(SocialPlatform.TIKTOK)) },
                    onDisconnect = { signUpStore.processIntent(SignUpIntent.DisconnectSocial(SocialPlatform.TIKTOK)) },
                )

                SocialPlatformItem(
                    platform = SocialPlatform.TWITTER,
                    isConnected = state.socialLinks.twitter != null,
                    isConnecting = state.connectingSocial == SocialPlatform.TWITTER,
                    username = state.socialLinks.twitter,
                    onConnect = { signUpStore.processIntent(SignUpIntent.ConnectSocial(SocialPlatform.TWITTER)) },
                    onDisconnect = { signUpStore.processIntent(SignUpIntent.DisconnectSocial(SocialPlatform.TWITTER)) },
                )

                SocialPlatformItem(
                    platform = SocialPlatform.FACEBOOK,
                    isConnected = state.socialLinks.facebook != null,
                    isConnecting = state.connectingSocial == SocialPlatform.FACEBOOK,
                    username = state.socialLinks.facebook,
                    onConnect = { signUpStore.processIntent(SignUpIntent.ConnectSocial(SocialPlatform.FACEBOOK)) },
                    onDisconnect = { signUpStore.processIntent(SignUpIntent.DisconnectSocial(SocialPlatform.FACEBOOK)) },
                )

                SocialPlatformItem(
                    platform = SocialPlatform.PINTEREST,
                    isConnected = state.socialLinks.pinterest != null,
                    isConnecting = state.connectingSocial == SocialPlatform.PINTEREST,
                    username = state.socialLinks.pinterest,
                    onConnect = { signUpStore.processIntent(SignUpIntent.ConnectSocial(SocialPlatform.PINTEREST)) },
                    onDisconnect = { signUpStore.processIntent(SignUpIntent.DisconnectSocial(SocialPlatform.PINTEREST)) },
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Connected count
            val connectedCount = state.socialLinks.linkedCount
            if (connectedCount > 0) {
                Text(
                    text = "$connectedCount account${if (connectedCount > 1) "s" else ""} connected",
                    style = PartyGalleryTypography.labelMedium,
                    color = colors.success,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Continue Button
            PartyButton(
                text = "Continue",
                onClick = { signUpStore.processIntent(SignUpIntent.SubmitSocialLinks) },
                modifier = Modifier.fillMaxWidth(),
                variant = PartyButtonVariant.PRIMARY,
                size = PartyButtonSize.LARGE,
                enabled = state.connectingSocial == null,
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

            // Skip Button
            Text(
                text = "Skip for now",
                style = PartyGalleryTypography.labelMedium,
                color = colors.onBackgroundVariant,
                modifier = Modifier
                    .clickable(enabled = state.connectingSocial == null) {
                        signUpStore.processIntent(SignUpIntent.SkipStep)
                    }
                    .padding(PartyGallerySpacing.md),
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

            // Back Button
            Text(
                text = "Back",
                style = PartyGalleryTypography.labelMedium,
                color = colors.primary,
                modifier = Modifier
                    .clickable(enabled = state.connectingSocial == null) {
                        signUpStore.processIntent(SignUpIntent.PreviousStep)
                        onBackPressed()
                    }
                    .padding(PartyGallerySpacing.md),
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.xl))
        }
    }
}

/**
 * Single social platform connection item
 */
@Composable
private fun SocialPlatformItem(
    platform: SocialPlatform,
    isConnected: Boolean,
    isConnecting: Boolean,
    username: String?,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
) {
    val colors = Theme.colors
    val platformInfo = getPlatformInfo(platform)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isConnected) colors.surfaceVariant else colors.surface)
            .border(
                width = 1.dp,
                color = if (isConnected) colors.success.copy(alpha = 0.5f) else colors.divider,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(PartyGallerySpacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Platform icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(platformInfo.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = platformInfo.emoji,
                style = PartyGalleryTypography.titleLarge,
            )
        }

        Spacer(modifier = Modifier.width(PartyGallerySpacing.md))

        // Platform info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = platformInfo.name,
                style = PartyGalleryTypography.titleSmall,
                color = colors.onBackground,
            )
            if (isConnected && username != null) {
                Text(
                    text = "@$username",
                    style = PartyGalleryTypography.bodySmall,
                    color = colors.success,
                )
            } else {
                Text(
                    text = platformInfo.description,
                    style = PartyGalleryTypography.bodySmall,
                    color = colors.onBackgroundVariant,
                )
            }
        }

        Spacer(modifier = Modifier.width(PartyGallerySpacing.sm))

        // Action button
        if (isConnecting) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = colors.primary,
                strokeWidth = 2.dp,
            )
        } else if (isConnected) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.error.copy(alpha = 0.1f))
                    .clickable { onDisconnect() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "Disconnect",
                    style = PartyGalleryTypography.labelSmall,
                    color = colors.error,
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(platformInfo.color)
                    .clickable { onConnect() }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "Connect",
                    style = PartyGalleryTypography.labelMedium,
                    color = Color.White,
                )
            }
        }
    }
}

/**
 * Platform display information
 */
private data class PlatformDisplayInfo(
    val name: String,
    val emoji: String,
    val color: Color,
    val description: String,
)

/**
 * Get display info for each platform
 */
private fun getPlatformInfo(platform: SocialPlatform): PlatformDisplayInfo {
    return when (platform) {
        SocialPlatform.INSTAGRAM -> PlatformDisplayInfo(
            name = "Instagram",
            emoji = "📸",
            color = Color(0xFFE4405F),
            description = "Share photos & stories",
        )
        SocialPlatform.TIKTOK -> PlatformDisplayInfo(
            name = "TikTok",
            emoji = "🎵",
            color = Color(0xFF000000),
            description = "Share short videos",
        )
        SocialPlatform.TWITTER -> PlatformDisplayInfo(
            name = "X (Twitter)",
            emoji = "🐦",
            color = Color(0xFF1DA1F2),
            description = "Share updates",
        )
        SocialPlatform.FACEBOOK -> PlatformDisplayInfo(
            name = "Facebook",
            emoji = "👥",
            color = Color(0xFF1877F2),
            description = "Share with friends",
        )
        SocialPlatform.PINTEREST -> PlatformDisplayInfo(
            name = "Pinterest",
            emoji = "📌",
            color = Color(0xFFBD081C),
            description = "Save party inspiration",
        )
    }
}
