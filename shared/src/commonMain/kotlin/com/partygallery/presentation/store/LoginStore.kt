package com.partygallery.presentation.store

import com.partygallery.domain.repository.AuthRepository
import com.partygallery.presentation.intent.LoginIntent
import com.partygallery.presentation.state.LoginEvent
import com.partygallery.presentation.state.LoginResult
import com.partygallery.presentation.state.LoginState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Login Store - MVI State Management
 *
 * S2-003: LoginStore with MVI pattern
 *
 * Manages login screen state following unidirectional data flow:
 * Intent -> Store -> State -> UI -> Intent
 *
 * @param authRepository Repository for authentication operations
 */
class LoginStore(
    private val authRepository: AuthRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    /**
     * Process user intents and update state accordingly.
     */
    fun processIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> handleEmailChanged(intent.email)
            is LoginIntent.PasswordChanged -> handlePasswordChanged(intent.password)
            is LoginIntent.TogglePasswordVisibility -> handleTogglePasswordVisibility()
            is LoginIntent.RememberMeChanged -> handleRememberMeChanged(intent.remember)
            is LoginIntent.SignInWithEmail -> handleSignInWithEmail()
            is LoginIntent.SignInWithGoogle -> handleSignInWithGoogle()
            is LoginIntent.SignInWithApple -> handleSignInWithApple()
            is LoginIntent.NavigateToSignUp -> handleNavigateToSignUp()
            is LoginIntent.NavigateToForgotPassword -> handleNavigateToForgotPassword()
            is LoginIntent.ClearError -> handleClearError()
            is LoginIntent.ResetState -> handleResetState()
        }
    }

    // ============================================
    // Input Handlers
    // ============================================

    private fun handleEmailChanged(email: String) {
        val isValid = validateEmail(email)
        _state.value = _state.value.copy(
            email = email,
            isEmailValid = isValid || email.isEmpty(),
            emailError = if (!isValid && email.isNotEmpty()) "Invalid email format" else null,
        )
    }

    private fun handlePasswordChanged(password: String) {
        val isValid = validatePassword(password)
        _state.value = _state.value.copy(
            password = password,
            isPasswordValid = isValid || password.isEmpty(),
            passwordError = if (!isValid && password.isNotEmpty()) "Password must be at least 6 characters" else null,
        )
    }

    // ============================================
    // UI Action Handlers
    // ============================================

    private fun handleTogglePasswordVisibility() {
        _state.value = _state.value.copy(
            isPasswordVisible = !_state.value.isPasswordVisible,
        )
    }

    private fun handleRememberMeChanged(remember: Boolean) {
        _state.value = _state.value.copy(rememberMe = remember)
    }

    // ============================================
    // Authentication Handlers
    // ============================================

    private fun handleSignInWithEmail() {
        val currentState = _state.value

        if (!validateEmail(currentState.email)) {
            _state.value = currentState.copy(
                isEmailValid = false,
                emailError = "Please enter a valid email",
            )
            return
        }

        if (!validatePassword(currentState.password)) {
            _state.value = currentState.copy(
                isPasswordValid = false,
                passwordError = "Password must be at least 6 characters",
            )
            return
        }

        _state.value = currentState.copy(
            isLoading = true,
            loginResult = LoginResult.Loading,
        )

        scope.launch {
            authRepository.signInWithEmail(currentState.email, currentState.password)
                .onSuccess { result ->
                    if (!result.isEmailVerified) {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            loginResult = LoginResult.EmailNotVerified,
                        )
                        _events.send(LoginEvent.ShowEmailVerificationRequired)
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            loginResult = LoginResult.Success(result.userId),
                        )
                        _events.send(LoginEvent.NavigateToHome)
                    }
                }
                .onFailure { exception ->
                    val errorMessage = mapErrorToUserMessage(exception)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        loginResult = LoginResult.Error(errorMessage),
                    )
                    _events.send(LoginEvent.ShowError(errorMessage))
                }
        }
    }

    private fun handleSignInWithGoogle() {
        _state.value = _state.value.copy(
            isLoading = true,
            loginResult = LoginResult.Loading,
        )

        scope.launch {
            // Google Sign-In requires platform-specific ID token acquisition
            // For now, show not implemented error
            val errorMessage = "Google Sign-In coming soon"
            _state.value = _state.value.copy(
                isLoading = false,
                loginResult = LoginResult.Error(errorMessage),
            )
            _events.send(LoginEvent.ShowError(errorMessage))
        }
    }

    private fun handleSignInWithApple() {
        _state.value = _state.value.copy(
            isLoading = true,
            loginResult = LoginResult.Loading,
        )

        scope.launch {
            // Apple Sign-In requires platform-specific implementation
            // For now, show not implemented error
            val errorMessage = "Apple Sign-In coming soon"
            _state.value = _state.value.copy(
                isLoading = false,
                loginResult = LoginResult.Error(errorMessage),
            )
            _events.send(LoginEvent.ShowError(errorMessage))
        }
    }

    // ============================================
    // Navigation Handlers
    // ============================================

    private fun handleNavigateToSignUp() {
        scope.launch {
            _events.send(LoginEvent.NavigateToSignUp)
        }
    }

    private fun handleNavigateToForgotPassword() {
        scope.launch {
            _events.send(LoginEvent.NavigateToForgotPassword)
        }
    }

    // ============================================
    // State Management Handlers
    // ============================================

    private fun handleClearError() {
        _state.value = _state.value.copy(
            loginResult = LoginResult.Idle,
            emailError = null,
            passwordError = null,
        )
    }

    private fun handleResetState() {
        _state.value = LoginState()
    }

    // ============================================
    // Validation Helpers
    // ============================================

    private fun validateEmail(email: String): Boolean {
        if (email.isBlank()) return false
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }

    private fun validatePassword(password: String): Boolean {
        return password.length >= 6
    }

    // ============================================
    // Error Mapping
    // ============================================

    private fun mapErrorToUserMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("invalid", ignoreCase = true) == true ->
                "Invalid email or password"
            exception.message?.contains("network", ignoreCase = true) == true ->
                "Network error. Please check your connection"
            exception.message?.contains("disabled", ignoreCase = true) == true ->
                "This account has been disabled"
            exception.message?.contains("not found", ignoreCase = true) == true ->
                "Account not found. Please sign up"
            else -> exception.message ?: "An unexpected error occurred"
        }
    }
}
