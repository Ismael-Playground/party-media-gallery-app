package com.partygallery.presentation.state

/**
 * Login Screen State
 *
 * S2-003: LoginState for MVI pattern
 *
 * Represents the complete UI state of the login screen.
 * Follows Dark Mode First design principles.
 */
data class LoginState(
    val email: String = "",
    val password: String = "",
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val rememberMe: Boolean = false,
    val loginResult: LoginResult = LoginResult.Idle,
)

/**
 * Result of login operation.
 */
sealed class LoginResult {
    data object Idle : LoginResult()
    data object Loading : LoginResult()
    data class Success(val userId: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
    data object EmailNotVerified : LoginResult()
}

/**
 * One-time events triggered by login operations.
 */
sealed class LoginEvent {
    data object NavigateToHome : LoginEvent()
    data object NavigateToSignUp : LoginEvent()
    data object NavigateToForgotPassword : LoginEvent()
    data class ShowError(val message: String) : LoginEvent()
    data object ShowEmailVerificationRequired : LoginEvent()
}
