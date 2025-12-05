package com.partygallery.presentation.state

import com.partygallery.domain.model.PartyEvent
import com.partygallery.domain.model.SocialLinks
import com.partygallery.domain.model.User
import com.partygallery.domain.model.UserSummary
import com.partygallery.presentation.intent.ProfileTab

/**
 * State for the Profile screen.
 *
 * S5-001: ProfileState con datos de perfil y estadísticas
 */
data class ProfileState(
    // User data
    val user: User? = null,
    val isCurrentUser: Boolean = true,

    // Loading states
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isSaving: Boolean = false,
    val isUploadingAvatar: Boolean = false,
    val isUploadingCover: Boolean = false,

    // Edit mode
    val isEditing: Boolean = false,
    val editFirstName: String = "",
    val editLastName: String = "",
    val editUsername: String = "",
    val editBio: String = "",
    val editAvatarUri: String? = null,
    val editCoverUri: String? = null,

    // Validation errors
    val usernameError: String? = null,
    val firstNameError: String? = null,

    // Stats
    val postsCount: Int = 0,
    val partiesHostedCount: Int = 0,
    val partiesAttendedCount: Int = 0,

    // Content tabs
    val selectedTab: ProfileTab = ProfileTab.Posts,
    val posts: List<ProfileMediaItem> = emptyList(),
    val parties: List<PartyEvent> = emptyList(),
    val taggedMedia: List<ProfileMediaItem> = emptyList(),
    val savedMedia: List<ProfileMediaItem> = emptyList(),

    // Follow state
    val isFollowing: Boolean = false,
    val isFollowLoading: Boolean = false,

    // Dialogs
    val showAvatarPicker: Boolean = false,
    val showCoverPicker: Boolean = false,
    val showSocialLinksEditor: Boolean = false,
    val showFollowersList: Boolean = false,
    val showFollowingList: Boolean = false,

    // Social links editing
    val editSocialLinks: SocialLinks = SocialLinks(),

    // Follow lists
    val followers: List<UserSummary> = emptyList(),
    val following: List<UserSummary> = emptyList(),
    val isLoadingFollowList: Boolean = false,

    // Error
    val error: String? = null,
) {
    /**
     * Check if profile has any content.
     */
    val hasContent: Boolean
        get() = posts.isNotEmpty() || parties.isNotEmpty()

    /**
     * Check if edit form is valid.
     */
    val isEditFormValid: Boolean
        get() = editFirstName.isNotBlank() &&
            editUsername.isNotBlank() &&
            usernameError == null &&
            firstNameError == null

    /**
     * Check if there are unsaved changes.
     */
    val hasUnsavedChanges: Boolean
        get() = user?.let { u ->
            editFirstName != u.firstName ||
                editLastName != u.lastName ||
                editUsername != u.username ||
                editBio != (u.bio ?: "") ||
                editAvatarUri != null ||
                editCoverUri != null
        } ?: false

    /**
     * Get items for current tab.
     */
    val currentTabItems: List<Any>
        get() = when (selectedTab) {
            ProfileTab.Posts -> posts
            ProfileTab.Parties -> parties
            ProfileTab.Tagged -> taggedMedia
            ProfileTab.Saved -> savedMedia
        }

    /**
     * Check if current tab is empty.
     */
    val isCurrentTabEmpty: Boolean
        get() = currentTabItems.isEmpty()
}

/**
 * Profile media item for grid display.
 */
data class ProfileMediaItem(
    val id: String,
    val thumbnailUrl: String,
    val mediaUrl: String,
    val isVideo: Boolean,
    val partyId: String,
    val partyTitle: String,
    val likesCount: Int,
    val commentsCount: Int,
)
