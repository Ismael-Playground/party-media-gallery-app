package com.partygallery.data.auth

import com.partygallery.domain.repository.AuthResult
import com.partygallery.domain.repository.AuthState
import kotlinx.coroutines.flow.Flow

/**
 * Firebase Authentication Service - Expect Declaration
 *
 * S2-001: FirebaseAuthService expect/actual
 *
 * This service provides platform-specific Firebase Auth implementation.
 * Each platform (Android, iOS, Desktop, Web) provides its own actual implementation.
 */
expect class FirebaseAuthService() {

    /**
     * Observable stream of authentication state changes.
     */
    fun observeAuthState(): Flow<AuthState>

    /**
     * Get current user ID if authenticated.
     */
    suspend fun getCurrentUserId(): String?

    /**
     * Check if user is currently authenticated.
     */
    suspend fun isAuthenticated(): Boolean

    /**
     * Sign in with email and password.
     */
    suspend fun signInWithEmail(email: String, password: String): Result<AuthResult>

    /**
     * Sign in with Google ID token.
     */
    suspend fun signInWithGoogle(idToken: String): Result<AuthResult>

    /**
     * Sign in with Apple ID token.
     */
    suspend fun signInWithApple(idToken: String, nonce: String): Result<AuthResult>

    /**
     * Create new account with email and password.
     */
    suspend fun signUpWithEmail(email: String, password: String): Result<AuthResult>

    /**
     * Send password reset email.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    /**
     * Update current user's password.
     */
    suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit>

    /**
     * Get current user's ID token.
     */
    suspend fun getIdToken(forceRefresh: Boolean): Result<String>

    /**
     * Refresh authentication token.
     */
    suspend fun refreshToken(): Result<String>

    /**
     * Sign out current user.
     */
    suspend fun signOut(): Result<Unit>

    /**
     * Delete current user's account.
     */
    suspend fun deleteAccount(): Result<Unit>

    /**
     * Send email verification to current user.
     */
    suspend fun sendEmailVerification(): Result<Unit>

    /**
     * Check if current user's email is verified.
     */
    suspend fun isEmailVerified(): Boolean

    /**
     * Reload current user's data from Firebase.
     */
    suspend fun reloadUser(): Result<Unit>
}
