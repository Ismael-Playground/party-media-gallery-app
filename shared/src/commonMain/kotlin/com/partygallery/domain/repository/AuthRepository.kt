package com.partygallery.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Authentication operations.
 *
 * S1-011: Interfaces base de repositorios
 */
interface AuthRepository {

    // ============================================
    // Authentication State
    // ============================================

    suspend fun isAuthenticated(): Boolean
    fun observeAuthState(): Flow<AuthState>
    suspend fun getCurrentUserId(): String?

    // ============================================
    // Sign In Methods
    // ============================================

    suspend fun signInWithEmail(email: String, password: String): Result<AuthResult>
    suspend fun signInWithGoogle(idToken: String): Result<AuthResult>
    suspend fun signInWithApple(idToken: String, nonce: String): Result<AuthResult>

    // ============================================
    // Sign Up
    // ============================================

    suspend fun signUpWithEmail(email: String, password: String): Result<AuthResult>

    // ============================================
    // Password Management
    // ============================================

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit>

    // ============================================
    // Token Management
    // ============================================

    suspend fun getIdToken(forceRefresh: Boolean = false): Result<String>
    suspend fun refreshToken(): Result<String>

    // ============================================
    // Sign Out
    // ============================================

    suspend fun signOut(): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>

    // ============================================
    // Verification
    // ============================================

    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun isEmailVerified(): Boolean
    suspend fun reloadUser(): Result<Unit>
}

/**
 * Authentication state.
 */
sealed class AuthState {
    data object Unknown : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val userId: String) : AuthState()
}

/**
 * Result of authentication operation.
 */
data class AuthResult(
    val userId: String,
    val email: String,
    val isNewUser: Boolean,
    val isEmailVerified: Boolean,
    val idToken: String,
)
