package com.partygallery

import androidx.compose.ui.window.ComposeUIViewController
import com.partygallery.data.auth.FirebaseAuthService
import com.partygallery.data.repository.AuthRepositoryImpl
import com.partygallery.presentation.intent.SignUpIntent
import com.partygallery.presentation.state.SignUpEvent
import com.partygallery.presentation.state.SignUpStep
import com.partygallery.presentation.store.SignUpStore
import com.partygallery.ui.theme.PartyGalleryTheme
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.partygallery.ui.components.PartyButton
import com.partygallery.ui.components.PartyButtonSize
import com.partygallery.ui.components.PartyButtonVariant
import com.partygallery.ui.components.PartyTextField
import com.partygallery.ui.screens.auth.signup.*
import com.partygallery.ui.screens.main.MainScreen
import com.partygallery.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * MainViewController for iOS
 *
 * Creates a ComposeUIViewController that hosts the Party Gallery app
 * using Compose Multiplatform for iOS.
 */
fun MainViewController() = ComposeUIViewController {
    PartyGalleryTheme(darkTheme = true) {
        PartyGalleryApp()
    }
}

/**
 * Screen navigation enum
 */
private enum class Screen {
    LOGIN,
    SIGNUP,
    HOME,
}

/**
 * Main Party Gallery App Composable
 *
 * Shared app logic for iOS, mirroring the Desktop implementation
 */
@Composable
private fun PartyGalleryApp() {
    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }
    var loggedInEmail by remember { mutableStateOf<String?>(null) }

    // Create mock auth and signup store
    val authRepository = remember { AuthRepositoryImpl(FirebaseAuthService()) }
    val signUpStore = remember { SignUpStore(authRepository) }
    val signUpState by signUpStore.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            Screen.LOGIN -> {
                SimpleLoginScreen(
                    onLoginSuccess = { email ->
                        loggedInEmail = email
                        currentScreen = Screen.HOME
                    },
                    onNavigateToSignUp = {
                        currentScreen = Screen.SIGNUP
                    },
                )
            }
            Screen.SIGNUP -> {
                SignUpFlowScreen(
                    signUpStore = signUpStore,
                    currentStep = signUpState.currentStep,
                    onBackToLogin = {
                        currentScreen = Screen.LOGIN
                    },
                    onSignUpComplete = {
                        loggedInEmail = signUpState.email
                        currentScreen = Screen.HOME
                    },
                )
            }
            Screen.HOME -> {
                val displayName = signUpState.firstName.ifEmpty {
                    loggedInEmail?.substringBefore("@")
                        ?.replaceFirstChar { it.uppercase() }
                        ?: "User"
                }
                MainScreen(
                    userFirstName = displayName,
                    userEmail = loggedInEmail ?: "",
                    onLogout = {
                        currentScreen = Screen.LOGIN
                        loggedInEmail = null
                        signUpStore.processIntent(SignUpIntent.ResetFlow)
                    },
                )
            }
        }
    }
}

/**
 * SignUp Flow Container - Shows current step
 */
@Composable
private fun SignUpFlowScreen(
    signUpStore: SignUpStore,
    currentStep: SignUpStep,
    onBackToLogin: () -> Unit,
    onSignUpComplete: () -> Unit,
) {
    val state by signUpStore.state.collectAsState()

    // Watch for completion
    LaunchedEffect(currentStep) {
        if (currentStep == SignUpStep.COMPLETION) {
            // Auto-navigate after completion screen button click
        }
    }

    // Collect events
    LaunchedEffect(Unit) {
        signUpStore.events.collect { event ->
            when (event) {
                is SignUpEvent.NavigateToHome -> {
                    onSignUpComplete()
                }
                else -> { /* Handle other events */ }
            }
        }
    }

    AnimatedContent(
        targetState = currentStep,
        transitionSpec = {
            slideInHorizontally { width -> width } + fadeIn() togetherWith
                slideOutHorizontally { width -> -width } + fadeOut()
        },
    ) { step ->
        when (step) {
            SignUpStep.BASIC_INFO -> BasicInfoScreen(
                signUpStore = signUpStore,
                onNavigateToLogin = onBackToLogin,
            )
            SignUpStep.AVATAR_SETUP -> AvatarSetupScreen(
                signUpStore = signUpStore,
                onBackPressed = {},
            )
            SignUpStep.CONTACT_SYNC -> ContactSyncScreen(
                signUpStore = signUpStore,
                onBackPressed = {},
            )
            SignUpStep.INTEREST_TAGS -> InterestTagsScreen(
                signUpStore = signUpStore,
                onBackPressed = {},
            )
            SignUpStep.SOCIAL_LINKING -> SocialLinkingScreen(
                signUpStore = signUpStore,
                onBackPressed = {},
            )
            SignUpStep.COMPLETION -> CompletionScreen(
                signUpStore = signUpStore,
            )
        }
    }
}

@Composable
private fun SimpleLoginScreen(onLoginSuccess: (String) -> Unit, onNavigateToSignUp: () -> Unit) {
    val colors = Theme.colors
    val scope = rememberCoroutineScope()

    // Local state
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Validation
    fun validateEmail(e: String): Boolean {
        val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return e.matches(regex)
    }

    fun validatePassword(p: String): Boolean = p.length >= 6

    // Sign in action
    fun signIn() {
        emailError = null
        passwordError = null
        errorMessage = null
        successMessage = null

        if (!validateEmail(email)) {
            emailError = "Please enter a valid email"
            return
        }
        if (!validatePassword(password)) {
            passwordError = "Password must be at least 6 characters"
            return
        }

        isLoading = true

        scope.launch {
            delay(1500) // Simulate network delay
            isLoading = false
            successMessage = "Welcome! Login successful"
            delay(800)
            onLoginSuccess(email)
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
            Spacer(modifier = Modifier.height(60.dp))

            // Logo
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

            Spacer(modifier = Modifier.height(40.dp))

            // Success message
            if (successMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.success.copy(alpha = 0.2f))
                        .padding(16.dp),
                ) {
                    Text(
                        text = successMessage!!,
                        style = PartyGalleryTypography.bodyMedium,
                        color = colors.success,
                    )
                }
                Spacer(modifier = Modifier.height(PartyGallerySpacing.md))
            }

            // Error message
            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.error.copy(alpha = 0.2f))
                        .padding(16.dp),
                ) {
                    Text(
                        text = errorMessage!!,
                        style = PartyGalleryTypography.bodyMedium,
                        color = colors.error,
                    )
                }
                Spacer(modifier = Modifier.height(PartyGallerySpacing.md))
            }

            // Email Field
            PartyTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                modifier = Modifier.fillMaxWidth(),
                label = "Email",
                placeholder = "Enter your email",
                isError = emailError != null,
                errorMessage = emailError,
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))

            // Password Field
            PartyTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                modifier = Modifier.fillMaxWidth(),
                label = "Password",
                placeholder = "Enter your password",
                isError = passwordError != null,
                errorMessage = passwordError,
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    Text(
                        text = if (isPasswordVisible) "Hide" else "Show",
                        style = PartyGalleryTypography.labelSmall,
                        color = colors.primary,
                        modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible },
                    )
                },
            )

            Spacer(modifier = Modifier.height(PartyGallerySpacing.sm))

            // Remember me & Forgot password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
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
                        errorMessage = "Forgot password flow not implemented yet"
                    },
                )
            }

            Spacer(modifier = Modifier.height(PartyGallerySpacing.xl))

            // Login Button
            PartyButton(
                text = if (isLoading) "Signing in..." else "Sign In",
                onClick = { signIn() },
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

            Spacer(modifier = Modifier.height(PartyGallerySpacing.xl))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                @Suppress("DEPRECATION")
                Divider(modifier = Modifier.weight(1f), color = colors.divider)
                Text(
                    text = "  or continue with  ",
                    style = PartyGalleryTypography.bodySmall,
                    color = colors.onBackgroundVariant,
                )
                @Suppress("DEPRECATION")
                Divider(modifier = Modifier.weight(1f), color = colors.divider)
            }

            Spacer(modifier = Modifier.height(PartyGallerySpacing.xl))

            // Social buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PartyGallerySpacing.md),
            ) {
                PartyButton(
                    text = "Google",
                    onClick = { errorMessage = "Google Sign-In coming soon" },
                    modifier = Modifier.weight(1f),
                    variant = PartyButtonVariant.SECONDARY,
                    size = PartyButtonSize.MEDIUM,
                    enabled = !isLoading,
                )
                PartyButton(
                    text = "Apple",
                    onClick = { errorMessage = "Apple Sign-In coming soon" },
                    modifier = Modifier.weight(1f),
                    variant = PartyButtonVariant.SECONDARY,
                    size = PartyButtonSize.MEDIUM,
                    enabled = !isLoading,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sign Up link
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
                    modifier = Modifier.clickable { onNavigateToSignUp() },
                )
            }

            Spacer(modifier = Modifier.height(PartyGallerySpacing.md))
        }
    }
}
