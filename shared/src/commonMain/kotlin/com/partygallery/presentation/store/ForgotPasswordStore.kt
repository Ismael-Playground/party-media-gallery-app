package com.partygallery.presentation.store

import com.partygallery.domain.repository.AuthRepository
import com.partygallery.presentation.intent.ForgotPasswordIntent
import com.partygallery.presentation.state.ForgotPasswordEvent
import com.partygallery.presentation.state.ForgotPasswordResult
import com.partygallery.presentation.state.ForgotPasswordState
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
 * Forgot Password Store - MVI State Management
 *
 * S2-007: ForgotPasswordStore with MVI pattern
 *
 * Manages forgot password screen state following unidirectional data flow:
 * Intent -> Store -> State -> UI -> Intent
 *
 * @param authRepository Repository for authentication operations
 */
class ForgotPasswordStore(
    private val authRepository: AuthRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()

    private val _events = Channel<ForgotPasswordEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    /**
     * Process user intents and update state accordingly.
     */
    fun processIntent(intent: ForgotPasswordIntent) {
        when (intent) {
            is ForgotPasswordIntent.EmailChanged -> handleEmailChanged(intent.email)
            is ForgotPasswordIntent.SendResetEmail -> handleSendResetEmail()
            is ForgotPasswordIntent.NavigateBack -> handleNavigateBack()
            is ForgotPasswordIntent.NavigateToLogin -> handleNavigateToLogin()
            is ForgotPasswordIntent.ClearError -> handleClearError()
            is ForgotPasswordIntent.ResetState -> handleResetState()
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

    // ============================================
    // Action Handlers
    // ============================================

    private fun handleSendResetEmail() {
        val currentState = _state.value

        if (!validateEmail(currentState.email)) {
            _state.value = currentState.copy(
                isEmailValid = false,
                emailError = "Please enter a valid email",
            )
            return
        }

        _state.value = currentState.copy(
            isLoading = true,
            result = ForgotPasswordResult.Loading,
        )

        scope.launch {
            authRepository.sendPasswordResetEmail(currentState.email)
                .onSuccess {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        result = ForgotPasswordResult.Success,
                    )
                    _events.send(
                        ForgotPasswordEvent.ShowSuccess(
                            "Password reset email sent! Check your inbox.",
                        ),
                    )
                }
                .onFailure { exception ->
                    val errorMessage = mapErrorToUserMessage(exception)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        result = ForgotPasswordResult.Error(errorMessage),
                    )
                    _events.send(ForgotPasswordEvent.ShowError(errorMessage))
                }
        }
    }

    // ============================================
    // Navigation Handlers
    // ============================================

    private fun handleNavigateBack() {
        scope.launch {
            _events.send(ForgotPasswordEvent.NavigateBack)
        }
    }

    private fun handleNavigateToLogin() {
        scope.launch {
            _events.send(ForgotPasswordEvent.NavigateToLogin)
        }
    }

    // ============================================
    // State Management Handlers
    // ============================================

    private fun handleClearError() {
        _state.value = _state.value.copy(
            result = ForgotPasswordResult.Idle,
            emailError = null,
        )
    }

    private fun handleResetState() {
        _state.value = ForgotPasswordState()
    }

    // ============================================
    // Validation Helpers
    // ============================================

    private fun validateEmail(email: String): Boolean {
        if (email.isBlank()) return false
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }

    // ============================================
    // Error Mapping
    // ============================================

    private fun mapErrorToUserMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("not found", ignoreCase = true) == true ->
                "No account found with this email"
            exception.message?.contains("network", ignoreCase = true) == true ->
                "Network error. Please check your connection"
            exception.message?.contains("invalid", ignoreCase = true) == true ->
                "Invalid email address"
            exception.message?.contains("too many", ignoreCase = true) == true ->
                "Too many requests. Please try again later"
            else -> exception.message ?: "An unexpected error occurred"
        }
    }
}
