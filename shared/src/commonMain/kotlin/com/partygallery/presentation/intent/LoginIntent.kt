package com.partygallery.presentation.intent

/**
 * Login Screen Intents
 *
 * S2-004: LoginIntent sealed class for MVI pattern
 *
 * Represents all user actions that can occur on the login screen.
 * Each intent triggers a state change in the LoginStore.
 */
sealed class LoginIntent {

    // ============================================
    // Input Field Changes
    // ============================================

    data class EmailChanged(val email: String) : LoginIntent()
    data class PasswordChanged(val password: String) : LoginIntent()

    // ============================================
    // UI Actions
    // ============================================

    data object TogglePasswordVisibility : LoginIntent()
    data class RememberMeChanged(val remember: Boolean) : LoginIntent()

    // ============================================
    // Authentication Actions
    // ============================================

    data object SignInWithEmail : LoginIntent()
    data object SignInWithGoogle : LoginIntent()
    data object SignInWithApple : LoginIntent()

    // ============================================
    // Navigation Actions
    // ============================================

    data object NavigateToSignUp : LoginIntent()
    data object NavigateToForgotPassword : LoginIntent()

    // ============================================
    // State Management
    // ============================================

    data object ClearError : LoginIntent()
    data object ResetState : LoginIntent()
}
