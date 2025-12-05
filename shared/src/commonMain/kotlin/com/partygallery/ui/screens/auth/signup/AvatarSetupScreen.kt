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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
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
 * Avatar Setup Screen - Step 2 of SignUp Flow
 *
 * S2-012: Second step of signup flow
 *
 * Allows user to:
 * - Take a photo with camera
 * - Choose from gallery
 * - Skip for now
 *
 * Design: Dark Mode First with amber accent gradient border
 */
@Composable
fun AvatarSetupScreen(signUpStore: SignUpStore, onBackPressed: () -> Unit = {}) {
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
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Header
            SignUpStepHeader(
                stepNumber = 2,
                totalSteps = 6,
                title = "Add a Profile Photo",
                subtitle = "Help your friends recognize you at parties",
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Avatar Preview
            AvatarPreview(
                avatarUri = state.avatarUri,
                isUploading = state.isUploadingAvatar,
                firstName = state.firstName,
                onRemove = { signUpStore.processIntent(SignUpIntent.RemoveAvatar) },
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PartyGallerySpacing.md),
            ) {
                // Camera Button
                PartyButton(
                    text = "Camera",
                    onClick = { signUpStore.processIntent(SignUpIntent.OpenCamera) },
                    modifier = Modifier.weight(1f),
                    variant = PartyButtonVariant.SECONDARY,
                    size = PartyButtonSize.MEDIUM,
                    enabled = !state.isUploadingAvatar,
                )

                // Gallery Button
                PartyButton(
                    text = "Gallery",
                    onClick = { signUpStore.processIntent(SignUpIntent.OpenGallery) },
                    modifier = Modifier.weight(1f),
                    variant = PartyButtonVariant.SECONDARY,
                    size = PartyButtonSize.MEDIUM,
                    enabled = !state.isUploadingAvatar,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Continue Button
            PartyButton(
                text = when {
                    state.isUploadingAvatar -> "Uploading..."
                    state.avatarUri != null -> "Continue"
                    else -> "Continue"
                },
                onClick = { signUpStore.processIntent(SignUpIntent.UploadAvatar) },
                modifier = Modifier.fillMaxWidth(),
                variant = PartyButtonVariant.PRIMARY,
                size = PartyButtonSize.LARGE,
                enabled = !state.isUploadingAvatar,
                leadingIcon = if (state.isUploadingAvatar) {
                    {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = colors.onPrimary,
                            strokeWidth = 2.dp,
                        )
                    }
                } else {
                    null
                },
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

            // Skip Button
            Text(
                text = "Skip for now",
                style = PartyGalleryTypography.labelMedium,
                color = colors.onBackgroundVariant,
                modifier = Modifier
                    .clickable(enabled = !state.isUploadingAvatar) {
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
                    .clickable(enabled = !state.isUploadingAvatar) {
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
 * Avatar preview with gradient border
 */
@Composable
private fun AvatarPreview(avatarUri: String?, isUploading: Boolean, firstName: String, onRemove: () -> Unit) {
    val colors = Theme.colors

    Box(
        contentAlignment = Alignment.Center,
    ) {
        // Avatar circle with gradient border
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .border(
                    width = 3.dp,
                    color = colors.primary,
                    shape = CircleShape,
                )
                .background(colors.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = colors.primary,
                    strokeWidth = 3.dp,
                )
            } else if (avatarUri != null) {
                // In production, use Coil/Kamel to load image from URI
                // For now, show placeholder with checkmark
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Photo",
                        style = PartyGalleryTypography.headlineMedium,
                        color = colors.primary,
                    )
                    Text(
                        text = "Selected",
                        style = PartyGalleryTypography.bodySmall,
                        color = colors.success,
                    )
                }
            } else {
                // Default avatar with initials
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = firstName.take(1).uppercase().ifEmpty { "?" },
                        style = PartyGalleryTypography.displayLarge,
                        color = colors.primary,
                    )
                    Text(
                        text = "Add Photo",
                        style = PartyGalleryTypography.bodySmall,
                        color = colors.onBackgroundVariant,
                    )
                }
            }
        }

        // Remove button (if avatar selected)
        if (avatarUri != null && !isUploading) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colors.error)
                    .clickable { onRemove() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "X",
                    style = PartyGalleryTypography.labelSmall,
                    color = colors.onPrimary,
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

    // Tips
    Text(
        text = "Tips: Use a clear photo of your face for best results",
        style = PartyGalleryTypography.bodySmall,
        color = colors.onBackgroundVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = PartyGallerySpacing.xl),
    )
}
