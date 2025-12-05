package com.partygallery.ui.screens.auth.signup

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.partygallery.presentation.intent.SignUpIntent
import com.partygallery.presentation.store.SignUpStore
import com.partygallery.ui.components.PartyButton
import com.partygallery.ui.components.PartyButtonSize
import com.partygallery.ui.components.PartyButtonVariant
import com.partygallery.ui.components.PartyTextField
import com.partygallery.ui.theme.PartyGallerySpacing
import com.partygallery.ui.theme.PartyGalleryTypography
import com.partygallery.ui.theme.Theme

/**
 * Basic Info Screen - Step 1 of SignUp Flow
 *
 * S2-011: First step of signup flow
 *
 * Collects:
 * - Email
 * - Password & Confirm Password
 * - First Name & Last Name
 * - Username
 * - Birth Date (via date picker)
 *
 * Design: Dark Mode First (#0A0A0A background, #F59E0B accent)
 */
@Composable
fun BasicInfoScreen(signUpStore: SignUpStore, onNavigateToLogin: () -> Unit = {}) {
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
                stepNumber = 1,
                totalSteps = 6,
                title = "Create Account",
                subtitle = "Let's get you started with the basics",
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email Field
            PartyTextField(
                value = state.email,
                onValueChange = { signUpStore.processIntent(SignUpIntent.EmailChanged(it)) },
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
                onValueChange = { signUpStore.processIntent(SignUpIntent.PasswordChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = "Password",
                placeholder = "Create a password",
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
                            signUpStore.processIntent(SignUpIntent.TogglePasswordVisibility)
                        },
                    )
                },
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

            // Confirm Password Field
            PartyTextField(
                value = state.confirmPassword,
                onValueChange = { signUpStore.processIntent(SignUpIntent.ConfirmPasswordChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = "Confirm Password",
                placeholder = "Confirm your password",
                isError = !state.isPasswordMatch,
                errorMessage = state.confirmPasswordError,
                visualTransformation = if (state.isConfirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = ComposeKeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    Text(
                        text = if (state.isConfirmPasswordVisible) "Hide" else "Show",
                        style = PartyGalleryTypography.labelSmall,
                        color = colors.primary,
                        modifier = Modifier.clickable {
                            signUpStore.processIntent(SignUpIntent.ToggleConfirmPasswordVisibility)
                        },
                    )
                },
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.lg))

            // Name Fields Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PartyGallerySpacing.md),
            ) {
                PartyTextField(
                    value = state.firstName,
                    onValueChange = { signUpStore.processIntent(SignUpIntent.FirstNameChanged(it)) },
                    modifier = Modifier.weight(1f),
                    label = "First Name",
                    placeholder = "John",
                )
                PartyTextField(
                    value = state.lastName,
                    onValueChange = { signUpStore.processIntent(SignUpIntent.LastNameChanged(it)) },
                    modifier = Modifier.weight(1f),
                    label = "Last Name",
                    placeholder = "Doe",
                )
            }

            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

            // Username Field
            PartyTextField(
                value = state.username,
                onValueChange = { signUpStore.processIntent(SignUpIntent.UsernameChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = "Username",
                placeholder = "Choose a unique username",
                isError = !state.isUsernameValid || !state.isUsernameAvailable,
                errorMessage = state.usernameError,
                trailingIcon = {
                    when {
                        state.isCheckingUsername -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = colors.primary,
                                strokeWidth = 2.dp,
                            )
                        }
                        state.username.isNotEmpty() && state.isUsernameValid && state.isUsernameAvailable -> {
                            Text(
                                text = "Available",
                                style = PartyGalleryTypography.labelSmall,
                                color = colors.success,
                            )
                        }
                    }
                },
            )

            // Username hint
            Text(
                text = "3-20 characters, letters, numbers, and underscores only",
                style = PartyGalleryTypography.bodySmall,
                color = colors.onBackgroundVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = PartyGallerySpacing.sm, top = 4.dp),
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

            // Birth Date Field (simplified - would use date picker in production)
            BirthDateField(
                birthDate = state.birthDate,
                isError = !state.isBirthDateValid,
                errorMessage = state.birthDateError,
                onDateSelected = { date ->
                    signUpStore.processIntent(SignUpIntent.BirthDateChanged(date))
                },
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.xl))

            // Continue Button
            PartyButton(
                text = if (state.isLoading) "Creating Account..." else "Continue",
                onClick = { signUpStore.processIntent(SignUpIntent.SubmitBasicInfo) },
                modifier = Modifier.fillMaxWidth(),
                variant = PartyButtonVariant.PRIMARY,
                size = PartyButtonSize.LARGE,
                enabled = !state.isLoading && isBasicInfoValid(state),
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

            Spacer(modifier = Modifier.height(PartyGallerySpacing.lg))

            // Already have account link
            Row(
                modifier = Modifier.padding(vertical = PartyGallerySpacing.md),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Already have an account? ",
                    style = PartyGalleryTypography.bodyMedium,
                    color = colors.onBackgroundVariant,
                )
                Text(
                    text = "Sign In",
                    style = PartyGalleryTypography.labelMedium,
                    color = colors.primary,
                    modifier = Modifier.clickable { onNavigateToLogin() },
                )
            }

            Spacer(modifier = Modifier.height(PartyGallerySpacing.lg))
        }
    }
}

/**
 * Step header with progress indicator
 */
@Composable
fun SignUpStepHeader(stepNumber: Int, totalSteps: Int, title: String, subtitle: String) {
    val colors = Theme.colors

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Step indicator
        Text(
            text = "Step $stepNumber of $totalSteps",
            style = PartyGalleryTypography.labelMedium,
            color = colors.primary,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.sm))

        // Progress bar
        Row(
            modifier = Modifier.fillMaxWidth(0.5f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            repeat(totalSteps) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(
                            if (index < stepNumber) colors.primary else colors.surfaceVariant,
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.height(PartyGallerySpacing.lg))

        // Title
        Text(
            text = title,
            style = PartyGalleryTypography.headlineMedium,
            color = colors.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(PartyGallerySpacing.sm))

        // Subtitle
        Text(
            text = subtitle,
            style = PartyGalleryTypography.bodyMedium,
            color = colors.onBackgroundVariant,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Birth date field with date picker trigger
 * Note: In production, this would open a platform-specific date picker
 */
@Composable
private fun BirthDateField(
    birthDate: kotlinx.datetime.LocalDate?,
    isError: Boolean,
    errorMessage: String?,
    onDateSelected: (kotlinx.datetime.LocalDate) -> Unit,
) {
    val colors = Theme.colors
    val displayValue = birthDate?.let {
        "${it.monthNumber.toString().padStart(2, '0')}/${it.dayOfMonth.toString().padStart(2, '0')}/${it.year}"
    } ?: ""

    // For now, use a text field - in production would use platform date picker
    PartyTextField(
        value = displayValue,
        onValueChange = { input ->
            // Parse MM/DD/YYYY format
            val parts = input.split("/")
            if (parts.size == 3) {
                try {
                    val month = parts[0].toIntOrNull() ?: return@PartyTextField
                    val day = parts[1].toIntOrNull() ?: return@PartyTextField
                    val year = parts[2].toIntOrNull() ?: return@PartyTextField
                    if (month in 1..12 && day in 1..31 && year in 1900..2100) {
                        onDateSelected(kotlinx.datetime.LocalDate(year, month, day))
                    }
                } catch (_: Exception) {
                    // Invalid date
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        label = "Birth Date",
        placeholder = "MM/DD/YYYY",
        isError = isError,
        errorMessage = errorMessage,
        trailingIcon = {
            Text(
                text = "18+",
                style = PartyGalleryTypography.labelSmall,
                color = colors.onBackgroundVariant,
            )
        },
    )
}

/**
 * Check if basic info is valid for form submission
 */
private fun isBasicInfoValid(state: com.partygallery.presentation.state.SignUpState): Boolean {
    return state.email.isNotBlank() &&
        state.isEmailValid &&
        state.password.isNotBlank() &&
        state.isPasswordValid &&
        state.confirmPassword.isNotBlank() &&
        state.isPasswordMatch &&
        state.firstName.isNotBlank() &&
        state.username.isNotBlank() &&
        state.isUsernameValid &&
        state.isUsernameAvailable &&
        state.birthDate != null &&
        state.isBirthDateValid
}
