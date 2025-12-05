package com.partygallery.presentation.store

import com.partygallery.domain.model.PartyEvent
import com.partygallery.domain.model.PartyPrivacy
import com.partygallery.domain.model.PartyStatus
import com.partygallery.domain.model.Venue
import com.partygallery.presentation.intent.CreatePartyIntent
import com.partygallery.presentation.state.CreatePartyState
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
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/**
 * Store for Create Party form state management.
 *
 * S4-001: CreatePartyStore con form state
 */
class CreatePartyStore {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(CreatePartyState())
    val state: StateFlow<CreatePartyState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<CreatePartyEvent>()
    val events: SharedFlow<CreatePartyEvent> = _events.asSharedFlow()

    /**
     * Process an intent and update state.
     */
    fun processIntent(intent: CreatePartyIntent) {
        when (intent) {
            // Form field updates
            is CreatePartyIntent.UpdateTitle -> updateTitle(intent.title)
            is CreatePartyIntent.UpdateDescription -> updateDescription(intent.description)
            is CreatePartyIntent.UpdateVenue -> updateVenue(intent.venue)
            is CreatePartyIntent.UpdateStartDateTime -> updateStartDateTime(intent.dateTime)
            is CreatePartyIntent.UpdateEndDateTime -> updateEndDateTime(intent.dateTime)
            is CreatePartyIntent.UpdatePrivacy -> updatePrivacy(intent.privacy)
            is CreatePartyIntent.UpdateMaxAttendees -> updateMaxAttendees(intent.max)
            is CreatePartyIntent.UpdateCoverImage -> updateCoverImage(intent.imageUri)

            // Tags management
            is CreatePartyIntent.AddTag -> addTag(intent.tag)
            is CreatePartyIntent.RemoveTag -> removeTag(intent.tag)

            // Music genres management
            is CreatePartyIntent.AddMusicGenre -> addMusicGenre(intent.genre)
            is CreatePartyIntent.RemoveMusicGenre -> removeMusicGenre(intent.genre)

            // Actions
            CreatePartyIntent.Submit -> submit()
            CreatePartyIntent.SaveDraft -> saveDraft()
            CreatePartyIntent.ClearForm -> clearForm()
            CreatePartyIntent.DismissError -> dismissError()

            // Navigation
            CreatePartyIntent.NavigateBack -> navigateBack()

            // Picker dialogs
            CreatePartyIntent.ShowDatePicker -> showDatePicker()
            CreatePartyIntent.ShowTimePicker -> showTimePicker()
            CreatePartyIntent.ShowEndDatePicker -> showEndDatePicker()
            CreatePartyIntent.ShowEndTimePicker -> showEndTimePicker()
            CreatePartyIntent.ShowVenuePicker -> showVenuePicker()
            CreatePartyIntent.ShowTagPicker -> showTagPicker()
            CreatePartyIntent.ShowCoverImagePicker -> showCoverImagePicker()
            CreatePartyIntent.DismissPicker -> dismissPicker()
        }
    }

    private fun updateTitle(title: String) {
        _state.update { current ->
            current.copy(
                title = title,
                titleError = validateTitle(title),
            )
        }
    }

    private fun updateDescription(description: String) {
        _state.update { it.copy(description = description) }
    }

    private fun updateVenue(venue: Venue) {
        _state.update { current ->
            current.copy(
                venue = venue,
                venueError = null,
            )
        }
        dismissPicker()
    }

    private fun updateStartDateTime(dateTime: LocalDateTime) {
        _state.update { current ->
            val endDateTime = current.endDateTime
            val dateTimeError = validateDateTime(dateTime, endDateTime)
            current.copy(
                startDateTime = dateTime,
                dateTimeError = dateTimeError,
            )
        }
        dismissPicker()
    }

    private fun updateEndDateTime(dateTime: LocalDateTime?) {
        _state.update { current ->
            val startDateTime = current.startDateTime
            val dateTimeError = if (startDateTime != null && dateTime != null) {
                validateDateTime(startDateTime, dateTime)
            } else {
                null
            }
            current.copy(
                endDateTime = dateTime,
                dateTimeError = dateTimeError,
            )
        }
        dismissPicker()
    }

    private fun updatePrivacy(privacy: PartyPrivacy) {
        _state.update { it.copy(privacy = privacy) }
    }

    private fun updateMaxAttendees(max: Int?) {
        _state.update { it.copy(maxAttendees = max) }
    }

    private fun updateCoverImage(imageUri: String?) {
        _state.update { it.copy(coverImageUri = imageUri) }
        dismissPicker()
    }

    private fun addTag(tag: String) {
        _state.update { current ->
            if (tag !in current.tags) {
                current.copy(tags = current.tags + tag)
            } else {
                current
            }
        }
    }

    private fun removeTag(tag: String) {
        _state.update { current ->
            current.copy(tags = current.tags - tag)
        }
    }

    private fun addMusicGenre(genre: String) {
        _state.update { current ->
            if (genre !in current.musicGenres) {
                current.copy(musicGenres = current.musicGenres + genre)
            } else {
                current
            }
        }
    }

    private fun removeMusicGenre(genre: String) {
        _state.update { current ->
            current.copy(musicGenres = current.musicGenres - genre)
        }
    }

    private fun submit() {
        val currentState = _state.value

        // Validate all fields
        val titleError = validateTitle(currentState.title)
        val venueError = if (currentState.venue == null) "Please select a venue" else null
        val dateTimeError = if (currentState.startDateTime == null) {
            "Please select a start date and time"
        } else {
            validateDateTime(currentState.startDateTime, currentState.endDateTime)
        }

        if (titleError != null || venueError != null || dateTimeError != null) {
            _state.update {
                it.copy(
                    titleError = titleError,
                    venueError = venueError,
                    dateTimeError = dateTimeError,
                )
            }
            return
        }

        _state.update { it.copy(isSubmitting = true, error = null) }

        scope.launch {
            try {
                // Simulate API call
                delay(1500)

                // Create party event (mock)
                val partyId = "party_${Clock.System.now().toEpochMilliseconds()}"

                _state.update {
                    it.copy(
                        isSubmitting = false,
                        createdPartyId = partyId,
                    )
                }

                _events.emit(CreatePartyEvent.PartyCreated(partyId))
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        error = e.message ?: "Failed to create party",
                    )
                }
            }
        }
    }

    private fun saveDraft() {
        _state.update { it.copy(isSavingDraft = true) }

        scope.launch {
            try {
                // Simulate saving draft
                delay(500)

                _state.update {
                    it.copy(
                        isSavingDraft = false,
                        isDraftSaved = true,
                    )
                }

                _events.emit(CreatePartyEvent.DraftSaved)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSavingDraft = false,
                        error = e.message ?: "Failed to save draft",
                    )
                }
            }
        }
    }

    private fun clearForm() {
        _state.update { CreatePartyState() }
    }

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }

    private fun navigateBack() {
        scope.launch {
            _events.emit(CreatePartyEvent.NavigateBack)
        }
    }

    // Picker dialog methods
    private fun showDatePicker() {
        _state.update { it.copy(showDatePicker = true) }
    }

    private fun showTimePicker() {
        _state.update { it.copy(showTimePicker = true) }
    }

    private fun showEndDatePicker() {
        _state.update { it.copy(showEndDatePicker = true) }
    }

    private fun showEndTimePicker() {
        _state.update { it.copy(showEndTimePicker = true) }
    }

    private fun showVenuePicker() {
        _state.update { it.copy(showVenuePicker = true) }
    }

    private fun showTagPicker() {
        _state.update { it.copy(showTagPicker = true) }
    }

    private fun showCoverImagePicker() {
        _state.update { it.copy(showCoverImagePicker = true) }
    }

    private fun dismissPicker() {
        _state.update {
            it.copy(
                showDatePicker = false,
                showTimePicker = false,
                showEndDatePicker = false,
                showEndTimePicker = false,
                showVenuePicker = false,
                showTagPicker = false,
                showCoverImagePicker = false,
            )
        }
    }

    // Validation helpers
    private fun validateTitle(title: String): String? {
        return when {
            title.isBlank() -> "Party title is required"
            title.length < 3 -> "Title must be at least 3 characters"
            title.length > 100 -> "Title must be less than 100 characters"
            else -> null
        }
    }

    private fun validateDateTime(
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime?,
    ): String? {
        val now = Clock.System.now()
        val startInstant = startDateTime.toInstant(TimeZone.currentSystemDefault())

        if (startInstant < now) {
            return "Start time must be in the future"
        }

        if (endDateTime != null) {
            val endInstant = endDateTime.toInstant(TimeZone.currentSystemDefault())
            if (endInstant <= startInstant) {
                return "End time must be after start time"
            }
        }

        return null
    }
}

/**
 * Events emitted by CreatePartyStore.
 */
sealed class CreatePartyEvent {
    data class PartyCreated(val partyId: String) : CreatePartyEvent()
    data object DraftSaved : CreatePartyEvent()
    data object NavigateBack : CreatePartyEvent()
}
