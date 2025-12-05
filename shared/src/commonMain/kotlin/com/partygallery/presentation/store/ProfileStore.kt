package com.partygallery.presentation.store

import com.partygallery.domain.model.PartyEvent
import com.partygallery.domain.model.PartyPrivacy
import com.partygallery.domain.model.PartyStatus
import com.partygallery.domain.model.SocialLinks
import com.partygallery.domain.model.User
import com.partygallery.domain.model.UserSummary
import com.partygallery.domain.model.Venue
import com.partygallery.presentation.intent.ProfileIntent
import com.partygallery.presentation.intent.ProfileTab
import com.partygallery.presentation.state.ProfileMediaItem
import com.partygallery.presentation.state.ProfileState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days

/**
 * Store for Profile screen state management.
 *
 * S5-002: ProfileStore con estadísticas y acciones
 */
class ProfileStore {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    /**
     * Process an intent and update state.
     */
    fun processIntent(intent: ProfileIntent) {
        when (intent) {
            // Loading
            is ProfileIntent.LoadProfile -> loadProfile(intent.userId)
            ProfileIntent.RefreshProfile -> refreshProfile()

            // Editing
            ProfileIntent.StartEditing -> startEditing()
            ProfileIntent.CancelEditing -> cancelEditing()
            is ProfileIntent.UpdateFirstName -> updateFirstName(intent.firstName)
            is ProfileIntent.UpdateLastName -> updateLastName(intent.lastName)
            is ProfileIntent.UpdateUsername -> updateUsername(intent.username)
            is ProfileIntent.UpdateBio -> updateBio(intent.bio)
            ProfileIntent.SaveProfile -> saveProfile()

            // Avatar/Cover
            ProfileIntent.ShowAvatarPicker -> showAvatarPicker()
            ProfileIntent.ShowCoverPicker -> showCoverPicker()
            is ProfileIntent.UpdateAvatar -> updateAvatar(intent.imageUri)
            is ProfileIntent.UpdateCover -> updateCover(intent.imageUri)
            ProfileIntent.DismissPicker -> dismissPicker()

            // Social links
            ProfileIntent.ShowSocialLinksEditor -> showSocialLinksEditor()
            is ProfileIntent.UpdateInstagram -> updateInstagram(intent.handle)
            is ProfileIntent.UpdateTikTok -> updateTikTok(intent.handle)
            is ProfileIntent.UpdateTwitter -> updateTwitter(intent.handle)
            is ProfileIntent.UpdateFacebook -> updateFacebook(intent.handle)
            is ProfileIntent.UpdateSpotify -> updateSpotify(intent.handle)
            ProfileIntent.SaveSocialLinks -> saveSocialLinks()
            ProfileIntent.DismissSocialLinksEditor -> dismissSocialLinksEditor()

            // Follow
            ProfileIntent.FollowUser -> followUser()
            ProfileIntent.UnfollowUser -> unfollowUser()
            ProfileIntent.ShowFollowers -> showFollowers()
            ProfileIntent.ShowFollowing -> showFollowing()
            ProfileIntent.DismissFollowList -> dismissFollowList()

            // Tabs
            is ProfileIntent.SelectTab -> selectTab(intent.tab)

            // Navigation
            ProfileIntent.NavigateToSettings -> navigateToSettings()
            ProfileIntent.NavigateBack -> navigateBack()
            ProfileIntent.Logout -> logout()

            // Error
            ProfileIntent.DismissError -> dismissError()
        }
    }

    private fun loadProfile(userId: String?) {
        _state.update { it.copy(isLoading = true, error = null) }

        scope.launch {
            try {
                delay(800) // Simulate API call

                val isCurrentUser = userId == null
                val user = createMockUser(userId ?: "current_user")
                val posts = createMockPosts()
                val parties = createMockParties()

                _state.update {
                    it.copy(
                        user = user,
                        isCurrentUser = isCurrentUser,
                        isLoading = false,
                        postsCount = posts.size,
                        partiesHostedCount = parties.size,
                        partiesAttendedCount = 42,
                        posts = posts,
                        parties = parties,
                        taggedMedia = posts.take(3),
                        savedMedia = posts.takeLast(4),
                        isFollowing = !isCurrentUser && (userId?.hashCode() ?: 0) % 2 == 0,
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load profile",
                    )
                }
            }
        }
    }

    private fun refreshProfile() {
        _state.update { it.copy(isRefreshing = true) }
        scope.launch {
            delay(1000)
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private fun startEditing() {
        val user = _state.value.user ?: return
        _state.update {
            it.copy(
                isEditing = true,
                editFirstName = user.firstName,
                editLastName = user.lastName,
                editUsername = user.username,
                editBio = user.bio ?: "",
                editAvatarUri = null,
                editCoverUri = null,
            )
        }
    }

    private fun cancelEditing() {
        _state.update {
            it.copy(
                isEditing = false,
                editFirstName = "",
                editLastName = "",
                editUsername = "",
                editBio = "",
                editAvatarUri = null,
                editCoverUri = null,
                usernameError = null,
                firstNameError = null,
            )
        }
    }

    private fun updateFirstName(firstName: String) {
        _state.update {
            it.copy(
                editFirstName = firstName,
                firstNameError = if (firstName.isBlank()) "First name is required" else null,
            )
        }
    }

    private fun updateLastName(lastName: String) {
        _state.update { it.copy(editLastName = lastName) }
    }

    private fun updateUsername(username: String) {
        val error = when {
            username.isBlank() -> "Username is required"
            username.length < 3 -> "Username must be at least 3 characters"
            !username.matches(Regex("^[a-zA-Z0-9_]+$")) -> "Only letters, numbers and underscore allowed"
            else -> null
        }
        _state.update {
            it.copy(
                editUsername = username,
                usernameError = error,
            )
        }
    }

    private fun updateBio(bio: String) {
        _state.update { it.copy(editBio = bio.take(150)) }
    }

    private fun saveProfile() {
        if (!_state.value.isEditFormValid) return

        _state.update { it.copy(isSaving = true) }

        scope.launch {
            try {
                delay(1500) // Simulate API call

                val currentState = _state.value
                val updatedUser = currentState.user?.copy(
                    firstName = currentState.editFirstName,
                    lastName = currentState.editLastName,
                    username = currentState.editUsername,
                    bio = currentState.editBio.ifBlank { null },
                    avatarUrl = currentState.editAvatarUri ?: currentState.user?.avatarUrl,
                    coverPhotoUrl = currentState.editCoverUri ?: currentState.user?.coverPhotoUrl,
                )

                _state.update {
                    it.copy(
                        user = updatedUser,
                        isEditing = false,
                        isSaving = false,
                    )
                }

                _events.emit(ProfileEvent.ProfileSaved)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "Failed to save profile",
                    )
                }
            }
        }
    }

    private fun showAvatarPicker() {
        _state.update { it.copy(showAvatarPicker = true) }
    }

    private fun showCoverPicker() {
        _state.update { it.copy(showCoverPicker = true) }
    }

    private fun updateAvatar(imageUri: String) {
        _state.update {
            it.copy(
                editAvatarUri = imageUri,
                showAvatarPicker = false,
            )
        }
    }

    private fun updateCover(imageUri: String) {
        _state.update {
            it.copy(
                editCoverUri = imageUri,
                showCoverPicker = false,
            )
        }
    }

    private fun dismissPicker() {
        _state.update {
            it.copy(
                showAvatarPicker = false,
                showCoverPicker = false,
            )
        }
    }

    private fun showSocialLinksEditor() {
        val user = _state.value.user ?: return
        _state.update {
            it.copy(
                showSocialLinksEditor = true,
                editSocialLinks = user.socialLinks,
            )
        }
    }

    private fun updateInstagram(handle: String) {
        _state.update {
            it.copy(editSocialLinks = it.editSocialLinks.copy(instagram = handle.ifBlank { null }))
        }
    }

    private fun updateTikTok(handle: String) {
        _state.update {
            it.copy(editSocialLinks = it.editSocialLinks.copy(tiktok = handle.ifBlank { null }))
        }
    }

    private fun updateTwitter(handle: String) {
        _state.update {
            it.copy(editSocialLinks = it.editSocialLinks.copy(twitter = handle.ifBlank { null }))
        }
    }

    private fun updateFacebook(handle: String) {
        _state.update {
            it.copy(editSocialLinks = it.editSocialLinks.copy(facebook = handle.ifBlank { null }))
        }
    }

    private fun updateSpotify(handle: String) {
        _state.update {
            it.copy(editSocialLinks = it.editSocialLinks.copy(spotify = handle.ifBlank { null }))
        }
    }

    private fun saveSocialLinks() {
        scope.launch {
            delay(500)
            val updatedUser = _state.value.user?.copy(
                socialLinks = _state.value.editSocialLinks,
            )
            _state.update {
                it.copy(
                    user = updatedUser,
                    showSocialLinksEditor = false,
                )
            }
        }
    }

    private fun dismissSocialLinksEditor() {
        _state.update { it.copy(showSocialLinksEditor = false) }
    }

    private fun followUser() {
        _state.update { it.copy(isFollowLoading = true) }

        scope.launch {
            delay(500)
            val updatedUser = _state.value.user?.copy(
                followersCount = (_state.value.user?.followersCount ?: 0) + 1,
            )
            _state.update {
                it.copy(
                    user = updatedUser,
                    isFollowing = true,
                    isFollowLoading = false,
                )
            }
        }
    }

    private fun unfollowUser() {
        _state.update { it.copy(isFollowLoading = true) }

        scope.launch {
            delay(500)
            val updatedUser = _state.value.user?.copy(
                followersCount = maxOf(0, (_state.value.user?.followersCount ?: 0) - 1),
            )
            _state.update {
                it.copy(
                    user = updatedUser,
                    isFollowing = false,
                    isFollowLoading = false,
                )
            }
        }
    }

    private fun showFollowers() {
        _state.update { it.copy(showFollowersList = true, isLoadingFollowList = true) }

        scope.launch {
            delay(800)
            _state.update {
                it.copy(
                    followers = createMockFollowers(),
                    isLoadingFollowList = false,
                )
            }
        }
    }

    private fun showFollowing() {
        _state.update { it.copy(showFollowingList = true, isLoadingFollowList = true) }

        scope.launch {
            delay(800)
            _state.update {
                it.copy(
                    following = createMockFollowing(),
                    isLoadingFollowList = false,
                )
            }
        }
    }

    private fun dismissFollowList() {
        _state.update {
            it.copy(
                showFollowersList = false,
                showFollowingList = false,
            )
        }
    }

    private fun selectTab(tab: ProfileTab) {
        _state.update { it.copy(selectedTab = tab) }
    }

    private fun navigateToSettings() {
        scope.launch {
            _events.emit(ProfileEvent.NavigateToSettings)
        }
    }

    private fun navigateBack() {
        scope.launch {
            _events.emit(ProfileEvent.NavigateBack)
        }
    }

    private fun logout() {
        scope.launch {
            _events.emit(ProfileEvent.Logout)
        }
    }

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }

    // Mock data generators
    private fun createMockUser(userId: String): User {
        val now = Clock.System.now()
        return User(
            id = userId,
            firebaseId = "firebase_$userId",
            email = "party.lover@gallery.com",
            username = "party_queen",
            firstName = "Sarah",
            lastName = "Johnson",
            bio = "Living for the night life. DJ & event organizer. Let's party!",
            avatarUrl = null,
            coverPhotoUrl = null,
            socialLinks = SocialLinks(
                instagram = "party_queen",
                tiktok = "partyqueensarah",
                spotify = "sarahjohnson",
            ),
            tags = listOf("House Party", "Club Night", "EDM", "Rooftop"),
            followersCount = 2847,
            followingCount = 523,
            isVerified = true,
            isProfileComplete = true,
            createdAt = now,
        )
    }

    private fun createMockPosts(): List<ProfileMediaItem> {
        return (1..12).map { i ->
            ProfileMediaItem(
                id = "media_$i",
                thumbnailUrl = "https://picsum.photos/300/300?random=$i",
                mediaUrl = "https://picsum.photos/1080/1080?random=$i",
                isVideo = i % 4 == 0,
                partyId = "party_${i % 3}",
                partyTitle = listOf("Rooftop Vibes", "Club Inferno", "Beach Sunset")[i % 3],
                likesCount = (50..500).random(),
                commentsCount = (5..50).random(),
            )
        }
    }

    private fun createMockParties(): List<PartyEvent> {
        val now = Clock.System.now()

        return listOf(
            PartyEvent(
                id = "party_1",
                hostId = "current_user",
                coHosts = emptyList(),
                title = "Summer Rooftop Bash",
                description = "Join us for the hottest rooftop party of the summer!",
                venue = Venue(
                    name = "Sky Lounge",
                    address = "123 High Street",
                    latitude = 40.7128,
                    longitude = -74.0060,
                ),
                startsAt = now + 2.days,
                privacy = PartyPrivacy.PUBLIC,
                status = PartyStatus.PLANNED,
                coverImageUrl = null,
                tags = listOf("Rooftop", "Summer Vibes"),
                musicGenres = listOf("House", "EDM"),
                attendeesCount = 156,
                maxAttendees = 200,
                createdAt = now,
            ),
            PartyEvent(
                id = "party_2",
                hostId = "current_user",
                coHosts = emptyList(),
                title = "Underground Techno Night",
                description = "Deep beats in the depths of the city.",
                venue = Venue(
                    name = "The Basement",
                    address = "456 Dark Alley",
                    latitude = 40.7128,
                    longitude = -74.0060,
                ),
                startsAt = now + 7.days,
                privacy = PartyPrivacy.INVITE_ONLY,
                status = PartyStatus.PLANNED,
                coverImageUrl = null,
                tags = listOf("Underground", "Club Night"),
                musicGenres = listOf("Techno", "Drum & Bass"),
                attendeesCount = 89,
                maxAttendees = 100,
                createdAt = now,
            ),
        )
    }

    private fun createMockFollowers(): List<UserSummary> {
        return (1..20).map { i ->
            UserSummary(
                id = "user_$i",
                username = "partyfan_$i",
                displayName = "Party Fan $i",
                avatarUrl = null,
                isVerified = i <= 3,
            )
        }
    }

    private fun createMockFollowing(): List<UserSummary> {
        return (1..15).map { i ->
            UserSummary(
                id = "following_$i",
                username = "dj_$i",
                displayName = "DJ ${listOf("Beats", "Vibes", "Night", "Fire", "Wave")[i % 5]}",
                avatarUrl = null,
                isVerified = i <= 5,
            )
        }
    }
}

/**
 * Events emitted by ProfileStore.
 */
sealed class ProfileEvent {
    data object ProfileSaved : ProfileEvent()
    data object NavigateToSettings : ProfileEvent()
    data object NavigateBack : ProfileEvent()
    data object Logout : ProfileEvent()
}
