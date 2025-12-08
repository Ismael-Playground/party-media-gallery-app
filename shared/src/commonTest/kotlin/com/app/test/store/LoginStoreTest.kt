package com.app.test.store

import app.cash.turbine.test
import com.app.test.fixtures.TestFixtures
import com.app.test.repository.FakeAuthRepository
import com.partygallery.presentation.intent.LoginIntent
import com.partygallery.presentation.state.LoginResult
import com.partygallery.presentation.store.LoginStore
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * LoginStore Unit Tests using Turbine for Flow testing.
 *
 * S2.6-EX-001: LoginStoreTest example for TDD
 *
 * This test demonstrates the TDD approach for testing MVI stores:
 * - RED: Write test that fails
 * - GREEN: Implement minimal code to pass
 * - REFACTOR: Clean up while keeping tests green
 *
 * Tests follow the pattern:
 * - Given: Setup initial conditions (fakes, states)
 * - When: Trigger intent/action
 * - Then: Assert expected state changes
 */
class LoginStoreTest {

    private lateinit var store: LoginStore
    private lateinit var fakeAuthRepository: FakeAuthRepository

    @BeforeTest
    fun setup() {
        fakeAuthRepository = FakeAuthRepository()
        store = LoginStore(fakeAuthRepository)
    }

    // ============================================
    // Email Validation Tests
    // ============================================

    @Test
    fun emailChangedWithValidEmailUpdatesStateCorrectly() = runTest {
        store.state.test {
            // Initial state
            val initial = awaitItem()
            assertTrue(initial.email.isEmpty())

            // When: change email to valid email
            store.processIntent(LoginIntent.EmailChanged(TestFixtures.Emails.VALID))

            // Then: state updates with valid email
            val updated = awaitItem()
            assertEquals(TestFixtures.Emails.VALID, updated.email)
            assertTrue(updated.isEmailValid)
            assertNull(updated.emailError)
        }
    }

    @Test
    fun emailChangedWithInvalidEmailShowsError() = runTest {
        store.state.test {
            awaitItem() // Initial state

            // When: change email to invalid email
            store.processIntent(LoginIntent.EmailChanged(TestFixtures.Emails.INVALID))

            // Then: state shows error
            val updated = awaitItem()
            assertEquals(TestFixtures.Emails.INVALID, updated.email)
            assertFalse(updated.isEmailValid)
            assertEquals("Invalid email format", updated.emailError)
        }
    }

    @Test
    fun emailChangedWithEmptyEmailHasNoError() = runTest {
        store.state.test {
            val initial = awaitItem() // Initial state

            // Initial state already has empty email, so no state change expected
            // Just verify initial state has no error for empty email
            assertEquals("", initial.email)
            assertTrue(initial.isEmailValid)
            assertNull(initial.emailError)

            // If we first set a valid email, then clear it, we should get a new state
            store.processIntent(LoginIntent.EmailChanged(TestFixtures.Emails.VALID))
            val withEmail = awaitItem()
            assertEquals(TestFixtures.Emails.VALID, withEmail.email)

            // When: change email to empty (user clearing field)
            store.processIntent(LoginIntent.EmailChanged(TestFixtures.Emails.EMPTY))

            // Then: no error shown (allow user to clear field)
            val cleared = awaitItem()
            assertEquals("", cleared.email)
            assertTrue(cleared.isEmailValid)
            assertNull(cleared.emailError)
        }
    }

    // ============================================
    // Password Validation Tests
    // ============================================

    @Test
    fun passwordChangedWithValidPasswordUpdatesStateCorrectly() = runTest {
        store.state.test {
            awaitItem() // Initial state

            // When: change password to valid password
            store.processIntent(LoginIntent.PasswordChanged(TestFixtures.Passwords.VALID))

            // Then: state updates with valid password
            val updated = awaitItem()
            assertEquals(TestFixtures.Passwords.VALID, updated.password)
            assertTrue(updated.isPasswordValid)
            assertNull(updated.passwordError)
        }
    }

    @Test
    fun passwordChangedWithShortPasswordShowsError() = runTest {
        store.state.test {
            awaitItem() // Initial state

            // When: change password to too short
            store.processIntent(LoginIntent.PasswordChanged(TestFixtures.Passwords.SHORT))

            // Then: state shows error
            val updated = awaitItem()
            assertEquals(TestFixtures.Passwords.SHORT, updated.password)
            assertFalse(updated.isPasswordValid)
            assertEquals("Password must be at least 6 characters", updated.passwordError)
        }
    }

    // ============================================
    // Sign In Tests
    // ============================================

    @Test
    fun signInWithValidCredentialsNavigatesToHome() = runTest {
        // Given: valid credentials and successful auth
        fakeAuthRepository.setSignInSuccess()

        store.state.test {
            awaitItem() // Initial state

            // Setup valid email and password first
            store.processIntent(LoginIntent.EmailChanged(TestFixtures.Emails.VALID))
            awaitItem()
            store.processIntent(LoginIntent.PasswordChanged(TestFixtures.Passwords.VALID))
            awaitItem()

            // When: sign in
            store.processIntent(LoginIntent.SignInWithEmail)

            // Then: loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertIs<LoginResult.Loading>(loadingState.loginResult)

            // Then: success state
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertIs<LoginResult.Success>(successState.loginResult)
        }
    }

    @Test
    fun signInWithInvalidCredentialsShowsError() = runTest {
        // Given: auth will fail
        fakeAuthRepository.setSignInFailure(TestFixtures.Errors.INVALID_CREDENTIALS)

        store.state.test {
            awaitItem() // Initial state

            // Setup valid email and password
            store.processIntent(LoginIntent.EmailChanged(TestFixtures.Emails.VALID))
            awaitItem()
            store.processIntent(LoginIntent.PasswordChanged(TestFixtures.Passwords.VALID))
            awaitItem()

            // When: sign in
            store.processIntent(LoginIntent.SignInWithEmail)

            // Then: loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            // Then: error state
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertIs<LoginResult.Error>(errorState.loginResult)
        }
    }

    @Test
    fun signInWithUnverifiedEmailShowsVerificationRequired() = runTest {
        // Given: user exists but email not verified
        fakeAuthRepository.setSignInSuccess(isEmailVerified = false)

        store.state.test {
            awaitItem() // Initial state

            // Setup valid credentials
            store.processIntent(LoginIntent.EmailChanged(TestFixtures.Emails.VALID))
            awaitItem()
            store.processIntent(LoginIntent.PasswordChanged(TestFixtures.Passwords.VALID))
            awaitItem()

            // When: sign in
            store.processIntent(LoginIntent.SignInWithEmail)

            // Then: loading
            awaitItem()

            // Then: email not verified state
            val resultState = awaitItem()
            assertFalse(resultState.isLoading)
            assertIs<LoginResult.EmailNotVerified>(resultState.loginResult)
        }
    }

    @Test
    fun signInWithEmptyEmailShowsValidationErrorWithoutCallingAuth() = runTest {
        store.state.test {
            awaitItem() // Initial state

            // When: try to sign in with empty credentials
            store.processIntent(LoginIntent.SignInWithEmail)

            // Then: validation error shown, no loading state
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertFalse(errorState.isEmailValid)
            assertEquals("Please enter a valid email", errorState.emailError)
        }
    }

    // ============================================
    // UI Action Tests
    // ============================================

    @Test
    fun togglePasswordVisibilityTogglesState() = runTest {
        store.state.test {
            val initial = awaitItem()
            assertFalse(initial.isPasswordVisible)

            // When: toggle
            store.processIntent(LoginIntent.TogglePasswordVisibility)

            // Then: visible
            val visible = awaitItem()
            assertTrue(visible.isPasswordVisible)

            // When: toggle again
            store.processIntent(LoginIntent.TogglePasswordVisibility)

            // Then: hidden
            val hidden = awaitItem()
            assertFalse(hidden.isPasswordVisible)
        }
    }

    @Test
    fun rememberMeChangedUpdatesState() = runTest {
        store.state.test {
            val initial = awaitItem()
            assertFalse(initial.rememberMe)

            // When: enable remember me
            store.processIntent(LoginIntent.RememberMeChanged(true))

            // Then: enabled
            val enabled = awaitItem()
            assertTrue(enabled.rememberMe)
        }
    }

    @Test
    fun clearErrorResetsErrorState() = runTest {
        // Given: there's an error
        fakeAuthRepository.setSignInFailure(TestFixtures.Errors.INVALID_CREDENTIALS)

        store.state.test {
            awaitItem() // Initial

            // Create an error state
            store.processIntent(LoginIntent.EmailChanged(TestFixtures.Emails.VALID))
            awaitItem()
            store.processIntent(LoginIntent.PasswordChanged(TestFixtures.Passwords.VALID))
            awaitItem()
            store.processIntent(LoginIntent.SignInWithEmail)
            awaitItem() // Loading
            val errorState = awaitItem()
            assertIs<LoginResult.Error>(errorState.loginResult)

            // When: clear error
            store.processIntent(LoginIntent.ClearError)

            // Then: error cleared
            val clearedState = awaitItem()
            assertIs<LoginResult.Idle>(clearedState.loginResult)
            assertNull(clearedState.emailError)
            assertNull(clearedState.passwordError)
        }
    }

    @Test
    fun resetStateReturnsToInitialState() = runTest {
        store.state.test {
            awaitItem() // Initial

            // Change some state
            store.processIntent(LoginIntent.EmailChanged(TestFixtures.Emails.VALID))
            awaitItem()
            store.processIntent(LoginIntent.PasswordChanged(TestFixtures.Passwords.VALID))
            awaitItem()
            store.processIntent(LoginIntent.RememberMeChanged(true))
            awaitItem()

            // When: reset
            store.processIntent(LoginIntent.ResetState)

            // Then: back to initial
            val resetState = awaitItem()
            assertEquals("", resetState.email)
            assertEquals("", resetState.password)
            assertFalse(resetState.rememberMe)
            assertIs<LoginResult.Idle>(resetState.loginResult)
        }
    }
}
