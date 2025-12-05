package com.partygallery.ui.screens.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.partygallery.presentation.intent.SignUpIntent
import com.partygallery.presentation.store.SignUpStore
import com.partygallery.ui.components.PartyButton
import com.partygallery.ui.components.PartyButtonSize
import com.partygallery.ui.components.PartyButtonVariant
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.PartyGalleryTypography
import com.partygallery.ui.theme.Theme

/**
 * Completion Screen - Step 6 (Final) of SignUp Flow
 *
 * S2-016: Final step of signup flow
 *
 * Shows:
 * - Success animation/icon
 * - Welcome message
 * - Summary of profile setup
 * - CTA to start exploring
 */
@Composable
fun CompletionScreen(
    signUpStore: SignUpStore,
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
                .padding(horizontal = PartyGallerySpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Success Icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(colors.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "🎉",
                    style = PartyGalleryTypography.displayLarge,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Welcome message
            Text(
                text = "Welcome to Party Gallery!",
                style = PartyGalleryTypography.headlineLarge,
                color = colors.onBackground,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

            Text(
                text = "Hey ${state.firstName}! 👋",
                style = PartyGalleryTypography.titleLarge,
                color = colors.primary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.lg))

            Text(
                text = "Your account is all set up and ready to go. " +
                    "Start capturing and sharing your best party moments!",
                style = PartyGalleryTypography.bodyLarge,
                color = colors.onBackgroundVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = PartyGallerySpacing.lg),
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Profile summary
            ProfileSummary(
                tagsCount = state.selectedTags.size,
                socialLinksCount = state.socialLinks.linkedCount,
                hasAvatar = state.avatarUri != null,
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Start Button
            PartyButton(
                text = "Let's Party! 🎊",
                onClick = { signUpStore.processIntent(SignUpIntent.CompleteSignUp) },
                modifier = Modifier.fillMaxWidth(),
                variant = PartyButtonVariant.PRIMARY,
                size = PartyButtonSize.LARGE,
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.xl))
        }
    }
}

@Composable
private fun ProfileSummary(
    tagsCount: Int,
    socialLinksCount: Int,
    hasAvatar: Boolean,
) {
    val colors = Theme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .background(colors.surfaceVariant)
            .padding(PartyGallerySpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Your Profile",
            style = PartyGalleryTypography.titleMedium,
            color = colors.onBackground,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

        SummaryItem(
            emoji = if (hasAvatar) "✅" else "⏭️",
            text = if (hasAvatar) "Profile photo added" else "Profile photo skipped",
        )

        SummaryItem(
            emoji = "🎵",
            text = "$tagsCount interests selected",
        )

        SummaryItem(
            emoji = if (socialLinksCount > 0) "🔗" else "⏭️",
            text = if (socialLinksCount > 0) {
                "$socialLinksCount social accounts linked"
            } else {
                "Social linking skipped"
            },
        )
    }
}

@Composable
private fun SummaryItem(
    emoji: String,
    text: String,
) {
    val colors = Theme.colors

    Text(
        text = "$emoji $text",
        style = PartyGalleryTypography.bodyMedium,
        color = colors.onBackgroundVariant,
        modifier = Modifier.padding(vertical = 4.dp),
    )
}
