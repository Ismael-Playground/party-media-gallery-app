package com.partygallery.domain.repository

import com.partygallery.domain.model.analytics.PartyAnalytics
import com.partygallery.domain.model.analytics.UserEngagement
import com.partygallery.domain.model.analytics.ContentPerformance
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface AnalyticsRepository {
    suspend fun trackPartyEvent(eventName: String, properties: Map<String, Any>): Result<Unit>
    suspend fun trackUserAction(userId: String, action: String, properties: Map<String, Any>): Result<Unit>
    suspend fun trackPartyEngagement(partyEventId: String, userId: String, engagementType: String): Result<Unit>
    suspend fun trackMediaInteraction(mediaId: String, userId: String, interactionType: String): Result<Unit>
    
    suspend fun getPartyAnalytics(partyEventId: String): Result<PartyAnalytics>
    suspend fun getUserEngagementAnalytics(userId: String, dateRange: Pair<Instant, Instant>): Result<UserEngagement>
    suspend fun getContentPerformanceAnalytics(mediaId: String): Result<ContentPerformance>
    
    suspend fun getPopularPartyTimes(): Result<Map<Int, Int>> // hour -> count
    suspend fun getPopularPartyLocations(limit: Int = 10): Result<List<Pair<String, Int>>> // location -> count
    suspend fun getMostActiveUsers(limit: Int = 10): Result<List<Pair<String, Int>>> // userId -> activity score
    
    suspend fun getPartyTrends(dateRange: Pair<Instant, Instant>): Result<Map<String, Int>>
    suspend fun getMediaTrends(dateRange: Pair<Instant, Instant>): Result<Map<String, Int>>
    
    suspend fun getPartyAttendanceStats(partyEventId: String): Result<Map<String, Int>>
    suspend fun getPartyEngagementRate(partyEventId: String): Result<Double>
    suspend fun getAveragePartyDuration(): Result<Double> // in hours
    
    suspend fun trackAppSession(userId: String, sessionDuration: Long): Result<Unit>
    suspend fun trackFeatureUsage(userId: String, feature: String, duration: Long): Result<Unit>
    
    fun observePartyAnalytics(partyEventId: String): Flow<PartyAnalytics>
    fun observeUserEngagement(userId: String): Flow<UserEngagement>
    fun observeTrendingContent(): Flow<List<ContentPerformance>>
    
    suspend fun generateAnalyticsReport(
        userId: String,
        dateRange: Pair<Instant, Instant>,
        reportType: String
    ): Result<Map<String, Any>>
}