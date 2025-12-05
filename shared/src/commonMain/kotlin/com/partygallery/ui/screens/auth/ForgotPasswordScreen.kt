package com.partygallery.ui.screens.auth

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions as ComposeKeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
 * - Send reset link functionality
 * - Resend link with cooldown timer
 * - Success/error states
 * - Back navigation
 *
 * @param forgotPasswordStore MVI store for state management
 * @param onNavigateBack Callback for navigation back
 * @param onNavigateToLogin Callback for navigation to login
 * @param onShowError Callback for showing error messages
 * @param onShowSuccess Callback for showing success messages
 */
@Composable
fun ForgotPasswordScreen(
    forgotPasswordStore: ForgotPasswordStore,
    onNavigateBack: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onShowError: (String) -> Unit = {},
    onShowSuccess: (String) -> Unit = {},
) {
    val state by forgotPasswordStore.state.collectAsState()
    val colors = Theme.colors

    // Handle one-time events
    LaunchedEffect(Unit) {
        forgotPasswordStore.events.collect { event ->
            when (event) {
                is ForgotPasswordEvent.NavigateBack -> onNavigateBack()
                is ForgotPasswordEvent.NavigateToLogin -> onNavigateToLogin()
                is ForgotPasswordEvent.ShowError -> onShowError(event.message)
                is ForgotPasswordEvent.ShowEmailSent ->
                    onShowSuccess("Password reset link sent to your email")
                is ForgotPasswordEvent.ShowResendSuccess ->
                    onShowSuccess("Reset link sent again")
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
            Spacer(modifier = Modifier.height(PartyGallerySpacing.xl))

            // Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
            ) {
                Text(
                    text = "< Back",
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

            // Show different content based on state
            when (state.resetResult) {
                is ForgotPasswordResult.Success -> {
                    // Success state
                    SuccessContent(
                        email = state.email,
                        canResend = state.canResend,
                        resendCountdown = state.resendCountdown,
                        isLoading = state.isLoading,
                        onResend = {
                            forgotPasswordStore.processIntent(ForgotPasswordIntent.ResendResetLink)
                        },
                        onBackToLogin = {
                            forgotPasswordStore.processIntent(ForgotPasswordIntent.NavigateToLogin)
                        },
                    )
                }
                else -> {
                    // Input state
                    InputContent(
                        email = state.email,
                        isEmailValid = state.isEmailValid,
                        emailError = state.emailError,
                        isLoading = state.isLoading,
                        onEmailChange = { email ->
                            forgotPasswordStore.processIntent(
                                ForgotPasswordIntent.EmailChanged(email),
                            )
                        },
                        onSendResetLink = {
                            forgotPasswordStore.processIntent(ForgotPasswordIntent.SendResetLink)
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Remember password link
            Row(
                modifier = Modifier.padding(vertical = PartyGallerySpacing.xl),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Remember your password? ",
                    style = PartyGalleryTypography.bodyMedium,
                    color = colors.onBackgroundVariant,
                )
                Text(
                    text = "Sign In",
                    style = PartyGalleryTypography.labelMedium,
                    color = colors.primary,
                    modifier = Modifier.clickable {
                        forgotPasswordStore.processIntent(ForgotPasswordIntent.NavigateToLogin)
                    },
                )
            }

            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))
        }
    }
}

/**
 * Header with icon and text
 */
@Composable
private fun ForgotPasswordHeader() {
    val colors = Theme.colors

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Lock icon placeholder (using text for now)
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(colors.surfaceVariant, shape = androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "🔒",
                style = PartyGalleryTypography.displayMedium,
            )
        }

        Spacer(modifier = Modifier.height(PartyGallerySpacing.lg))

        Text(
            text = "Forgot Password?",
            style = PartyGalleryTypography.headlineLarge,
            color = colors.onBackground,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.sm))

        Text(
            text = "Enter your email address and we'll send you\na link to reset your password",
            style = PartyGalleryTypography.bodyLarge,
            color = colors.onBackgroundVariant,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Input content with email field and send button
 */
@Composable
private fun InputContent(
    email: String,
    isEmailValid: Boolean,
    emailError: String?,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onSendResetLink: () -> Unit,
) {
    val colors = Theme.colors

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Email Field
        PartyTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = "Email",
            placeholder = "Enter your email",
            isError = !isEmailValid,
            errorMessage = emailError,
            keyboardOptions = ComposeKeyboardOptions(keyboardType = KeyboardType.Email),
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.xl))

        // Send Reset Link Button
        PartyButton(
            text = if (isLoading) "Sending..." else "Send Reset Link",
            onClick = onSendResetLink,
            modifier = Modifier.fillMaxWidth(),
            variant = PartyButtonVariant.PRIMARY,
            size = PartyButtonSize.LARGE,
            enabled = !isLoading,
            leadingIcon = if (isLoading) {
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
    }
}

/**
 * Success content with confirmation and resend option
 */
@Composable
private fun SuccessContent(
    email: String,
    canResend: Boolean,
    resendCountdown: Int,
    isLoading: Boolean,
    onResend: () -> Unit,
    onBackToLogin: () -> Unit,
) {
    val colors = Theme.colors

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Success icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(colors.primary.copy(alpha = 0.1f), shape = androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "✉️",
                style = PartyGalleryTypography.displayMedium,
            )
        }

        Spacer(modifier = Modifier.height(PartyGallerySpacing.lg))

        Text(
            text = "Check your email",
            style = PartyGalleryTypography.headlineMedium,
            color = colors.onBackground,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.sm))

        Text(
            text = "We've sent a password reset link to",
            style = PartyGalleryTypography.bodyLarge,
            color = colors.onBackgroundVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.xs))

        Text(
            text = email,
            style = PartyGalleryTypography.labelLarge,
            color = colors.primary,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.xl))

        // Back to Login Button
        PartyButton(
            text = "Back to Login",
            onClick = onBackToLogin,
            modifier = Modifier.fillMaxWidth(),
            variant = PartyButtonVariant.PRIMARY,
            size = PartyButtonSize.LARGE,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.lg))

        // Resend link
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Didn't receive the email? ",
                style = PartyGalleryTypography.bodyMedium,
                color = colors.onBackgroundVariant,
            )

            if (canResend) {
                Text(
                    text = if (isLoading) "Sending..." else "Resend",
                    style = PartyGalleryTypography.labelMedium,
                    color = if (isLoading) colors.onBackgroundVariant else colors.primary,
                    modifier = Modifier.clickable(enabled = !isLoading) {
                        onResend()
                    },
                )
            } else {
                Text(
                    text = "Resend in ${resendCountdown}s",
                    style = PartyGalleryTypography.bodyMedium,
                    color = colors.onBackgroundVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

        // Help text
        Text(
            text = "Check your spam folder if you don't see it",
            style = PartyGalleryTypography.bodySmall,
            color = colors.onBackgroundVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )
    }
}
