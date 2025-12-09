package com.app.test.store

import app.cash.turbine.test
import com.app.test.repository.FakeAuthRepository
import com.partygallery.presentation.intent.SignUpIntent
import com.partygallery.presentation.state.SignUpResult
import com.partygallery.presentation.state.SignUpStep
import com.partygallery.presentation.state.SocialPlatform
import com.partygallery.presentation.store.SignUpStore
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * SignUpStore Unit Tests using Turbine for Flow testing.
 *
 * Tests the 6-step signup flow:
 * 1. Basic Info - email, password, username, birthdate validation
 * 2. Avatar Setup - image selection
 * 3. Contact Sync - permission and selection
 * 4. Interest Tags - tag selection
 * 5. Social Linking - connect social accounts
 * 6. Completion - final step
 */
class SignUpStoreTest {

    private lateinit var store: SignUpStore
    private lateinit var fakeAuthRepository: FakeAuthRepository

    @BeforeTest
    fun setup() {
        fakeAuthRepository = FakeAuthRepository()
        store = SignUpStore(fakeAuthRepository)
    }

    // ============================================
    // Initial State Tests
    // ============================================

    @Test
    fun initialStateIsCorrect() = runTest {
        store.state.test {
            val state = awaitItem()
            assertEquals(SignUpStep.BASIC_INFO, state.currentStep)
            assertEquals("", state.email)
            assertEquals("", state.password)
            assertEquals("", state.username)
            assertNull(state.birthDate)
            assertTrue(state.completedSteps.isEmpty())
            assertIs<SignUpResult.Idle>(state.signUpResult)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ============================================
    // Step 1: Email Validation Tests
    // ============================================

    @Test
    fun emailChangedWithValidEmailUpdatesState() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.EmailChanged("test@party.gallery"))
            val state = awaitItem()

            assertEquals("test@party.gallery", state.email)
            assertTrue(state.isEmailValid)
            assertNull(state.emailError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun emailChangedWithInvalidEmailShowsError() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.EmailChanged("invalid-email"))
            val state = awaitItem()

            assertEquals("invalid-email", state.email)
            assertFalse(state.isEmailValid)
            assertEquals("Invalid email format", state.emailError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun emailChangedWithEmptyEmailClearsError() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.EmailChanged("invalid"))
            awaitItem() // Invalid state

            store.processIntent(SignUpIntent.EmailChanged(""))
            val state = awaitItem()

            assertEquals("", state.email)
            assertTrue(state.isEmailValid) // Empty is considered "valid" (no error shown)
            assertNull(state.emailError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ============================================
    // Step 1: Password Validation Tests
    // ============================================

    @Test
    fun passwordChangedWithValidPasswordUpdatesState() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.PasswordChanged("Password123"))
            val state = awaitItem()

            assertEquals("Password123", state.password)
            assertTrue(state.isPasswordValid)
            assertNull(state.passwordError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun passwordChangedWithShortPasswordShowsError() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.PasswordChanged("Pass1"))
            val state = awaitItem()

            assertFalse(state.isPasswordValid)
            assertNotNull(state.passwordError)
            assertTrue(state.passwordError!!.contains("8 characters"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun passwordChangedWithoutUppercaseShowsError() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.PasswordChanged("password123"))
            val state = awaitItem()

            assertFalse(state.isPasswordValid)
            assertNotNull(state.passwordError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun passwordChangedWithoutLowercaseShowsError() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.PasswordChanged("PASSWORD123"))
            val state = awaitItem()

            assertFalse(state.isPasswordValid)
            assertNotNull(state.passwordError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun passwordChangedWithoutNumberShowsError() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.PasswordChanged("PasswordABC"))
            val state = awaitItem()

            assertFalse(state.isPasswordValid)
            assertNotNull(state.passwordError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ============================================
    // Step 1: Confirm Password Tests
    // ============================================

    @Test
    fun confirmPasswordMatchingPasswordIsValid() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.PasswordChanged("Password123"))
            awaitItem()

            store.processIntent(SignUpIntent.ConfirmPasswordChanged("Password123"))
            val state = awaitItem()

            assertTrue(state.isPasswordMatch)
            assertNull(state.confirmPasswordError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun confirmPasswordNotMatchingShowsError() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.PasswordChanged("Password123"))
            awaitItem()

            store.processIntent(SignUpIntent.ConfirmPasswordChanged("DifferentPassword123"))
            val state = awaitItem()

            assertFalse(state.isPasswordMatch)
            assertEquals("Passwords don't match", state.confirmPasswordError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ============================================
    // Step 1: Username Validation Tests
    // ============================================

    @Test
    fun usernameChangedWithValidUsernameSanitizesInput() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.UsernameChanged("TestUser"))
            val state = awaitItem()

            // Username should be sanitized to lowercase
            assertEquals("testuser", state.username)
            assertTrue(state.isUsernameValid)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun usernameChangedRemovesSpecialCharacters() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.UsernameChanged("test@user!123"))
            val state = awaitItem()

            assertEquals("testuser123", state.username)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun usernameChangedWithTooShortUsernameShowsError() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.UsernameChanged("ab"))
            val state = awaitItem()

            assertEquals("ab", state.username)
            assertFalse(state.isUsernameValid)
            assertNotNull(state.usernameError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ============================================
    // Step 1: Birth Date Validation Tests
    // ============================================

    @Test
    fun birthDateChangedWith18PlusAgeIsValid() = runTest {
        store.state.test {
            awaitItem() // Initial state

            val adultBirthDate = get18PlusBirthDate()
            store.processIntent(SignUpIntent.BirthDateChanged(adultBirthDate))
            val state = awaitItem()

            assertEquals(adultBirthDate, state.birthDate)
            assertTrue(state.isBirthDateValid)
            assertNull(state.birthDateError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun birthDateChangedWithUnder18ShowsError() = runTest {
        store.state.test {
            awaitItem() // Initial state

            val minorBirthDate = getUnder18BirthDate()
            store.processIntent(SignUpIntent.BirthDateChanged(minorBirthDate))
            val state = awaitItem()

            assertEquals(minorBirthDate, state.birthDate)
            assertFalse(state.isBirthDateValid)
            assertTrue(state.birthDateError!!.contains("18"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ============================================
    // Step 1: Password Visibility Toggle Tests
    // ============================================

    @Test
    fun togglePasswordVisibilityChangesState() = runTest {
        store.state.test {
            val initial = awaitItem()
            assertFalse(initial.isPasswordVisible)

            store.processIntent(SignUpIntent.TogglePasswordVisibility)
            val toggled = awaitItem()
            assertTrue(toggled.isPasswordVisible)

            store.processIntent(SignUpIntent.TogglePasswordVisibility)
            val toggledBack = awaitItem()
            assertFalse(toggledBack.isPasswordVisible)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun toggleConfirmPasswordVisibilityChangesState() = runTest {
        store.state.test {
            val initial = awaitItem()
            assertFalse(initial.isConfirmPasswordVisible)

            store.processIntent(SignUpIntent.ToggleConfirmPasswordVisibility)
            val toggled = awaitItem()
            assertTrue(toggled.isConfirmPasswordVisible)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ============================================
    // Navigation Tests
    // ============================================

    @Test
    fun nextStepAdvancesToNextStep() = runTest {
        store.state.test {
            awaitItem() // Initial state (BASIC_INFO)

            store.processIntent(SignUpIntent.NextStep)
            val state = awaitItem()

            assertEquals(SignUpStep.AVATAR_SETUP, state.currentStep)
            assertTrue(state.completedSteps.contains(SignUpStep.BASIC_INFO))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun previousStepGoesBackToPreviousStep() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.NextStep)
            awaitItem() // AVATAR_SETUP

            store.processIntent(SignUpIntent.PreviousStep)
            val state = awaitItem()

            assertEquals(SignUpStep.BASIC_INFO, state.currentStep)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun previousStepFromFirstStepDoesNothing() = runTest {
        store.state.test {
            val initial = awaitItem()
            assertEquals(SignUpStep.BASIC_INFO, initial.currentStep)

            store.processIntent(SignUpIntent.PreviousStep)
            // No state change expected - we're already at the first step
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun goToStepNavigatesToSpecificStep() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.GoToStep(SignUpStep.INTEREST_TAGS))
            val state = awaitItem()

            assertEquals(SignUpStep.INTEREST_TAGS, state.currentStep)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun skipStepBehavesLikeNextStep() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.SkipStep)
            val state = awaitItem()

            assertEquals(SignUpStep.AVATAR_SETUP, state.currentStep)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ============================================
    // Step 2: Avatar Tests
    // ============================================

    @Test
    fun avatarSelectedUpdatesState() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.AvatarSelected("file://avatar.jpg", null))
            val state = awaitItem()

            assertEquals("file://avatar.jpg", state.avatarUri)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun removeAvatarClearsAvatar() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.AvatarSelected("file://avatar.jpg", null))
            awaitItem()

            store.processIntent(SignUpIntent.RemoveAvatar)
            val state = awaitItem()

            assertNull(state.avatarUri)
            assertNull(state.avatarBytes)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ============================================
    // Step 3: Contact Sync Tests
    // ============================================

    @Test
    fun contactsPermissionResultGrantedUpdatesState() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.ContactsPermissionResult(true))
            // First update: hasContactsPermission = true
            val permissionState = awaitItem()
            assertTrue(permissionState.hasContactsPermission)

            // Second update: isSyncingContacts = true (triggered by handleSyncContacts)
            val syncingState = awaitItem()
            assertTrue(syncingState.isSyncingContacts)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun contactsPermissionResultDeniedDoesNotTriggerSync() = runTest {
        store.state.test {
            val initial = awaitItem()
            assertFalse(initial.hasContactsPermission)

            // Permission denied - state updates but no sync triggered
            store.processIntent(SignUpIntent.ContactsPermissionResult(false))

            // hasContactsPermission was already false, so no state change emitted
            // Verify the current state is correct
            val currentState = store.state.value
            assertFalse(currentState.hasContactsPermission)
            assertFalse(currentState.isSyncingContacts)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun toggleContactSelectionTogglesSelection() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.ToggleContactSelection("contact1"))
            val selected = awaitItem()
            assertTrue(selected.selectedContacts.contains("contact1"))

            store.processIntent(SignUpIntent.ToggleContactSelection("contact1"))
            val deselected = awaitItem()
            assertFalse(deselected.selectedContacts.contains("contact1"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ============================================
    // Step 4: Interest Tags Tests
    // ============================================

    @Test
    fun toggleTagAddsAndRemovesTag() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.ToggleTag("tag1"))
            val added = awaitItem()
            assertTrue(added.selectedTags.contains("tag1"))

            store.processIntent(SignUpIntent.ToggleTag("tag1"))
            val removed = awaitItem()
            assertFalse(removed.selectedTags.contains("tag1"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun selectTagsSetsMultipleTags() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.SelectTags(setOf("tag1", "tag2", "tag3")))
            val state = awaitItem()

            assertEquals(3, state.selectedTags.size)
            assertTrue(state.selectedTags.containsAll(listOf("tag1", "tag2", "tag3")))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun clearTagsRemovesAllTags() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.SelectTags(setOf("tag1", "tag2")))
            awaitItem()

            store.processIntent(SignUpIntent.ClearTags)
            val state = awaitItem()

            assertTrue(state.selectedTags.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ============================================
    // Step 5: Social Linking Tests
    // ============================================

    @Test
    fun socialAuthSuccessUpdatesSocialLinks() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.SocialAuthSuccess(SocialPlatform.INSTAGRAM, "party_user"))
            val state = awaitItem()

            assertEquals("party_user", state.socialLinks.instagram)
            assertNull(state.connectingSocial)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun disconnectSocialRemovesLink() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.SocialAuthSuccess(SocialPlatform.TWITTER, "party_tweets"))
            awaitItem()

            store.processIntent(SignUpIntent.DisconnectSocial(SocialPlatform.TWITTER))
            val state = awaitItem()

            assertNull(state.socialLinks.twitter)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun socialAuthFailedClearsConnectingState() = runTest {
        store.state.test {
            awaitItem() // Initial state

            // Manually set connecting state by starting connection
            store.processIntent(SignUpIntent.ConnectSocial(SocialPlatform.FACEBOOK))
            val connecting = awaitItem()
            assertEquals(SocialPlatform.FACEBOOK, connecting.connectingSocial)

            store.processIntent(SignUpIntent.SocialAuthFailed(SocialPlatform.FACEBOOK, "Auth failed"))
            val state = awaitItem()

            assertNull(state.connectingSocial)
            assertNull(state.socialLinks.facebook)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ============================================
    // General Tests
    // ============================================

    @Test
    fun clearErrorResetsAllErrors() = runTest {
        store.state.test {
            awaitItem() // Initial state

            // Create some errors
            store.processIntent(SignUpIntent.EmailChanged("invalid"))
            awaitItem()

            store.processIntent(SignUpIntent.ClearError)
            val state = awaitItem()

            assertNull(state.emailError)
            assertNull(state.passwordError)
            assertNull(state.usernameError)
            assertIs<SignUpResult.Idle>(state.signUpResult)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun resetFlowResetsEntireState() = runTest {
        store.state.test {
            awaitItem() // Initial state

            // Make some changes
            store.processIntent(SignUpIntent.EmailChanged("test@party.gallery"))
            awaitItem()
            store.processIntent(SignUpIntent.FirstNameChanged("John"))
            awaitItem()
            store.processIntent(SignUpIntent.NextStep)
            awaitItem()

            store.processIntent(SignUpIntent.ResetFlow)
            val state = awaitItem()

            assertEquals(SignUpStep.BASIC_INFO, state.currentStep)
            assertEquals("", state.email)
            assertEquals("", state.firstName)
            assertTrue(state.completedSteps.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun firstNameChangedUpdatesState() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.FirstNameChanged("  John  "))
            val state = awaitItem()

            assertEquals("John", state.firstName) // Trimmed
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun lastNameChangedUpdatesState() = runTest {
        store.state.test {
            awaitItem() // Initial state

            store.processIntent(SignUpIntent.LastNameChanged("  Doe  "))
            val state = awaitItem()

            assertEquals("Doe", state.lastName) // Trimmed
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ============================================
    // Helper Functions
    // ============================================

    private fun get18PlusBirthDate(): LocalDate {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return LocalDate(now.year - 20, now.monthNumber, now.dayOfMonth)
    }

    private fun getUnder18BirthDate(): LocalDate {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return LocalDate(now.year - 15, now.monthNumber, now.dayOfMonth)
    }
}
