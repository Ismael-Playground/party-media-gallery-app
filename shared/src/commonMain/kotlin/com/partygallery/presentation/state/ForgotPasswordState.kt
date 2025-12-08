package com.partygallery.presentation.state

/**
 * Forgot Password Screen State
 *
 * S2-007: ForgotPasswordState for MVI pattern
 *
 * Represents the complete UI state of the forgot password screen.
 * Follows Dark Mode First design principles.
 */
data class ForgotPasswordState(
    val email: String = "",
    val isEmailValid: Boolean = true,
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val result: ForgotPasswordResult = ForgotPasswordResult.Idle,
)

/**
 * Result of forgot password operation.
 */
sealed class ForgotPasswordResult {
    data object Idle : ForgotPasswordResult()
    data object Loading : ForgotPasswordResult()
    data object Success : ForgotPasswordResult()
    data class Error(val message: String) : ForgotPasswordResult()
}

/**
 * One-time events triggered by forgot password operations.
 */
sealed class ForgotPasswordEvent {
    data object NavigateBack : ForgotPasswordEvent()
    data object NavigateToLogin : ForgotPasswordEvent()
    data class ShowError(val message: String) : ForgotPasswordEvent()
    data class ShowSuccess(val message: String) : ForgotPasswordEvent()
}
