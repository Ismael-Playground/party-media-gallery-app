package com.partygallery.presentation.intent

/**
 * Forgot Password Screen Intents
 *
 * S2-007: ForgotPasswordIntent sealed class for MVI pattern
 *
 * Represents all user actions that can occur on the forgot password screen.
 * Each intent triggers a state change in the ForgotPasswordStore.
 */
sealed class ForgotPasswordIntent {

    // ============================================
    // Input Field Changes
    // ============================================

    data class EmailChanged(val email: String) : ForgotPasswordIntent()

    // ============================================
    // Actions
    // ============================================

    data object SendResetEmail : ForgotPasswordIntent()

    // ============================================
    // Navigation Actions
    // ============================================

    data object NavigateBack : ForgotPasswordIntent()
    data object NavigateToLogin : ForgotPasswordIntent()

    // ============================================
    // State Management
    // ============================================

    data object ClearError : ForgotPasswordIntent()
    data object ResetState : ForgotPasswordIntent()
}
