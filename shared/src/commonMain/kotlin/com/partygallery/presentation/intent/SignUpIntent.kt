package com.partygallery.presentation.intent

import com.partygallery.presentation.state.SignUpStep
import com.partygallery.presentation.state.SocialPlatform
import kotlinx.datetime.LocalDate

/**
 * SignUp Intent
 *
 * S2-010: SignUpIntent sealed class for MVI pattern
 *
 * User intents for the 6-step signup flow.
 * Each intent represents a user action that triggers state changes.
 */
sealed class SignUpIntent {

    // ============================================
    // Navigation Intents
    // ============================================

    /** Navigate to next step */
    data object NextStep : SignUpIntent()

    /** Navigate to previous step */
    data object PreviousStep : SignUpIntent()

    /** Navigate to specific step */
    data class GoToStep(val step: SignUpStep) : SignUpIntent()

    /** Skip current optional step */
    data object SkipStep : SignUpIntent()

    /** Navigate back to login */
    data object NavigateToLogin : SignUpIntent()

    // ============================================
    // Step 1: Basic Info Intents
    // ============================================

    /** Email input changed */
    data class EmailChanged(val email: String) : SignUpIntent()

    /** Password input changed */
    data class PasswordChanged(val password: String) : SignUpIntent()

    /** Confirm password input changed */
    data class ConfirmPasswordChanged(val confirmPassword: String) : SignUpIntent()

    /** First name input changed */
    data class FirstNameChanged(val firstName: String) : SignUpIntent()

    /** Last name input changed */
    data class LastNameChanged(val lastName: String) : SignUpIntent()

    /** Username input changed - triggers availability check */
    data class UsernameChanged(val username: String) : SignUpIntent()

    /** Birth date selected */
    data class BirthDateChanged(val birthDate: LocalDate) : SignUpIntent()

    /** Toggle password visibility */
    data object TogglePasswordVisibility : SignUpIntent()

    /** Toggle confirm password visibility */
    data object ToggleConfirmPasswordVisibility : SignUpIntent()

    /** Submit basic info and create account */
    data object SubmitBasicInfo : SignUpIntent()

    // ============================================
    // Step 2: Avatar Setup Intents
    // ============================================

    /** Request to open camera */
    data object OpenCamera : SignUpIntent()

    /** Request to open image picker/gallery */
    data object OpenGallery : SignUpIntent()

    /** Avatar image selected (from camera or gallery) */
    data class AvatarSelected(val uri: String, val bytes: ByteArray? = null) : SignUpIntent()

    /** Remove selected avatar */
    data object RemoveAvatar : SignUpIntent()

    /** Upload avatar to server */
    data object UploadAvatar : SignUpIntent()

    // ============================================
    // Step 3: Contact Sync Intents
    // ============================================

    /** Request contacts permission */
    data object RequestContactsPermission : SignUpIntent()

    /** Contacts permission result */
    data class ContactsPermissionResult(val granted: Boolean) : SignUpIntent()

    /** Start syncing contacts */
    data object SyncContacts : SignUpIntent()

    /** Toggle contact selection */
    data class ToggleContactSelection(val contactId: String) : SignUpIntent()

    /** Select all contacts */
    data object SelectAllContacts : SignUpIntent()

    /** Submit selected contacts (follow them) */
    data object SubmitContacts : SignUpIntent()

    /** Skip contact sync */
    data object SkipContactSync : SignUpIntent()

    // ============================================
    // Step 4: Interest Tags Intents
    // ============================================

    /** Load available tags */
    data object LoadTags : SignUpIntent()

    /** Toggle tag selection */
    data class ToggleTag(val tagId: String) : SignUpIntent()

    /** Select multiple tags at once */
    data class SelectTags(val tagIds: Set<String>) : SignUpIntent()

    /** Clear all selected tags */
    data object ClearTags : SignUpIntent()

    /** Submit selected tags */
    data object SubmitTags : SignUpIntent()

    // ============================================
    // Step 5: Social Linking Intents
    // ============================================

    /** Connect social media account */
    data class ConnectSocial(val platform: SocialPlatform) : SignUpIntent()

    /** Disconnect social media account */
    data class DisconnectSocial(val platform: SocialPlatform) : SignUpIntent()

    /** Social auth completed successfully */
    data class SocialAuthSuccess(
        val platform: SocialPlatform,
        val handle: String,
    ) : SignUpIntent()

    /** Social auth failed */
    data class SocialAuthFailed(
        val platform: SocialPlatform,
        val error: String,
    ) : SignUpIntent()

    /** Skip social linking */
    data object SkipSocialLinking : SignUpIntent()

    /** Submit social links and continue */
    data object SubmitSocialLinks : SignUpIntent()

    // ============================================
    // Step 6: Completion Intents
    // ============================================

    /** Complete signup and navigate to home */
    data object CompleteSignUp : SignUpIntent()

    // ============================================
    // General Intents
    // ============================================

    /** Clear any error state */
    data object ClearError : SignUpIntent()

    /** Reset entire signup flow */
    data object ResetFlow : SignUpIntent()
}
