package com.partygallery.data.repository

import com.partygallery.data.auth.FirebaseAuthService
import com.partygallery.domain.repository.AuthRepository
import com.partygallery.domain.repository.AuthResult
import com.partygallery.domain.repository.AuthState
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of AuthRepository using FirebaseAuthService.
 *
 * S2-002: AuthRepository implementation
 *
 * This repository acts as a clean architecture adapter between the domain layer
 * and the platform-specific Firebase authentication service.
 *
 * @param firebaseAuthService Platform-specific Firebase authentication service
 */
class AuthRepositoryImpl(
    private val firebaseAuthService: FirebaseAuthService,
) : AuthRepository {

    // ============================================
    // Authentication State
    // ============================================

    override suspend fun isAuthenticated(): Boolean = firebaseAuthService.isAuthenticated()

    override fun observeAuthState(): Flow<AuthState> = firebaseAuthService.observeAuthState()

    override suspend fun getCurrentUserId(): String? = firebaseAuthService.getCurrentUserId()

    // ============================================
    // Sign In Methods
    // ============================================

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthResult> =
        firebaseAuthService.signInWithEmail(email, password)

    override suspend fun signInWithGoogle(idToken: String): Result<AuthResult> =
        firebaseAuthService.signInWithGoogle(idToken)

    override suspend fun signInWithApple(idToken: String, nonce: String): Result<AuthResult> =
        firebaseAuthService.signInWithApple(idToken, nonce)

    // ============================================
    // Sign Up
    // ============================================

    override suspend fun signUpWithEmail(email: String, password: String): Result<AuthResult> =
        firebaseAuthService.signUpWithEmail(email, password)

    // ============================================
    // Password Management
    // ============================================

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        firebaseAuthService.sendPasswordResetEmail(email)

    override suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit> =
        firebaseAuthService.updatePassword(currentPassword, newPassword)

    // ============================================
    // Token Management
    // ============================================

    override suspend fun getIdToken(forceRefresh: Boolean): Result<String> =
        firebaseAuthService.getIdToken(forceRefresh)

    override suspend fun refreshToken(): Result<String> = firebaseAuthService.refreshToken()

    // ============================================
    // Sign Out
    // ============================================

    override suspend fun signOut(): Result<Unit> = firebaseAuthService.signOut()

    override suspend fun deleteAccount(): Result<Unit> = firebaseAuthService.deleteAccount()

    // ============================================
    // Verification
    // ============================================

    override suspend fun sendEmailVerification(): Result<Unit> = firebaseAuthService.sendEmailVerification()

    override suspend fun isEmailVerified(): Boolean = firebaseAuthService.isEmailVerified()

    override suspend fun reloadUser(): Result<Unit> = firebaseAuthService.reloadUser()
}
