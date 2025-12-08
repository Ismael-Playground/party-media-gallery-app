package com.partygallery.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.partygallery.presentation.intent.ForgotPasswordIntent
import com.partygallery.presentation.state.ForgotPasswordEvent
import com.partygallery.presentation.state.ForgotPasswordResult
import com.partygallery.presentation.store.ForgotPasswordStore
import com.partygallery.ui.components.PartyButton
import com.partygallery.ui.components.PartyButtonSize
import com.partygallery.ui.components.PartyButtonVariant
import com.partygallery.ui.components.PartyTextField
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.PartyGalleryTypography
import com.partygallery.ui.theme.Theme

/**
 * Forgot Password Screen
 *
 * S2-007: ForgotPasswordScreen UI following Dark Mode First design system.
 *
 * Features:
 * - Email input for password reset
 * - Back navigation to login
 * - Success/error feedback
 *
 * @param forgotPasswordStore MVI store for state management
 * @param onNavigateBack Callback for navigation back to login
 * @param onShowSuccess Callback for showing success message
 * @param onShowError Callback for showing error message
 */
@Composable
fun ForgotPasswordScreen(
    forgotPasswordStore: ForgotPasswordStore,
    onNavigateBack: () -> Unit = {},
    onShowSuccess: (String) -> Unit = {},
    onShowError: (String) -> Unit = {},
) {
    val state by forgotPasswordStore.state.collectAsState()
    val colors = Theme.colors

    // Handle one-time events
    LaunchedEffect(Unit) {
        forgotPasswordStore.events.collect { event ->
            when (event) {
                is ForgotPasswordEvent.NavigateBack -> onNavigateBack()
                is ForgotPasswordEvent.NavigateToLogin -> onNavigateBack()
                is ForgotPasswordEvent.ShowError -> onShowError(event.message)
                is ForgotPasswordEvent.ShowSuccess -> onShowSuccess(event.message)
            }
        }
    }

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
            Spacer(modifier = Modifier.height(PartyGallerySpacing.lg))

            // Back button row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "← Back",
                    style = PartyGalleryTypography.labelMedium,
                    color = colors.primary,
                    modifier = Modifier.clickable {
                        forgotPasswordStore.processIntent(ForgotPasswordIntent.NavigateBack)
                    },
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Header
            ForgotPasswordHeader()

            Spacer(modifier = Modifier.height(48.dp))

            // Show success state or form
            if (state.result is ForgotPasswordResult.Success) {
                SuccessContent(
                    email = state.email,
                    onBackToLogin = {
                        forgotPasswordStore.processIntent(ForgotPasswordIntent.NavigateToLogin)
                    },
                )
            } else {
                // Email Field
                PartyTextField(
                    value = state.email,
                    onValueChange = {
                        forgotPasswordStore.processIntent(ForgotPasswordIntent.EmailChanged(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = "Email",
                    placeholder = "Enter your email address",
                    isError = !state.isEmailValid,
                    errorMessage = state.emailError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )

                Spacer(modifier = Modifier.height(PartyGallerySpacing.xl))

                // Send Reset Email Button
                PartyButton(
                    text = if (state.isLoading) "Sending..." else "Send Reset Link",
                    onClick = {
                        forgotPasswordStore.processIntent(ForgotPasswordIntent.SendResetEmail)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    variant = PartyButtonVariant.PRIMARY,
                    size = PartyButtonSize.LARGE,
                    enabled = !state.isLoading,
                    leadingIcon = if (state.isLoading) {
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

                Spacer(modifier = Modifier.height(PartyGallerySpacing.xl))

                // Back to Login Link
                Text(
                    text = "Back to Sign In",
                    style = PartyGalleryTypography.labelMedium,
                    color = colors.primary,
                    modifier = Modifier.clickable {
                        forgotPasswordStore.processIntent(ForgotPasswordIntent.NavigateBack)
                    },
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(PartyGallerySpacing.xl))
        }
    }
}

/**
 * Forgot Password Header with title and description
 */
@Composable
private fun ForgotPasswordHeader() {
    val colors = Theme.colors

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Forgot Password?",
            style = PartyGalleryTypography.displayMedium,
            color = colors.onBackground,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

        Text(
            text = "Don't worry! Enter your email address and we'll send you a link to reset your password.",
            style = PartyGalleryTypography.bodyLarge,
            color = colors.onBackgroundVariant,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Success content shown after email is sent
 */
@Composable
private fun SuccessContent(
    email: String,
    onBackToLogin: () -> Unit,
) {
    val colors = Theme.colors

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Success icon (using text emoji as placeholder)
        Text(
            text = "✓",
            style = PartyGalleryTypography.displayLarge,
            color = colors.primary,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.lg))

        Text(
            text = "Email Sent!",
            style = PartyGalleryTypography.headlineMedium,
            color = colors.onBackground,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

        Text(
            text = "We've sent a password reset link to:",
            style = PartyGalleryTypography.bodyLarge,
            color = colors.onBackgroundVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.sm))

        Text(
            text = email,
            style = PartyGalleryTypography.labelLarge,
            color = colors.primary,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

        Text(
            text = "Check your inbox and follow the instructions to reset your password.",
            style = PartyGalleryTypography.bodyMedium,
            color = colors.onBackgroundVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.xl))

        PartyButton(
            text = "Back to Sign In",
            onClick = onBackToLogin,
            modifier = Modifier.fillMaxWidth(),
            variant = PartyButtonVariant.PRIMARY,
            size = PartyButtonSize.LARGE,
        )
    }
}
