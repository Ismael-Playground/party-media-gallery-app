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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.partygallery.presentation.intent.LoginIntent
import com.partygallery.presentation.state.LoginEvent
import com.partygallery.presentation.state.LoginResult
import com.partygallery.presentation.store.LoginStore
import com.partygallery.ui.components.PartyButton
import com.partygallery.ui.components.PartyButtonSize
import com.partygallery.ui.components.PartyButtonVariant
import com.partygallery.ui.components.PartyTextField
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.PartyGalleryTypography
import com.partygallery.ui.theme.Theme

/**
 * Login Screen
 *
 * S2-006: LoginScreen UI following Dark Mode First design system.
 *
 * Features:
 * - Email/password authentication
 * - Social login (Google, Apple)
 * - Remember me option
 * - Forgot password navigation
 * - Sign up navigation
 *
 * @param loginStore MVI store for state management
 * @param onNavigateToHome Callback for navigation to home
 * @param onNavigateToSignUp Callback for navigation to sign up
 * @param onNavigateToForgotPassword Callback for navigation to forgot password
 */
@Composable
fun LoginScreen(
    loginStore: LoginStore,
    onNavigateToHome: () -> Unit = {},
    onNavigateToSignUp: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {},
    onShowError: (String) -> Unit = {},
) {
    val state by loginStore.state.collectAsState()
    val colors = Theme.colors

    // Handle one-time events
    LaunchedEffect(Unit) {
        loginStore.events.collect { event ->
            when (event) {
                is LoginEvent.NavigateToHome -> onNavigateToHome()
                is LoginEvent.NavigateToSignUp -> onNavigateToSignUp()
                is LoginEvent.NavigateToForgotPassword -> onNavigateToForgotPassword()
                is LoginEvent.ShowError -> onShowError(event.message)
                is LoginEvent.ShowEmailVerificationRequired ->
                    onShowError("Please verify your email before logging in")
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
            Spacer(modifier = Modifier.height(80.dp))

            // Logo & Brand
            LoginHeader()

            Spacer(modifier = Modifier.height(48.dp))

            // Email Field
            PartyTextField(
                value = state.email,
                onValueChange = { loginStore.processIntent(LoginIntent.EmailChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = "Email",
                placeholder = "Enter your email",
                isError = !state.isEmailValid,
                errorMessage = state.emailError,
                keyboardOptions = ComposeKeyboardOptions(keyboardType = KeyboardType.Email),
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

            // Password Field
            PartyTextField(
                value = state.password,
                onValueChange = { loginStore.processIntent(LoginIntent.PasswordChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = "Password",
                placeholder = "Enter your password",
                isError = !state.isPasswordValid,
                errorMessage = state.passwordError,
                visualTransformation = if (state.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = ComposeKeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    Text(
                        text = if (state.isPasswordVisible) "Hide" else "Show",
                        style = PartyGalleryTypography.labelSmall,
                        color = colors.primary,
                        modifier = Modifier.clickable {
                            loginStore.processIntent(LoginIntent.TogglePasswordVisibility)
                        },
                    )
                },
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.sm))

            // Remember me & Forgot password row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = state.rememberMe,
                        onCheckedChange = {
                            loginStore.processIntent(LoginIntent.RememberMeChanged(it))
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = colors.primary,
                            uncheckedColor = colors.onBackgroundVariant,
                            checkmarkColor = colors.onPrimary,
                        ),
                    )
                    Text(
                        text = "Remember me",
                        style = PartyGalleryTypography.bodyMedium,
                        color = colors.onBackgroundVariant,
                    )
                }

                Text(
                    text = "Forgot password?",
                    style = PartyGalleryTypography.bodyMedium,
                    color = colors.primary,
                    modifier = Modifier.clickable {
                        loginStore.processIntent(LoginIntent.NavigateToForgotPassword)
                    },
                )
            }

            Spacer(modifier = Modifier.height(PartyGallerySpacing.xl))

            // Login Button
            PartyButton(
                text = if (state.isLoading) "Signing in..." else "Sign In",
                onClick = { loginStore.processIntent(LoginIntent.SignInWithEmail) },
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

            // Divider with text
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                @Suppress("DEPRECATION")
                Divider(
                    modifier = Modifier.weight(1f),
                    color = colors.divider,
                )
                Text(
                    text = "  or continue with  ",
                    style = PartyGalleryTypography.bodySmall,
                    color = colors.onBackgroundVariant,
                )
                @Suppress("DEPRECATION")
                Divider(
                    modifier = Modifier.weight(1f),
                    color = colors.divider,
                )
            }

            Spacer(modifier = Modifier.height(PartyGallerySpacing.xl))

            // Social Login Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PartyGallerySpacing.md),
            ) {
                // Google Sign In
                SocialLoginButton(
                    text = "Google",
                    onClick = { loginStore.processIntent(LoginIntent.SignInWithGoogle) },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isLoading,
                )

                // Apple Sign In
                SocialLoginButton(
                    text = "Apple",
                    onClick = { loginStore.processIntent(LoginIntent.SignInWithApple) },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isLoading,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sign Up Link
            Row(
                modifier = Modifier.padding(vertical = PartyGallerySpacing.xl),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Don't have an account? ",
                    style = PartyGalleryTypography.bodyMedium,
                    color = colors.onBackgroundVariant,
                )
                Text(
                    text = "Sign Up",
                    style = PartyGalleryTypography.labelMedium,
                    color = colors.primary,
                    modifier = Modifier.clickable {
                        loginStore.processIntent(LoginIntent.NavigateToSignUp)
                    },
                )
            }

            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))
        }

        // Error overlay
        if (state.loginResult is LoginResult.Error) {
            // Error is handled via events, but could show snackbar/toast here
        }
    }
}

/**
 * Login Header with branding
 */
@Composable
private fun LoginHeader() {
    val colors = Theme.colors

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // App Name with accent color
        Text(
            text = "Party",
            style = PartyGalleryTypography.displayLarge,
            color = colors.primary,
        )
        Text(
            text = "Gallery",
            style = PartyGalleryTypography.displayMedium,
            color = colors.onBackground,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

        Text(
            text = "Capture and share party moments",
            style = PartyGalleryTypography.bodyLarge,
            color = colors.onBackgroundVariant,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Social login button component
 */
@Composable
private fun SocialLoginButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    PartyButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        variant = PartyButtonVariant.SECONDARY,
        size = PartyButtonSize.MEDIUM,
        enabled = enabled,
    )
}
