package com.partygallery.presentation.intent

import com.partygallery.domain.model.PartyPrivacy
import com.partygallery.domain.model.Venue
import kotlinx.datetime.LocalDateTime

/**
 * Intents for creating a party event.
 *
 * S4-001: CreatePartyStore con form state
 */
sealed class CreatePartyIntent {
    // Form field updates
    data class UpdateTitle(val title: String) : CreatePartyIntent()
    data class UpdateDescription(val description: String) : CreatePartyIntent()
    data class UpdateVenue(val venue: Venue) : CreatePartyIntent()
    data class UpdateStartDateTime(val dateTime: LocalDateTime) : CreatePartyIntent()
    data class UpdateEndDateTime(val dateTime: LocalDateTime?) : CreatePartyIntent()
    data class UpdatePrivacy(val privacy: PartyPrivacy) : CreatePartyIntent()
    data class UpdateMaxAttendees(val max: Int?) : CreatePartyIntent()
    data class UpdateCoverImage(val imageUri: String?) : CreatePartyIntent()

    // Tags management
    data class AddTag(val tag: String) : CreatePartyIntent()
    data class RemoveTag(val tag: String) : CreatePartyIntent()

    // Music genres management
    data class AddMusicGenre(val genre: String) : CreatePartyIntent()
    data class RemoveMusicGenre(val genre: String) : CreatePartyIntent()

    // Actions
    data object Submit : CreatePartyIntent()
    data object SaveDraft : CreatePartyIntent()
    data object ClearForm : CreatePartyIntent()
    data object DismissError : CreatePartyIntent()

    // Navigation
    data object NavigateBack : CreatePartyIntent()

    // Picker dialogs
    data object ShowDatePicker : CreatePartyIntent()
    data object ShowTimePicker : CreatePartyIntent()
    data object ShowEndDatePicker : CreatePartyIntent()
    data object ShowEndTimePicker : CreatePartyIntent()
    data object ShowVenuePicker : CreatePartyIntent()
    data object ShowTagPicker : CreatePartyIntent()
    data object ShowCoverImagePicker : CreatePartyIntent()
    data object DismissPicker : CreatePartyIntent()
}
