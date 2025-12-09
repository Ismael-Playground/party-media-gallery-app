package com.partygallery.data.mapper

import com.partygallery.data.dto.PartyAttendeeDto
import com.partygallery.data.dto.PartyEventDto
import com.partygallery.data.dto.PartyEventSummaryDto
import com.partygallery.data.dto.VenueDto
import com.partygallery.domain.model.PartyEvent
import com.partygallery.domain.model.PartyEventSummary
import com.partygallery.domain.model.PartyPrivacy
import com.partygallery.domain.model.PartyStatus
import com.partygallery.domain.model.Venue
import kotlinx.datetime.Instant

/**
 * Mapper extensions for PartyEvent DTO <-> Domain conversions.
 *
 * S2.5-007: Data mappers (DTO to Domain)
 */

// DTO -> Domain

fun PartyEventDto.toDomain(): PartyEvent = PartyEvent(
    id = id,
    hostId = hostId,
    host = host?.toDomain(),
    coHosts = coHosts.map { it.toDomain() },
    title = title,
    description = description,
    venue = venue.toDomain(),
    coverImageUrl = coverImageUrl,
    startsAt = Instant.fromEpochMilliseconds(startsAt),
    endsAt = endsAt?.let { Instant.fromEpochMilliseconds(it) },
    status = parsePartyStatus(status),
    privacy = parsePartyPrivacy(privacy),
    tags = tags,
    musicGenres = musicGenres,
    maxAttendees = maxAttendees,
    attendeesCount = attendeesCount,
    mediaCount = mediaCount,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = updatedAt?.let { Instant.fromEpochMilliseconds(it) },
)

fun VenueDto.toDomain(): Venue = Venue(
    name = name,
    address = address,
    city = city,
    country = country,
    latitude = latitude,
    longitude = longitude,
    placeId = placeId,
)

fun PartyEventSummaryDto.toDomain(): PartyEventSummary = PartyEventSummary(
    id = id,
    title = title,
    venueName = venueName,
    coverImageUrl = coverImageUrl,
    startsAt = Instant.fromEpochMilliseconds(startsAt),
    status = parsePartyStatus(status),
    attendeesCount = attendeesCount,
    isLive = parsePartyStatus(status) == PartyStatus.LIVE,
)

// Domain -> DTO

fun PartyEvent.toDto(): PartyEventDto = PartyEventDto(
    id = id,
    hostId = hostId,
    host = host?.toDto(),
    coHosts = coHosts.map { it.toDto() },
    title = title,
    description = description,
    venue = venue.toDto(),
    coverImageUrl = coverImageUrl,
    startsAt = startsAt.toEpochMilliseconds(),
    endsAt = endsAt?.toEpochMilliseconds(),
    status = status.name,
    privacy = privacy.name,
    tags = tags,
    musicGenres = musicGenres,
    maxAttendees = maxAttendees,
    attendeesCount = attendeesCount,
    mediaCount = mediaCount,
    createdAt = createdAt.toEpochMilliseconds(),
    updatedAt = updatedAt?.toEpochMilliseconds(),
)

fun Venue.toDto(): VenueDto = VenueDto(
    name = name,
    address = address,
    city = city,
    country = country,
    latitude = latitude,
    longitude = longitude,
    placeId = placeId,
)

fun PartyEventSummary.toDto(): PartyEventSummaryDto = PartyEventSummaryDto(
    id = id,
    title = title,
    coverImageUrl = coverImageUrl,
    startsAt = startsAt.toEpochMilliseconds(),
    status = status.name,
    venueName = venueName,
    attendeesCount = attendeesCount,
)

// Helper functions

private fun parsePartyStatus(status: String): PartyStatus {
    return try {
        PartyStatus.valueOf(status)
    } catch (e: Exception) {
        PartyStatus.PLANNED
    }
}

private fun parsePartyPrivacy(privacy: String): PartyPrivacy {
    return try {
        PartyPrivacy.valueOf(privacy)
    } catch (e: Exception) {
        PartyPrivacy.PUBLIC
    }
}
