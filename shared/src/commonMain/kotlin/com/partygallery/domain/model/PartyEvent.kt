package com.partygallery.domain.model

import kotlinx.datetime.Instant

/**
 * Party event status enum.
 */
enum class PartyStatus {
    PLANNED,
    LIVE,
    ENDED,
    CANCELLED
}

/**
 * Privacy level for party events.
 */
enum class PartyPrivacy {
    PUBLIC,
    FRIENDS_ONLY,
    INVITE_ONLY,
    PRIVATE
}

/**
 * Venue information for a party event.
 *
 * S1-008: Modelo PartyEvent (domain)
 */
data class Venue(
    val name: String,
    val address: String? = null,
    val city: String? = null,
    val country: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val placeId: String? = null
) {
    val hasCoordinates: Boolean
        get() = latitude != null && longitude != null

    val fullAddress: String
        get() = listOfNotNull(address, city, country).joinToString(", ")
}

/**
 * Party event domain model.
 *
 * S1-008: Modelo PartyEvent (domain)
 */
data class PartyEvent(
    val id: String,
    val hostId: String,
    val host: UserSummary? = null,
    val coHosts: List<UserSummary> = emptyList(),
    val title: String,
    val description: String? = null,
    val venue: Venue,
    val coverImageUrl: String? = null,
    val startsAt: Instant,
    val endsAt: Instant? = null,
    val status: PartyStatus = PartyStatus.PLANNED,
    val privacy: PartyPrivacy = PartyPrivacy.PUBLIC,
    val tags: List<String> = emptyList(),
    val musicGenres: List<String> = emptyList(),
    val maxAttendees: Int? = null,
    val attendeesCount: Int = 0,
    val mediaCount: Int = 0,
    val isUserAttending: Boolean = false,
    val isUserHost: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant? = null
) {
    val isLive: Boolean
        get() = status == PartyStatus.LIVE

    val isUpcoming: Boolean
        get() = status == PartyStatus.PLANNED

    val hasEnded: Boolean
        get() = status == PartyStatus.ENDED || status == PartyStatus.CANCELLED

    val isFull: Boolean
        get() = maxAttendees != null && attendeesCount >= maxAttendees
}

/**
 * Simplified party event representation for lists.
 */
data class PartyEventSummary(
    val id: String,
    val title: String,
    val venueName: String,
    val coverImageUrl: String?,
    val startsAt: Instant,
    val status: PartyStatus,
    val attendeesCount: Int,
    val isLive: Boolean
)

/**
 * Extension to convert full PartyEvent to PartyEventSummary.
 */
fun PartyEvent.toSummary(): PartyEventSummary = PartyEventSummary(
    id = id,
    title = title,
    venueName = venue.name,
    coverImageUrl = coverImageUrl,
    startsAt = startsAt,
    status = status,
    attendeesCount = attendeesCount,
    isLive = isLive
)
