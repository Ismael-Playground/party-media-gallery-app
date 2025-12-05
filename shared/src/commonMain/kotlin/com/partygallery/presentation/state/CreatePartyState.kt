package com.partygallery.presentation.state

import com.partygallery.domain.model.PartyPrivacy
import com.partygallery.domain.model.Venue
import kotlinx.datetime.LocalDateTime

/**
 * State for the Create Party form.
 *
 * S4-001: CreatePartyStore con form state
 */
data class CreatePartyState(
    // Form fields
    val title: String = "",
    val description: String = "",
    val venue: Venue? = null,
    val startDateTime: LocalDateTime? = null,
    val endDateTime: LocalDateTime? = null,
    val privacy: PartyPrivacy = PartyPrivacy.PUBLIC,
    val maxAttendees: Int? = null,
    val coverImageUri: String? = null,
    val tags: List<String> = emptyList(),
    val musicGenres: List<String> = emptyList(),

    // Form validation
    val titleError: String? = null,
    val venueError: String? = null,
    val dateTimeError: String? = null,

    // Loading states
    val isSubmitting: Boolean = false,
    val isSavingDraft: Boolean = false,
    val isUploadingImage: Boolean = false,

    // Result states
    val error: String? = null,
    val createdPartyId: String? = null,
    val isDraftSaved: Boolean = false,

    // Picker dialogs
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,
    val showEndDatePicker: Boolean = false,
    val showEndTimePicker: Boolean = false,
    val showVenuePicker: Boolean = false,
    val showTagPicker: Boolean = false,
    val showCoverImagePicker: Boolean = false,

    // Available options
    val availableTags: List<String> = defaultTags,
    val availableMusicGenres: List<String> = defaultMusicGenres,
) {
    /**
     * Check if the form is valid for submission.
     */
    val isFormValid: Boolean
        get() = title.isNotBlank() &&
            venue != null &&
            startDateTime != null &&
            titleError == null &&
            venueError == null &&
            dateTimeError == null

    /**
     * Check if any picker dialog is open.
     */
    val isPickerOpen: Boolean
        get() = showDatePicker ||
            showTimePicker ||
            showEndDatePicker ||
            showEndTimePicker ||
            showVenuePicker ||
            showTagPicker ||
            showCoverImagePicker

    /**
     * Check if form has unsaved changes.
     */
    val hasChanges: Boolean
        get() = title.isNotBlank() ||
            description.isNotBlank() ||
            venue != null ||
            startDateTime != null ||
            tags.isNotEmpty() ||
            coverImageUri != null

    companion object {
        val defaultTags = listOf(
            "House Party",
            "Club Night",
            "Birthday",
            "Rooftop",
            "Beach Party",
            "Pool Party",
            "Corporate",
            "Wedding",
            "Anniversary",
            "Graduation",
            "Holiday",
            "New Year",
            "Halloween",
            "Summer Vibes",
            "Underground",
            "VIP",
            "After Party",
            "Brunch",
            "Day Party",
            "Warehouse",
        )

        val defaultMusicGenres = listOf(
            "House",
            "Techno",
            "Hip Hop",
            "R&B",
            "Pop",
            "EDM",
            "Latin",
            "Reggaeton",
            "Afrobeats",
            "Rock",
            "Indie",
            "Jazz",
            "Soul",
            "Funk",
            "Disco",
            "Drum & Bass",
            "Dubstep",
            "Trance",
            "Ambient",
            "Mixed",
        )
    }
}
