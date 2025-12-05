package com.partygallery.data.auth

import com.partygallery.domain.repository.AuthResult
import com.partygallery.domain.repository.AuthState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Desktop Firebase Authentication Service Implementation
 *
 * S2-001: FirebaseAuthService actual for Desktop (JVM)
 *
 * Desktop uses REST API calls to Firebase or connects to backend server.
 * Currently provides mock implementation for development.
 */
actual class FirebaseAuthService actual constructor() {

    private var mockUserId: String? = null
    private var mockEmail: String? = null
    private var mockEmailVerified: Boolean = false

    actual fun observeAuthState(): Flow<AuthState> = callbackFlow {
        trySend(if (mockUserId != null) AuthState.Authenticated(mockUserId!!) else AuthState.Unauthenticated)
        awaitClose { }
    }

    actual suspend fun getCurrentUserId(): String? = mockUserId

    actual suspend fun isAuthenticated(): Boolean = mockUserId != null

    actual suspend fun signInWithEmail(email: String, password: String): Result<AuthResult> {
        return try {
            // TODO: Implement REST API call to Firebase or backend
            mockUserId = "mock_user_${email.hashCode()}"
            mockEmail = email
            mockEmailVerified = true

            Result.success(
                AuthResult(
                    userId = mockUserId!!,
                    email = email,
                    isNewUser = false,
                    isEmailVerified = mockEmailVerified,
                    idToken = "mock_token_${System.currentTimeMillis()}",
                ),
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun signInWithGoogle(idToken: String): Result<AuthResult> {
        return Result.failure(NotImplementedError("Google Sign-In not available on Desktop"))
    }

    actual suspend fun signInWithApple(idToken: String, nonce: String): Result<AuthResult> {
        return Result.failure(NotImplementedError("Apple Sign-In not available on Desktop"))
    }

    actual suspend fun signUpWithEmail(email: String, password: String): Result<AuthResult> {
        return try {
            mockUserId = "mock_user_${email.hashCode()}"
            mockEmail = email
            mockEmailVerified = false

            Result.success(
                AuthResult(
                    userId = mockUserId!!,
                    email = email,
                    isNewUser = true,
                    isEmailVerified = mockEmailVerified,
                    idToken = "mock_token_${System.currentTimeMillis()}",
                ),
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun sendPasswordResetEmail(email: String): Result<Unit> = Result.success(Unit)

    actual suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit> = Result.success(Unit)

    actual suspend fun getIdToken(forceRefresh: Boolean): Result<String> {
        return if (mockUserId != null) {
            Result.success("mock_token_${System.currentTimeMillis()}")
        } else {
            Result.failure(IllegalStateException("User not authenticated"))
        }
    }

    actual suspend fun refreshToken(): Result<String> = getIdToken(true)

    actual suspend fun signOut(): Result<Unit> {
        mockUserId = null
        mockEmail = null
        mockEmailVerified = false
        return Result.success(Unit)
    }

    actual suspend fun deleteAccount(): Result<Unit> {
        mockUserId = null
        mockEmail = null
        mockEmailVerified = false
        return Result.success(Unit)
    }

    actual suspend fun sendEmailVerification(): Result<Unit> = Result.success(Unit)

    actual suspend fun isEmailVerified(): Boolean = mockEmailVerified

    actual suspend fun reloadUser(): Result<Unit> = Result.success(Unit)
}
