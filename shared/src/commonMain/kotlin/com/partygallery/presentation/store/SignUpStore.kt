package com.partygallery.presentation.store

import com.partygallery.domain.repository.AuthRepository
import com.partygallery.presentation.intent.SignUpIntent
import com.partygallery.presentation.state.ContactMatch
import com.partygallery.presentation.state.PartyTag
import com.partygallery.presentation.state.SignUpEvent
import com.partygallery.presentation.state.SignUpResult
import com.partygallery.presentation.state.SignUpState
import com.partygallery.presentation.state.SignUpStep
import com.partygallery.presentation.state.SocialLinks
import com.partygallery.presentation.state.SocialPlatform
import com.partygallery.presentation.state.TagCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.yearsUntil

/**
 * SignUp Store - MVI State Management
 *
 * S2-010: SignUpStore with MVI pattern for 6-step signup flow
 *
 * Manages signup flow state following unidirectional data flow:
 * Intent -> Store -> State -> UI -> Intent
 *
 * @param authRepository Repository for authentication operations
 */
class SignUpStore(
    private val authRepository: AuthRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(SignUpState())
    val state: StateFlow<SignUpState> = _state.asStateFlow()

    private val _events = Channel<SignUpEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // Minimum age requirement
    private val minimumAge = 18

    /**
     * Process user intents and update state accordingly.
     */
    fun processIntent(intent: SignUpIntent) {
        when (intent) {
            // Navigation
            is SignUpIntent.NextStep -> handleNextStep()
            is SignUpIntent.PreviousStep -> handlePreviousStep()
            is SignUpIntent.GoToStep -> handleGoToStep(intent.step)
            is SignUpIntent.SkipStep -> handleSkipStep()
            is SignUpIntent.NavigateToLogin -> handleNavigateToLogin()

            // Step 1: Basic Info
            is SignUpIntent.EmailChanged -> handleEmailChanged(intent.email)
            is SignUpIntent.PasswordChanged -> handlePasswordChanged(intent.password)
            is SignUpIntent.ConfirmPasswordChanged -> handleConfirmPasswordChanged(intent.confirmPassword)
            is SignUpIntent.FirstNameChanged -> handleFirstNameChanged(intent.firstName)
            is SignUpIntent.LastNameChanged -> handleLastNameChanged(intent.lastName)
            is SignUpIntent.UsernameChanged -> handleUsernameChanged(intent.username)
            is SignUpIntent.BirthDateChanged -> handleBirthDateChanged(intent.birthDate)
            is SignUpIntent.TogglePasswordVisibility -> handleTogglePasswordVisibility()
            is SignUpIntent.ToggleConfirmPasswordVisibility -> handleToggleConfirmPasswordVisibility()
            is SignUpIntent.SubmitBasicInfo -> handleSubmitBasicInfo()

            // Step 2: Avatar
            is SignUpIntent.OpenCamera -> handleOpenCamera()
            is SignUpIntent.OpenGallery -> handleOpenGallery()
            is SignUpIntent.AvatarSelected -> handleAvatarSelected(intent.uri, intent.bytes)
            is SignUpIntent.RemoveAvatar -> handleRemoveAvatar()
            is SignUpIntent.UploadAvatar -> handleUploadAvatar()

            // Step 3: Contacts
            is SignUpIntent.RequestContactsPermission -> handleRequestContactsPermission()
            is SignUpIntent.ContactsPermissionResult -> handleContactsPermissionResult(intent.granted)
            is SignUpIntent.SyncContacts -> handleSyncContacts()
            is SignUpIntent.ToggleContactSelection -> handleToggleContactSelection(intent.contactId)
            is SignUpIntent.SelectAllContacts -> handleSelectAllContacts()
            is SignUpIntent.SubmitContacts -> handleSubmitContacts()
            is SignUpIntent.SkipContactSync -> handleSkipContactSync()

            // Step 4: Tags
            is SignUpIntent.LoadTags -> handleLoadTags()
            is SignUpIntent.ToggleTag -> handleToggleTag(intent.tagId)
            is SignUpIntent.SelectTags -> handleSelectTags(intent.tagIds)
            is SignUpIntent.ClearTags -> handleClearTags()
            is SignUpIntent.SubmitTags -> handleSubmitTags()

            // Step 5: Social
            is SignUpIntent.ConnectSocial -> handleConnectSocial(intent.platform)
            is SignUpIntent.DisconnectSocial -> handleDisconnectSocial(intent.platform)
            is SignUpIntent.SocialAuthSuccess -> handleSocialAuthSuccess(intent.platform, intent.handle)
            is SignUpIntent.SocialAuthFailed -> handleSocialAuthFailed(intent.platform, intent.error)
            is SignUpIntent.SkipSocialLinking -> handleSkipSocialLinking()
            is SignUpIntent.SubmitSocialLinks -> handleSubmitSocialLinks()

            // Step 6: Completion
            is SignUpIntent.CompleteSignUp -> handleCompleteSignUp()

            // General
            is SignUpIntent.ClearError -> handleClearError()
            is SignUpIntent.ResetFlow -> handleResetFlow()
        }
    }

    // ============================================
    // Navigation Handlers
    // ============================================

    private fun handleNextStep() {
        val currentStep = _state.value.currentStep
        val nextStepNumber = currentStep.stepNumber + 1
        if (nextStepNumber <= SignUpStep.entries.size) {
            val nextStep = SignUpStep.fromNumber(nextStepNumber)
            _state.value = _state.value.copy(
                currentStep = nextStep,
                completedSteps = _state.value.completedSteps + currentStep,
            )
            scope.launch { _events.send(SignUpEvent.NavigateToStep(nextStep)) }
        }
    }

    private fun handlePreviousStep() {
        val currentStep = _state.value.currentStep
        val prevStepNumber = currentStep.stepNumber - 1
        if (prevStepNumber >= 1) {
            val prevStep = SignUpStep.fromNumber(prevStepNumber)
            _state.value = _state.value.copy(currentStep = prevStep)
            scope.launch { _events.send(SignUpEvent.NavigateToStep(prevStep)) }
        }
    }

    private fun handleGoToStep(step: SignUpStep) {
        _state.value = _state.value.copy(currentStep = step)
        scope.launch { _events.send(SignUpEvent.NavigateToStep(step)) }
    }

    private fun handleSkipStep() {
        handleNextStep()
    }

    private fun handleNavigateToLogin() {
        scope.launch { _events.send(SignUpEvent.NavigateToLogin) }
    }

    // ============================================
    // Step 1: Basic Info Handlers
    // ============================================

    private fun handleEmailChanged(email: String) {
        val isValid = validateEmail(email)
        _state.value = _state.value.copy(
            email = email,
            isEmailValid = isValid || email.isEmpty(),
            emailError = if (!isValid && email.isNotEmpty()) "Invalid email format" else null,
        )
    }

    private fun handlePasswordChanged(password: String) {
        val isValid = validatePassword(password)
        val passwordsMatch = password == _state.value.confirmPassword
        _state.value = _state.value.copy(
            password = password,
            isPasswordValid = isValid || password.isEmpty(),
            passwordError = if (!isValid && password.isNotEmpty()) {
                "Password must be at least 8 characters with uppercase, lowercase, and number"
            } else {
                null
            },
            isPasswordMatch = passwordsMatch || _state.value.confirmPassword.isEmpty(),
            confirmPasswordError = if (!passwordsMatch && _state.value.confirmPassword.isNotEmpty()) {
                "Passwords don't match"
            } else {
                null
            },
        )
    }

    private fun handleConfirmPasswordChanged(confirmPassword: String) {
        val passwordsMatch = _state.value.password == confirmPassword
        _state.value = _state.value.copy(
            confirmPassword = confirmPassword,
            isPasswordMatch = passwordsMatch || confirmPassword.isEmpty(),
            confirmPasswordError = if (!passwordsMatch && confirmPassword.isNotEmpty()) {
                "Passwords don't match"
            } else {
                null
            },
        )
    }

    private fun handleFirstNameChanged(firstName: String) {
        _state.value = _state.value.copy(firstName = firstName.trim())
    }

    private fun handleLastNameChanged(lastName: String) {
        _state.value = _state.value.copy(lastName = lastName.trim())
    }

    private fun handleUsernameChanged(username: String) {
        val sanitized = username.lowercase().replace(Regex("[^a-z0-9_]"), "")
        val isValid = validateUsername(sanitized)

        _state.value = _state.value.copy(
            username = sanitized,
            isUsernameValid = isValid || sanitized.isEmpty(),
            usernameError = if (!isValid && sanitized.isNotEmpty()) {
                "Username must be 3-20 characters, letters, numbers, and underscores only"
            } else {
                null
            },
            isCheckingUsername = isValid && sanitized.isNotEmpty(),
        )

        // Check username availability with debounce
        if (isValid && sanitized.isNotEmpty()) {
            scope.launch {
                delay(500) // Debounce
                if (_state.value.username == sanitized) {
                    checkUsernameAvailability(sanitized)
                }
            }
        }
    }

    private fun checkUsernameAvailability(username: String) {
        scope.launch {
            // Mock availability check - replace with actual API call
            delay(300)
            val isAvailable = !listOf("admin", "test", "user", "party").contains(username)
            _state.value = _state.value.copy(
                isUsernameAvailable = isAvailable,
                isCheckingUsername = false,
                usernameError = if (!isAvailable) "Username is already taken" else null,
            )
        }
    }

    private fun handleBirthDateChanged(birthDate: LocalDate) {
        val age = calculateAge(birthDate)
        val isValid = age >= minimumAge

        _state.value = _state.value.copy(
            birthDate = birthDate,
            isBirthDateValid = isValid,
            birthDateError = if (!isValid) "You must be at least $minimumAge years old" else null,
        )
    }

    private fun handleTogglePasswordVisibility() {
        _state.value = _state.value.copy(
            isPasswordVisible = !_state.value.isPasswordVisible,
        )
    }

    private fun handleToggleConfirmPasswordVisibility() {
        _state.value = _state.value.copy(
            isConfirmPasswordVisible = !_state.value.isConfirmPasswordVisible,
        )
    }

    private fun handleSubmitBasicInfo() {
        val currentState = _state.value

        // Validate all fields
        if (!validateEmail(currentState.email)) {
            _state.value = currentState.copy(
                isEmailValid = false,
                emailError = "Please enter a valid email",
            )
            return
        }
        if (!validatePassword(currentState.password)) {
            _state.value = currentState.copy(
                isPasswordValid = false,
                passwordError = "Password must be at least 8 characters with uppercase, lowercase, and number",
            )
            return
        }
        if (currentState.password != currentState.confirmPassword) {
            _state.value = currentState.copy(
                isPasswordMatch = false,
                confirmPasswordError = "Passwords don't match",
            )
            return
        }
        if (!validateUsername(currentState.username)) {
            _state.value = currentState.copy(
                isUsernameValid = false,
                usernameError = "Please enter a valid username",
            )
            return
        }
        if (currentState.birthDate == null || !currentState.isBirthDateValid) {
            _state.value = currentState.copy(
                isBirthDateValid = false,
                birthDateError = "Please enter a valid birth date",
            )
            return
        }
        if (currentState.firstName.isBlank()) {
            scope.launch { _events.send(SignUpEvent.ShowError("Please enter your first name")) }
            return
        }

        // Create account
        _state.value = currentState.copy(
            isLoading = true,
            signUpResult = SignUpResult.Loading,
        )

        scope.launch {
            authRepository.signUpWithEmail(
                email = currentState.email,
                password = currentState.password,
            ).onSuccess { result ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    signUpResult = SignUpResult.Success(result.userId),
                )
                handleNextStep()
            }.onFailure { exception ->
                val errorMessage = mapSignUpError(exception)
                _state.value = _state.value.copy(
                    isLoading = false,
                    signUpResult = SignUpResult.Error(errorMessage),
                )
                _events.send(SignUpEvent.ShowError(errorMessage))
            }
        }
    }

    // ============================================
    // Step 2: Avatar Handlers
    // ============================================

    private fun handleOpenCamera() {
        scope.launch { _events.send(SignUpEvent.RequestCameraPermission) }
    }

    private fun handleOpenGallery() {
        scope.launch { _events.send(SignUpEvent.OpenImagePicker) }
    }

    private fun handleAvatarSelected(uri: String, bytes: ByteArray?) {
        _state.value = _state.value.copy(
            avatarUri = uri,
            avatarBytes = bytes,
        )
    }

    private fun handleRemoveAvatar() {
        _state.value = _state.value.copy(
            avatarUri = null,
            avatarBytes = null,
        )
    }

    private fun handleUploadAvatar() {
        if (_state.value.avatarUri == null) {
            handleNextStep() // Skip if no avatar
            return
        }

        _state.value = _state.value.copy(isUploadingAvatar = true)

        scope.launch {
            // Mock upload - replace with actual implementation
            delay(1500)
            _state.value = _state.value.copy(isUploadingAvatar = false)
            handleNextStep()
        }
    }

    // ============================================
    // Step 3: Contact Sync Handlers
    // ============================================

    private fun handleRequestContactsPermission() {
        scope.launch { _events.send(SignUpEvent.RequestContactsPermission) }
    }

    private fun handleContactsPermissionResult(granted: Boolean) {
        _state.value = _state.value.copy(hasContactsPermission = granted)
        if (granted) {
            handleSyncContacts()
        }
    }

    private fun handleSyncContacts() {
        _state.value = _state.value.copy(isSyncingContacts = true)

        scope.launch {
            // Mock contact sync - replace with actual implementation
            delay(2000)
            val mockMatches = listOf(
                ContactMatch("u1", "John Smith", "johnsmith"),
                ContactMatch("u2", "Jane Doe", "janedoe"),
                ContactMatch("u3", "Mike Johnson", "mikej"),
                ContactMatch("u4", "Sarah Williams", "sarahw"),
                ContactMatch("u5", "David Brown", "davidb"),
            )
            _state.value = _state.value.copy(
                isSyncingContacts = false,
                contactMatches = mockMatches,
            )
        }
    }

    private fun handleToggleContactSelection(contactId: String) {
        val currentSelected = _state.value.selectedContacts
        val newSelected = if (currentSelected.contains(contactId)) {
            currentSelected - contactId
        } else {
            currentSelected + contactId
        }
        _state.value = _state.value.copy(selectedContacts = newSelected)
    }

    private fun handleSelectAllContacts() {
        val allIds = _state.value.contactMatches.map { it.userId }.toSet()
        val currentSelected = _state.value.selectedContacts
        val newSelected = if (currentSelected.size == allIds.size) {
            emptySet() // Deselect all
        } else {
            allIds // Select all
        }
        _state.value = _state.value.copy(selectedContacts = newSelected)
    }

    private fun handleSubmitContacts() {
        // In production, this would follow the selected contacts
        handleNextStep()
    }

    private fun handleSkipContactSync() {
        handleNextStep()
    }

    // ============================================
    // Step 4: Interest Tags Handlers
    // ============================================

    private fun handleLoadTags() {
        _state.value = _state.value.copy(isLoadingTags = true)

        scope.launch {
            // Mock tags - replace with actual API call
            delay(500)
            val mockTags = listOf(
                PartyTag("1", "House", "🏠", TagCategory.MUSIC_GENRE),
                PartyTag("2", "Hip Hop", "🎤", TagCategory.MUSIC_GENRE),
                PartyTag("3", "EDM", "🎧", TagCategory.MUSIC_GENRE),
                PartyTag("4", "Latin", "💃", TagCategory.MUSIC_GENRE),
                PartyTag("5", "Rock", "🎸", TagCategory.MUSIC_GENRE),
                PartyTag("6", "Birthday", "🎂", TagCategory.PARTY_TYPE),
                PartyTag("7", "Festival", "🎪", TagCategory.PARTY_TYPE),
                PartyTag("8", "Club", "🪩", TagCategory.PARTY_TYPE),
                PartyTag("9", "House Party", "🏡", TagCategory.PARTY_TYPE),
                PartyTag("10", "Rooftop", "🌃", TagCategory.PARTY_TYPE),
                PartyTag("11", "HYPE", "🔥", TagCategory.VIBE),
                PartyTag("12", "CHILL", "😎", TagCategory.VIBE),
                PartyTag("13", "WILD", "🤪", TagCategory.VIBE),
                PartyTag("14", "ROMANTIC", "💕", TagCategory.VIBE),
                PartyTag("15", "ELEGANT", "✨", TagCategory.VIBE),
            )
            _state.value = _state.value.copy(
                availableTags = mockTags,
                isLoadingTags = false,
            )
        }
    }

    private fun handleToggleTag(tagId: String) {
        val currentTags = _state.value.selectedTags
        val newTags = if (currentTags.contains(tagId)) {
            currentTags - tagId
        } else {
            currentTags + tagId
        }
        _state.value = _state.value.copy(selectedTags = newTags)
    }

    private fun handleSelectTags(tagIds: Set<String>) {
        _state.value = _state.value.copy(selectedTags = tagIds)
    }

    private fun handleClearTags() {
        _state.value = _state.value.copy(selectedTags = emptySet())
    }

    private fun handleSubmitTags() {
        if (_state.value.selectedTags.size < 3) {
            scope.launch { _events.send(SignUpEvent.ShowError("Please select at least 3 interests")) }
            return
        }
        handleNextStep()
    }

    // ============================================
    // Step 5: Social Linking Handlers
    // ============================================

    private fun handleConnectSocial(platform: SocialPlatform) {
        _state.value = _state.value.copy(connectingSocial = platform)

        scope.launch {
            // Mock OAuth flow - replace with actual implementation
            delay(1500)
            // Simulate successful connection with mock username
            val mockHandle = when (platform) {
                SocialPlatform.INSTAGRAM -> "party_user"
                SocialPlatform.TIKTOK -> "partytiktok"
                SocialPlatform.TWITTER -> "partytweets"
                SocialPlatform.FACEBOOK -> "partyface"
                SocialPlatform.PINTEREST -> "partypins"
            }
            handleSocialAuthSuccess(platform, mockHandle)
        }
    }

    private fun handleDisconnectSocial(platform: SocialPlatform) {
        val currentLinks = _state.value.socialLinks
        val newLinks = when (platform) {
            SocialPlatform.INSTAGRAM -> currentLinks.copy(instagram = null)
            SocialPlatform.TIKTOK -> currentLinks.copy(tiktok = null)
            SocialPlatform.TWITTER -> currentLinks.copy(twitter = null)
            SocialPlatform.FACEBOOK -> currentLinks.copy(facebook = null)
            SocialPlatform.PINTEREST -> currentLinks.copy(pinterest = null)
        }
        _state.value = _state.value.copy(socialLinks = newLinks)
    }

    private fun handleSocialAuthSuccess(platform: SocialPlatform, handle: String) {
        val currentLinks = _state.value.socialLinks
        val newLinks = when (platform) {
            SocialPlatform.INSTAGRAM -> currentLinks.copy(instagram = handle)
            SocialPlatform.TIKTOK -> currentLinks.copy(tiktok = handle)
            SocialPlatform.TWITTER -> currentLinks.copy(twitter = handle)
            SocialPlatform.FACEBOOK -> currentLinks.copy(facebook = handle)
            SocialPlatform.PINTEREST -> currentLinks.copy(pinterest = handle)
        }
        _state.value = _state.value.copy(
            socialLinks = newLinks,
            connectingSocial = null,
        )
    }

    private fun handleSocialAuthFailed(platform: SocialPlatform, error: String) {
        _state.value = _state.value.copy(connectingSocial = null)
        scope.launch {
            _events.send(SignUpEvent.ShowError("Failed to connect ${platform.name}: $error"))
        }
    }

    private fun handleSkipSocialLinking() {
        handleNextStep()
    }

    private fun handleSubmitSocialLinks() {
        handleNextStep()
    }

    // ============================================
    // Step 6: Completion Handlers
    // ============================================

    private fun handleCompleteSignUp() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true)
            delay(500) // Brief delay for UX
            _state.value = _state.value.copy(isLoading = false)
            _events.send(SignUpEvent.NavigateToHome)
        }
    }

    // ============================================
    // General Handlers
    // ============================================

    private fun handleClearError() {
        _state.value = _state.value.copy(
            signUpResult = SignUpResult.Idle,
            emailError = null,
            passwordError = null,
            confirmPasswordError = null,
            usernameError = null,
            birthDateError = null,
        )
    }

    private fun handleResetFlow() {
        _state.value = SignUpState()
    }

    // ============================================
    // Validation Helpers
    // ============================================

    private fun validateEmail(email: String): Boolean {
        if (email.isBlank()) return false
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }

    private fun validatePassword(password: String): Boolean {
        if (password.length < 8) return false
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        return hasUppercase && hasLowercase && hasDigit
    }

    private fun validateUsername(username: String): Boolean {
        if (username.length < 3 || username.length > 20) return false
        val usernameRegex = "^[a-z0-9_]+$".toRegex()
        return username.matches(usernameRegex)
    }

    private fun calculateAge(birthDate: LocalDate): Int {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return birthDate.yearsUntil(today)
    }

    // ============================================
    // Error Mapping
    // ============================================

    private fun mapSignUpError(exception: Throwable): String {
        return when {
            exception.message?.contains("email", ignoreCase = true) == true &&
                exception.message?.contains("exists", ignoreCase = true) == true ->
                "An account with this email already exists"
            exception.message?.contains("weak", ignoreCase = true) == true ->
                "Password is too weak"
            exception.message?.contains("network", ignoreCase = true) == true ->
                "Network error. Please check your connection"
            else -> exception.message ?: "An unexpected error occurred"
        }
    }
}
