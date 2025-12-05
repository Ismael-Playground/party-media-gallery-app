package com.partygallery.presentation.intent

/**
 * User intents for Profile screen.
 *
 * S5-001: ProfileIntent con acciones de perfil
 */
sealed class ProfileIntent {
    // Profile loading
    data class LoadProfile(val userId: String? = null) : ProfileIntent()
    data object RefreshProfile : ProfileIntent()

    // Profile editing
    data object StartEditing : ProfileIntent()
    data object CancelEditing : ProfileIntent()
    data class UpdateFirstName(val firstName: String) : ProfileIntent()
    data class UpdateLastName(val lastName: String) : ProfileIntent()
    data class UpdateUsername(val username: String) : ProfileIntent()
    data class UpdateBio(val bio: String) : ProfileIntent()
    data object SaveProfile : ProfileIntent()

    // Avatar/Cover
    data object ShowAvatarPicker : ProfileIntent()
    data object ShowCoverPicker : ProfileIntent()
    data class UpdateAvatar(val imageUri: String) : ProfileIntent()
    data class UpdateCover(val imageUri: String) : ProfileIntent()
    data object DismissPicker : ProfileIntent()

    // Social links
    data object ShowSocialLinksEditor : ProfileIntent()
    data class UpdateInstagram(val handle: String) : ProfileIntent()
    data class UpdateTikTok(val handle: String) : ProfileIntent()
    data class UpdateTwitter(val handle: String) : ProfileIntent()
    data class UpdateFacebook(val handle: String) : ProfileIntent()
    data class UpdateSpotify(val handle: String) : ProfileIntent()
    data object SaveSocialLinks : ProfileIntent()
    data object DismissSocialLinksEditor : ProfileIntent()

    // Follow actions
    data object FollowUser : ProfileIntent()
    data object UnfollowUser : ProfileIntent()
    data object ShowFollowers : ProfileIntent()
    data object ShowFollowing : ProfileIntent()
    data object DismissFollowList : ProfileIntent()

    // Content tabs
    data class SelectTab(val tab: ProfileTab) : ProfileIntent()

    // Navigation
    data object NavigateToSettings : ProfileIntent()
    data object NavigateBack : ProfileIntent()
    data object Logout : ProfileIntent()

    // Error handling
    data object DismissError : ProfileIntent()
}

/**
 * Profile content tabs.
 */
enum class ProfileTab(val label: String) {
    Posts("Posts"),
    Parties("Parties"),
    Tagged("Tagged"),
    Saved("Saved"),
}
