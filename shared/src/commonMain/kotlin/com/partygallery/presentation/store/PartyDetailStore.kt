package com.partygallery.presentation.store

import com.partygallery.domain.model.MediaContent
import com.partygallery.domain.model.MediaMetadata
import com.partygallery.domain.model.MediaSocialMetrics
import com.partygallery.domain.model.MediaType
import com.partygallery.domain.model.PartyEvent
import com.partygallery.domain.model.PartyMood
import com.partygallery.domain.model.PartyStatus
import com.partygallery.domain.model.UserSummary
import com.partygallery.domain.model.Venue
import com.partygallery.presentation.intent.PartyDetailIntent
import com.partygallery.presentation.state.MediaFilterOption
import com.partygallery.presentation.state.PartyDetailState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * Party Detail Screen MVI Store.
 *
 * S4-003: PartyDetailStore
 */
class PartyDetailStore(
    private val partyId: String,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
) {
    private val _state = MutableStateFlow(PartyDetailState())
    val state: StateFlow<PartyDetailState> = _state.asStateFlow()

    init {
        processIntent(PartyDetailIntent.LoadParty(partyId))
    }

    fun processIntent(intent: PartyDetailIntent) {
        when (intent) {
            is PartyDetailIntent.LoadParty -> loadParty(intent.partyId)
            is PartyDetailIntent.RefreshParty -> refreshParty()
            is PartyDetailIntent.SelectMediaFilter -> selectMediaFilter(intent.filter)
            is PartyDetailIntent.LoadMoreMedia -> loadMoreMedia()
            is PartyDetailIntent.ToggleRsvp -> toggleRsvp()
            is PartyDetailIntent.LikeMedia -> likeMedia(intent.mediaId)
            is PartyDetailIntent.UnlikeMedia -> unlikeMedia(intent.mediaId)
            is PartyDetailIntent.NavigateToUploadMedia -> { /* Handled by UI */ }
            is PartyDetailIntent.NavigateBack -> { /* Handled by UI */ }
        }
    }

    private fun loadParty(partyId: String) {
        scope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            delay(600)

            val party = generateMockParty(partyId)
            val media = generateMockMedia(partyId)

            _state.update {
                it.copy(
                    isLoading = false,
                    party = party,
                    mediaItems = media,
                    isUserAttending = party.isUserAttending,
                )
            }
        }
    }

    private fun refreshParty() {
        scope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(500)
            val party = _state.value.party?.copy(
                attendeesCount = (50..250).random(),
                mediaCount = (20..100).random(),
            )
            _state.update { it.copy(isLoading = false, party = party) }
        }
    }

    private fun selectMediaFilter(filter: MediaFilterOption) {
        _state.update { it.copy(selectedMediaFilter = filter, isLoadingMedia = true) }
        scope.launch {
            delay(300)
            val allMedia = generateMockMedia(partyId)
            val filteredMedia = when (filter) {
                MediaFilterOption.All -> allMedia
                MediaFilterOption.Photos -> allMedia.filter { it.type == MediaType.PHOTO }
                MediaFilterOption.Videos -> allMedia.filter { it.type == MediaType.VIDEO }
                MediaFilterOption.Hype -> allMedia.filter { it.mood == PartyMood.HYPE }
                MediaFilterOption.Chill -> allMedia.filter { it.mood == PartyMood.CHILL }
                MediaFilterOption.Wild -> allMedia.filter { it.mood == PartyMood.WILD }
            }
            _state.update { it.copy(mediaItems = filteredMedia, isLoadingMedia = false) }
        }
    }

    private fun loadMoreMedia() {
        scope.launch {
            val currentMedia = _state.value.mediaItems
            val moreMedia = generateMockMedia(partyId, startIndex = currentMedia.size)
            _state.update { it.copy(mediaItems = currentMedia + moreMedia) }
        }
    }

    private fun toggleRsvp() {
        scope.launch {
            _state.update { it.copy(isRsvpLoading = true) }
            delay(500)
            val newAttending = !_state.value.isUserAttending
            val party = _state.value.party?.copy(
                isUserAttending = newAttending,
                attendeesCount = if (newAttending) {
                    (_state.value.party?.attendeesCount ?: 0) + 1
                } else {
                    (_state.value.party?.attendeesCount ?: 1) - 1
                },
            )
            _state.update {
                it.copy(
                    isRsvpLoading = false,
                    isUserAttending = newAttending,
                    party = party,
                )
            }
        }
    }

    private fun likeMedia(mediaId: String) {
        _state.update { state ->
            state.copy(
                mediaItems = state.mediaItems.map { media ->
                    if (media.id == mediaId) {
                        media.copy(
                            socialMetrics = media.socialMetrics.copy(
                                isLikedByUser = true,
                                likesCount = media.socialMetrics.likesCount + 1,
                            ),
                        )
                    } else {
                        media
                    }
                },
            )
        }
    }

    private fun unlikeMedia(mediaId: String) {
        _state.update { state ->
            state.copy(
                mediaItems = state.mediaItems.map { media ->
                    if (media.id == mediaId) {
                        media.copy(
                            socialMetrics = media.socialMetrics.copy(
                                isLikedByUser = false,
                                likesCount = (media.socialMetrics.likesCount - 1).coerceAtLeast(0),
                            ),
                        )
                    } else {
                        media
                    }
                },
            )
        }
    }

    private fun generateMockParty(partyId: String): PartyEvent {
        val partyNames = listOf(
            "Summer Beach Bash",
            "Rooftop Vibes",
            "Neon Nights",
            "House Party Extravaganza",
            "Club Night Fever",
        )
        val venues = listOf(
            "Skybar Rooftop",
            "Club Paradiso",
            "Beach House Miami",
            "The Warehouse NYC",
            "Downtown Loft LA",
        )
        val index = partyId.hashCode().mod(partyNames.size).let { if (it < 0) it + partyNames.size else it }

        return PartyEvent(
            id = partyId,
            hostId = "host_123",
            host = UserSummary(
                id = "host_123",
                username = "alex_m",
                displayName = "Alex Martinez",
                avatarUrl = null,
            ),
            title = partyNames[index],
            description = "Join us for an unforgettable night! Great music, amazing vibes, " +
                "and the best crowd in town. Don't miss out on the party of the year!",
            venue = Venue(
                name = venues[index],
                address = "123 Party Street",
                city = "Miami",
                country = "USA",
                latitude = 25.7617,
                longitude = -80.1918,
            ),
            startsAt = Clock.System.now(),
            status = if (index % 2 == 0) PartyStatus.LIVE else PartyStatus.PLANNED,
            attendeesCount = (50..200).random(),
            mediaCount = (20..80).random(),
            isUserAttending = index % 3 == 0,
            isUserHost = false,
            tags = listOf("music", "dancing", "nightlife"),
            musicGenres = listOf("EDM", "House", "Hip-Hop"),
            createdAt = Clock.System.now(),
        )
    }

    private fun generateMockMedia(partyId: String, startIndex: Int = 0): List<MediaContent> {
        val moods = PartyMood.entries.toTypedArray()
        val captions = listOf(
            "Best night ever!",
            "Vibes are unreal",
            "Party mode activated",
            "Living my best life",
            "This is incredible",
            null,
            null,
        )

        return (startIndex until startIndex + 12).map { index ->
            val isVideo = index % 4 == 0
            MediaContent(
                id = "media_${partyId}_$index",
                partyEventId = partyId,
                uploaderId = "user_${index % 5}",
                uploader = UserSummary(
                    id = "user_${index % 5}",
                    username = listOf("alex_m", "jordan_k", "taylor_s", "morgan_b", "casey_l")[index % 5],
                    displayName = listOf("Alex M.", "Jordan K.", "Taylor S.", "Morgan B.", "Casey L.")[index % 5],
                    avatarUrl = null,
                ),
                type = if (isVideo) MediaType.VIDEO else MediaType.PHOTO,
                url = "",
                thumbnailUrl = null,
                caption = captions[index % captions.size],
                mood = moods[index % moods.size],
                metadata = MediaMetadata(
                    width = 1080,
                    height = 1920,
                    durationSeconds = if (isVideo) (15..60).random() else null,
                ),
                socialMetrics = MediaSocialMetrics(
                    likesCount = (10..500).random(),
                    commentsCount = (0..50).random(),
                    isLikedByUser = index % 3 == 0,
                ),
                createdAt = Clock.System.now(),
            )
        }
    }
}
