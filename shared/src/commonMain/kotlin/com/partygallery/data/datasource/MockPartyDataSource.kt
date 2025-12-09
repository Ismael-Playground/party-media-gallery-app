package com.partygallery.data.datasource

import com.partygallery.data.dto.PartyAttendeeDto
import com.partygallery.data.dto.PartyEventDto
import com.partygallery.data.dto.UserSummaryDto
import com.partygallery.data.dto.VenueDto
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

/**
 * Mock implementation of PartyDataSource for development and testing.
 * Will be replaced with FirebasePartyDataSource in production.
 *
 * S2.5-002: PartyRepositoryImpl con Firebase
 */
class MockPartyDataSource : PartyDataSource {

    // In-memory storage
    private val parties = MutableStateFlow<Map<String, PartyEventDto>>(createInitialParties())
    private val attendees = MutableStateFlow<Map<String, List<PartyAttendeeDto>>>(createInitialAttendees())
    private val rsvpStatuses = MutableStateFlow<Map<String, String>>(emptyMap()) // "partyId_userId" -> status

    // Simulate network delay
    private suspend fun simulateNetworkDelay() {
        delay(300)
    }

    // ============================================
    // CRUD Operations
    // ============================================

    override suspend fun createParty(party: PartyEventDto): PartyEventDto {
        simulateNetworkDelay()
        val now = Clock.System.now().toEpochMilliseconds()
        val newParty = party.copy(
            id = "party_${System.currentTimeMillis()}",
            createdAt = now,
            updatedAt = now,
        )
        parties.value = parties.value + (newParty.id to newParty)
        return newParty
    }

    override suspend fun getPartyById(partyId: String): PartyEventDto? {
        simulateNetworkDelay()
        return parties.value[partyId]
    }

    override suspend fun updateParty(party: PartyEventDto): PartyEventDto {
        simulateNetworkDelay()
        val updatedParty = party.copy(
            updatedAt = Clock.System.now().toEpochMilliseconds(),
        )
        parties.value = parties.value + (party.id to updatedParty)
        return updatedParty
    }

    override suspend fun deleteParty(partyId: String) {
        simulateNetworkDelay()
        parties.value = parties.value - partyId
    }

    // ============================================
    // Query Operations
    // ============================================

    override suspend fun getPartiesByHost(hostId: String, limit: Int): List<PartyEventDto> {
        simulateNetworkDelay()
        return parties.value.values
            .filter { it.hostId == hostId }
            .sortedByDescending { it.startsAt }
            .take(limit)
    }

    override suspend fun getUpcomingParties(limit: Int): List<PartyEventDto> {
        simulateNetworkDelay()
        val now = Clock.System.now().toEpochMilliseconds()
        return parties.value.values
            .filter { it.status == "PLANNED" && it.startsAt > now }
            .sortedBy { it.startsAt }
            .take(limit)
    }

    override suspend fun getLiveParties(limit: Int): List<PartyEventDto> {
        simulateNetworkDelay()
        return parties.value.values
            .filter { it.status == "LIVE" }
            .sortedByDescending { it.attendeesCount }
            .take(limit)
    }

    override suspend fun getPastParties(userId: String, limit: Int): List<PartyEventDto> {
        simulateNetworkDelay()
        return parties.value.values
            .filter { it.status == "ENDED" }
            .sortedByDescending { it.endsAt ?: it.startsAt }
            .take(limit)
    }

    override suspend fun searchParties(query: String, limit: Int): List<PartyEventDto> {
        simulateNetworkDelay()
        val lowercaseQuery = query.lowercase()
        return parties.value.values
            .filter { party ->
                party.title.lowercase().contains(lowercaseQuery) ||
                    party.description?.lowercase()?.contains(lowercaseQuery) == true ||
                    party.venue.name.lowercase().contains(lowercaseQuery) ||
                    party.tags.any { it.lowercase().contains(lowercaseQuery) }
            }
            .take(limit)
    }

    override suspend fun getPartiesByTags(tags: List<String>, limit: Int): List<PartyEventDto> {
        simulateNetworkDelay()
        val lowercaseTags = tags.map { it.lowercase() }
        return parties.value.values
            .filter { party ->
                party.tags.any { tag -> lowercaseTags.any { it == tag.lowercase() } }
            }
            .sortedByDescending { it.startsAt }
            .take(limit)
    }

    override suspend fun getNearbyParties(
        latitude: Double,
        longitude: Double,
        radiusKm: Double,
        limit: Int,
    ): List<PartyEventDto> {
        simulateNetworkDelay()
        return parties.value.values
            .filter { party ->
                val venueLat = party.venue.latitude
                val venueLon = party.venue.longitude
                if (venueLat != null && venueLon != null) {
                    calculateDistanceKm(latitude, longitude, venueLat, venueLon) <= radiusKm
                } else {
                    false
                }
            }
            .sortedBy { party ->
                val venueLat = party.venue.latitude ?: return@sortedBy Double.MAX_VALUE
                val venueLon = party.venue.longitude ?: return@sortedBy Double.MAX_VALUE
                calculateDistanceKm(latitude, longitude, venueLat, venueLon)
            }
            .take(limit)
    }

    // ============================================
    // Status Management
    // ============================================

    override suspend fun updatePartyStatus(partyId: String, status: String) {
        simulateNetworkDelay()
        val party = parties.value[partyId] ?: return
        val updatedParty = party.copy(
            status = status,
            updatedAt = Clock.System.now().toEpochMilliseconds(),
        )
        parties.value = parties.value + (partyId to updatedParty)
    }

    // ============================================
    // Attendee Management
    // ============================================

    override suspend fun getAttendees(partyId: String): List<PartyAttendeeDto> {
        simulateNetworkDelay()
        return attendees.value[partyId] ?: emptyList()
    }

    override suspend fun getAttendeeCount(partyId: String): Int {
        simulateNetworkDelay()
        return parties.value[partyId]?.attendeesCount ?: 0
    }

    override suspend fun rsvp(partyId: String, userId: String, status: String) {
        simulateNetworkDelay()
        val key = "${partyId}_$userId"
        rsvpStatuses.value = rsvpStatuses.value + (key to status)

        // Update attendee count
        val party = parties.value[partyId] ?: return
        val currentAttendees = attendees.value[partyId]?.toMutableList() ?: mutableListOf()

        val existingIndex = currentAttendees.indexOfFirst { it.userId == userId }
        val newAttendee = PartyAttendeeDto(
            userId = userId,
            user = UserSummaryDto(
                id = userId,
                username = "user_$userId",
                firstName = "User",
                lastName = userId,
            ),
            status = status,
            rsvpAt = Clock.System.now().toEpochMilliseconds(),
        )

        if (existingIndex >= 0) {
            currentAttendees[existingIndex] = newAttendee
        } else {
            currentAttendees.add(newAttendee)
        }

        attendees.value = attendees.value + (partyId to currentAttendees)

        // Update party attendee count
        val goingCount = currentAttendees.count { it.status == "GOING" }
        val updatedParty = party.copy(attendeesCount = goingCount)
        parties.value = parties.value + (partyId to updatedParty)
    }

    override suspend fun checkIn(partyId: String, userId: String) {
        simulateNetworkDelay()
        rsvp(partyId, userId, "CHECKED_IN")
    }

    override suspend fun getUserRsvpStatus(partyId: String, userId: String): String? {
        simulateNetworkDelay()
        val key = "${partyId}_$userId"
        return rsvpStatuses.value[key]
    }

    // ============================================
    // Co-hosts
    // ============================================

    override suspend fun addCoHost(partyId: String, userId: String) {
        simulateNetworkDelay()
        val party = parties.value[partyId] ?: return
        val coHostDto = UserSummaryDto(
            id = userId,
            username = "cohost_$userId",
            firstName = "Co-Host",
            lastName = userId,
        )
        val updatedParty = party.copy(
            coHosts = party.coHosts + coHostDto,
            updatedAt = Clock.System.now().toEpochMilliseconds(),
        )
        parties.value = parties.value + (partyId to updatedParty)
    }

    override suspend fun removeCoHost(partyId: String, userId: String) {
        simulateNetworkDelay()
        val party = parties.value[partyId] ?: return
        val updatedParty = party.copy(
            coHosts = party.coHosts.filter { it.id != userId },
            updatedAt = Clock.System.now().toEpochMilliseconds(),
        )
        parties.value = parties.value + (partyId to updatedParty)
    }

    override suspend fun getCoHosts(partyId: String): List<UserSummaryDto> {
        simulateNetworkDelay()
        return parties.value[partyId]?.coHosts ?: emptyList()
    }

    // ============================================
    // Real-time Observers
    // ============================================

    override fun observeParty(partyId: String): Flow<PartyEventDto?> {
        return parties.map { it[partyId] }
    }

    override fun observeLiveParties(): Flow<List<PartyEventDto>> {
        return parties.map { partiesMap ->
            partiesMap.values.filter { it.status == "LIVE" }
        }
    }

    override fun observeAttendeeCount(partyId: String): Flow<Int> {
        return parties.map { it[partyId]?.attendeesCount ?: 0 }
    }

    override fun observeUserRsvpStatus(partyId: String, userId: String): Flow<String?> {
        val key = "${partyId}_$userId"
        return rsvpStatuses.map { it[key] }
    }

    // ============================================
    // Helper Functions
    // ============================================

    private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * acos(1 - 2 * a)
        return earthRadius * c
    }

    // ============================================
    // Mock Data
    // ============================================

    private fun createInitialParties(): Map<String, PartyEventDto> {
        val now = Clock.System.now().toEpochMilliseconds()
        val hourInMillis = 60 * 60 * 1000L

        val party1 = PartyEventDto(
            id = "party1",
            hostId = "user1",
            host = UserSummaryDto(
                id = "user1",
                username = "partyking",
                firstName = "Test",
                lastName = "User",
                avatarUrl = "https://i.pravatar.cc/150?u=user1",
                isVerified = true,
            ),
            title = "Summer Rooftop Bash",
            description = "Join us for the ultimate summer party with amazing views and great music!",
            venue = VenueDto(
                name = "Sky Lounge",
                address = "123 Party Street",
                city = "Miami",
                country = "USA",
                latitude = 25.7617,
                longitude = -80.1918,
            ),
            coverImageUrl = "https://picsum.photos/seed/party1/800/400",
            startsAt = now + (2 * hourInMillis),
            endsAt = now + (8 * hourInMillis),
            status = "PLANNED",
            privacy = "PUBLIC",
            tags = listOf("Summer", "Rooftop", "Electronic"),
            musicGenres = listOf("House", "Techno", "EDM"),
            maxAttendees = 200,
            attendeesCount = 87,
            mediaCount = 0,
            createdAt = now - (7 * 24 * hourInMillis),
        )

        val party2 = PartyEventDto(
            id = "party2",
            hostId = "user2",
            host = UserSummaryDto(
                id = "user2",
                username = "djmaster",
                firstName = "DJ",
                lastName = "Master",
                avatarUrl = "https://i.pravatar.cc/150?u=user2",
            ),
            title = "Underground Techno Night",
            description = "Deep beats, dark vibes. The best techno experience in the city.",
            venue = VenueDto(
                name = "The Basement",
                address = "456 Underground Ave",
                city = "Berlin",
                country = "Germany",
                latitude = 52.5200,
                longitude = 13.4050,
            ),
            coverImageUrl = "https://picsum.photos/seed/party2/800/400",
            startsAt = now - hourInMillis,
            status = "LIVE",
            privacy = "PUBLIC",
            tags = listOf("Techno", "Underground", "Nightlife"),
            musicGenres = listOf("Techno", "Industrial"),
            attendeesCount = 156,
            mediaCount = 23,
            createdAt = now - (14 * 24 * hourInMillis),
        )

        val party3 = PartyEventDto(
            id = "party3",
            hostId = "user3",
            host = UserSummaryDto(
                id = "user3",
                username = "clubqueen",
                firstName = "Club",
                lastName = "Queen",
                avatarUrl = "https://i.pravatar.cc/150?u=user3",
            ),
            title = "R&B Vibes Only",
            description = "Smooth R&B classics and new hits. Dress code: Stylish",
            venue = VenueDto(
                name = "Velvet Lounge",
                address = "789 VIP Lane",
                city = "Los Angeles",
                country = "USA",
                latitude = 34.0522,
                longitude = -118.2437,
            ),
            coverImageUrl = "https://picsum.photos/seed/party3/800/400",
            startsAt = now + (24 * hourInMillis),
            status = "PLANNED",
            privacy = "FRIENDS_ONLY",
            tags = listOf("R&B", "VIP", "Lounge"),
            musicGenres = listOf("R&B", "Hip Hop", "Soul"),
            maxAttendees = 100,
            attendeesCount = 45,
            mediaCount = 0,
            createdAt = now - (3 * 24 * hourInMillis),
        )

        val party4 = PartyEventDto(
            id = "party4",
            hostId = "user1",
            host = UserSummaryDto(
                id = "user1",
                username = "partyking",
                firstName = "Test",
                lastName = "User",
                avatarUrl = "https://i.pravatar.cc/150?u=user1",
                isVerified = true,
            ),
            title = "New Year's Eve 2024",
            description = "Ring in the new year with style! Champagne included.",
            venue = VenueDto(
                name = "Grand Ballroom",
                address = "1 Celebration Plaza",
                city = "New York",
                country = "USA",
                latitude = 40.7128,
                longitude = -74.0060,
            ),
            coverImageUrl = "https://picsum.photos/seed/party4/800/400",
            startsAt = now - (30 * 24 * hourInMillis),
            endsAt = now - (29 * 24 * hourInMillis),
            status = "ENDED",
            privacy = "PUBLIC",
            tags = listOf("NYE", "Celebration", "Premium"),
            musicGenres = listOf("Top 40", "Dance", "Pop"),
            maxAttendees = 500,
            attendeesCount = 487,
            mediaCount = 312,
            createdAt = now - (60 * 24 * hourInMillis),
        )

        return mapOf(
            party1.id to party1,
            party2.id to party2,
            party3.id to party3,
            party4.id to party4,
        )
    }

    private fun createInitialAttendees(): Map<String, List<PartyAttendeeDto>> {
        val now = Clock.System.now().toEpochMilliseconds()

        return mapOf(
            "party1" to listOf(
                PartyAttendeeDto(
                    userId = "user2",
                    user = UserSummaryDto(id = "user2", username = "djmaster", firstName = "DJ", lastName = "Master"),
                    status = "GOING",
                    rsvpAt = now - (2 * 24 * 60 * 60 * 1000L),
                ),
                PartyAttendeeDto(
                    userId = "user3",
                    user = UserSummaryDto(id = "user3", username = "clubqueen", firstName = "Club", lastName = "Queen"),
                    status = "MAYBE",
                    rsvpAt = now - (1 * 24 * 60 * 60 * 1000L),
                ),
            ),
            "party2" to listOf(
                PartyAttendeeDto(
                    userId = "user1",
                    user = UserSummaryDto(id = "user1", username = "partyking", firstName = "Test", lastName = "User"),
                    status = "GOING",
                    rsvpAt = now - (3 * 24 * 60 * 60 * 1000L),
                ),
            ),
        )
    }
}
