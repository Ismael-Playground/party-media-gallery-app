package com.partygallery.presentation.state

import kotlinx.datetime.LocalDate

/**
 * SignUp Flow State
 *
 * S2-010: SignUpState for MVI pattern - 6-step signup flow
 *
 * Represents the complete UI state of the signup flow.
 * Follows Dark Mode First design principles.
 *
 * Steps:
 * 1. BasicInfo - name, birthdate, username
 * 2. AvatarSetup - profile photo
 * 3. ContactSync - sync contacts to find friends
 * 4. InterestTags - select party interests
 * 5. SocialLinking - connect social media accounts
 * 6. Completion - success screen
 */
data class SignUpState(
    // Current step (1-6)
    val currentStep: SignUpStep = SignUpStep.BASIC_INFO,
    val completedSteps: Set<SignUpStep> = emptySet(),

    // Step 1: Basic Info
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val birthDate: LocalDate? = null,

    // Validation states
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isPasswordMatch: Boolean = true,
    val isUsernameValid: Boolean = true,
    val isUsernameAvailable: Boolean = true,
    val isBirthDateValid: Boolean = true,

    // Error messages
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val usernameError: String? = null,
    val birthDateError: String? = null,

    // Step 2: Avatar Setup
    val avatarUri: String? = null,
    val avatarBytes: ByteArray? = null,
    val isUploadingAvatar: Boolean = false,

    // Step 3: Contact Sync
    val hasContactsPermission: Boolean = false,
    val isSyncingContacts: Boolean = false,
    val contactMatches: List<ContactMatch> = emptyList(),
    val selectedContacts: Set<String> = emptySet(),

    // Step 4: Interest Tags
    val availableTags: List<PartyTag> = emptyList(),
    val selectedTags: Set<String> = emptySet(),
    val isLoadingTags: Boolean = false,

    // Step 5: Social Linking
    val socialLinks: SocialLinks = SocialLinks(),
    val connectingSocial: SocialPlatform? = null,

    // Loading & Results
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val signUpResult: SignUpResult = SignUpResult.Idle,

    // Username check debounce
    val isCheckingUsername: Boolean = false,
)

/**
 * Signup flow steps
 */
enum class SignUpStep(val stepNumber: Int, val title: String) {
    BASIC_INFO(1, "Basic Info"),
    AVATAR_SETUP(2, "Profile Photo"),
    CONTACT_SYNC(3, "Find Friends"),
    INTEREST_TAGS(4, "Your Interests"),
    SOCIAL_LINKING(5, "Connect Socials"),
    COMPLETION(6, "All Set!");

    companion object {
        fun fromNumber(number: Int): SignUpStep =
            entries.find { it.stepNumber == number } ?: BASIC_INFO
    }
}

/**
 * Party interest tag
 */
data class PartyTag(
    val id: String,
    val name: String,
    val emoji: String,
    val category: TagCategory,
)

enum class TagCategory {
    MUSIC_GENRE,
    PARTY_TYPE,
    VIBE,
    ACTIVITY,
}

/**
 * Social media links
 */
data class SocialLinks(
    val instagram: String? = null,
    val tiktok: String? = null,
    val twitter: String? = null,
    val facebook: String? = null,
    val pinterest: String? = null,
) {
    val linkedCount: Int
        get() = listOfNotNull(instagram, tiktok, twitter, facebook, pinterest).size
}

/**
 * Result of signup operation
 */
sealed class SignUpResult {
    data object Idle : SignUpResult()
    data object Loading : SignUpResult()
    data class Success(val userId: String) : SignUpResult()
    data class Error(val message: String) : SignUpResult()
    data object EmailAlreadyExists : SignUpResult()
    data object UsernameAlreadyTaken : SignUpResult()
    data object WeakPassword : SignUpResult()
}

/**
 * One-time events triggered by signup operations
 */
sealed class SignUpEvent {
    data object NavigateToHome : SignUpEvent()
    data object NavigateToLogin : SignUpEvent()
    data class NavigateToStep(val step: SignUpStep) : SignUpEvent()
    data class ShowError(val message: String) : SignUpEvent()
    data object RequestContactsPermission : SignUpEvent()
    data object RequestCameraPermission : SignUpEvent()
    data object OpenImagePicker : SignUpEvent()
    data class OpenSocialAuth(val platform: SocialPlatform) : SignUpEvent()
}

enum class SocialPlatform {
    INSTAGRAM,
    TIKTOK,
    TWITTER,
    FACEBOOK,
    PINTEREST,
}

/**
 * Matched contact from user's address book who is on the app
 */
data class ContactMatch(
    val userId: String,
    val displayName: String,
    val username: String,
    val avatarUrl: String? = null,
)
