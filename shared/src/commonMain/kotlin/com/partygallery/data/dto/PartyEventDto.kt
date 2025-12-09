package com.partygallery.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for PartyEvent entity.
 * Used for Firebase Firestore serialization/deserialization.
 *
 * S2.5-007: Data mappers (DTO to Domain)
 */
@Serializable
data class PartyEventDto(
    val id: String = "",
    @SerialName("host_id")
    val hostId: String = "",
    val host: UserSummaryDto? = null,
    @SerialName("co_hosts")
    val coHosts: List<UserSummaryDto> = emptyList(),
    val title: String = "",
    val description: String? = null,
    val venue: VenueDto = VenueDto(),
    @SerialName("cover_image_url")
    val coverImageUrl: String? = null,
    @SerialName("starts_at")
    val startsAt: Long = 0L, // Unix timestamp millis
    @SerialName("ends_at")
    val endsAt: Long? = null,
    val status: String = "PLANNED", // PLANNED, LIVE, ENDED, CANCELLED
    val privacy: String = "PUBLIC", // PUBLIC, FRIENDS_ONLY, INVITE_ONLY, PRIVATE
    val tags: List<String> = emptyList(),
    @SerialName("music_genres")
    val musicGenres: List<String> = emptyList(),
    @SerialName("max_attendees")
    val maxAttendees: Int? = null,
    @SerialName("attendees_count")
    val attendeesCount: Int = 0,
    @SerialName("media_count")
    val mediaCount: Int = 0,
    @SerialName("created_at")
    val createdAt: Long = 0L,
    @SerialName("updated_at")
    val updatedAt: Long? = null,
)

@Serializable
data class VenueDto(
    val name: String = "",
    val address: String? = null,
    val city: String? = null,
    val country: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @SerialName("place_id")
    val placeId: String? = null,
)

/**
 * Lightweight DTO for party event summaries in lists
 */
@Serializable
data class PartyEventSummaryDto(
    val id: String = "",
    val title: String = "",
    @SerialName("cover_image_url")
    val coverImageUrl: String? = null,
    @SerialName("starts_at")
    val startsAt: Long = 0L,
    val status: String = "PLANNED",
    @SerialName("venue_name")
    val venueName: String = "",
    @SerialName("attendees_count")
    val attendeesCount: Int = 0,
)

/**
 * DTO for party attendee information
 */
@Serializable
data class PartyAttendeeDto(
    @SerialName("user_id")
    val userId: String = "",
    val user: UserSummaryDto? = null,
    val status: String = "GOING", // GOING, MAYBE, NOT_GOING
    @SerialName("rsvp_at")
    val rsvpAt: Long = 0L,
)
