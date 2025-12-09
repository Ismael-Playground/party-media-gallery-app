package com.app.test.repository

import com.partygallery.domain.repository.AuthRepository
import com.partygallery.domain.repository.AuthResult
import com.partygallery.domain.repository.AuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Fake AuthRepository for testing.
 *
 * S2.6-EX-002: FakeAuthRepository example for TDD
 *
 * This fake allows you to control authentication behavior in tests
 * by setting up success/failure scenarios before each test.
 */
class FakeAuthRepository : AuthRepository {

    // Configurable test behavior
    private var signInResult: Result<AuthResult> = Result.success(defaultAuthResult())
    private var signUpResult: Result<AuthResult> = Result.success(defaultAuthResult())
    private var isAuthenticatedValue: Boolean = false
    private var currentUserIdValue: String? = null

    private val authStateFlow = MutableStateFlow<AuthState>(AuthState.Unknown)

    // ============================================
    // Test Setup Methods
    // ============================================

    /**
     * Configure what signInWithEmail will return.
     */
    fun setSignInResult(result: Result<AuthResult>) {
        signInResult = result
    }

    /**
     * Configure sign in to succeed with given user details.
     */
    fun setSignInSuccess(
        userId: String = "test-user-123",
        email: String = "test@party.gallery",
        isEmailVerified: Boolean = true,
    ) {
        signInResult = Result.success(
            AuthResult(
                userId = userId,
                email = email,
                isNewUser = false,
                isEmailVerified = isEmailVerified,
                idToken = "test-token-$userId",
            ),
        )
    }

    /**
     * Configure sign in to fail with given error.
     */
    fun setSignInFailure(error: Throwable) {
        signInResult = Result.failure(error)
    }

    /**
     * Configure what signUpWithEmail will return.
     */
    fun setSignUpResult(result: Result<AuthResult>) {
        signUpResult = result
    }

    /**
     * Configure authenticated state.
     */
    fun setAuthenticated(isAuthenticated: Boolean, userId: String? = null) {
        isAuthenticatedValue = isAuthenticated
        currentUserIdValue = userId
        authStateFlow.value = if (isAuthenticated && userId != null) {
            AuthState.Authenticated(userId)
        } else {
            AuthState.Unauthenticated
        }
    }

    /**
     * Reset all configured behaviors to defaults.
     */
    fun reset() {
        signInResult = Result.success(defaultAuthResult())
        signUpResult = Result.success(defaultAuthResult())
        isAuthenticatedValue = false
        currentUserIdValue = null
        authStateFlow.value = AuthState.Unknown
    }

    // ============================================
    // AuthRepository Implementation
    // ============================================

    override suspend fun isAuthenticated(): Boolean = isAuthenticatedValue

    override fun observeAuthState(): Flow<AuthState> = authStateFlow

    override suspend fun getCurrentUserId(): String? = currentUserIdValue

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthResult> {
        return signInResult
    }

    override suspend fun signInWithGoogle(idToken: String): Result<AuthResult> {
        return signInResult
    }

    override suspend fun signInWithApple(idToken: String, nonce: String): Result<AuthResult> {
        return signInResult
    }

    override suspend fun signUpWithEmail(email: String, password: String): Result<AuthResult> {
        return signUpResult
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getIdToken(forceRefresh: Boolean): Result<String> {
        return Result.success("test-id-token")
    }

    override suspend fun refreshToken(): Result<String> {
        return Result.success("test-refresh-token")
    }

    override suspend fun signOut(): Result<Unit> {
        setAuthenticated(false)
        return Result.success(Unit)
    }

    override suspend fun deleteAccount(): Result<Unit> {
        setAuthenticated(false)
        return Result.success(Unit)
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun isEmailVerified(): Boolean {
        return signInResult.getOrNull()?.isEmailVerified ?: false
    }

    override suspend fun reloadUser(): Result<Unit> {
        return Result.success(Unit)
    }

    // ============================================
    // Helper Methods
    // ============================================

    private fun defaultAuthResult() = AuthResult(
        userId = "test-user-123",
        email = "test@party.gallery",
        isNewUser = false,
        isEmailVerified = true,
        idToken = "test-token",
    )
}
