package com.partygallery.data.repository

import com.partygallery.domain.repository.AnalyticsRepository
import com.partygallery.domain.model.analytics.PartyAnalytics
import com.partygallery.domain.model.analytics.UserEngagement
import com.partygallery.domain.model.analytics.ContentPerformance
import com.partygallery.data.datasource.remote.AnalyticsRemoteDataSource
import com.partygallery.data.datasource.local.AnalyticsLocalDataSource
import com.partygallery.data.mapper.AnalyticsMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class AnalyticsRepositoryImpl(
    private val remoteDataSource: AnalyticsRemoteDataSource,
    private val localDataSource: AnalyticsLocalDataSource,
    private val mapper: AnalyticsMapper
) : AnalyticsRepository {

    override suspend fun trackPartyEvent(eventName: String, properties: Map<String, Any>): Result<Unit> = runCatching {
        // Track locally first for offline support
        localDataSource.trackEvent(eventName, properties)
        
        // Send to remote analytics
        remoteDataSource.trackPartyEvent(eventName, properties).getOrThrow()
    }

    override suspend fun trackUserAction(userId: String, action: String, properties: Map<String, Any>): Result<Unit> = runCatching {
        localDataSource.trackUserAction(userId, action, properties)
        remoteDataSource.trackUserAction(userId, action, properties).getOrThrow()
    }

    override suspend fun trackPartyEngagement(partyEventId: String, userId: String, engagementType: String): Result<Unit> = runCatching {
        localDataSource.trackPartyEngagement(partyEventId, userId, engagementType)
        remoteDataSource.trackPartyEngagement(partyEventId, userId, engagementType).getOrThrow()
    }

    override suspend fun trackMediaInteraction(mediaId: String, userId: String, interactionType: String): Result<Unit> = runCatching {
        localDataSource.trackMediaInteraction(mediaId, userId, interactionType)
        remoteDataSource.trackMediaInteraction(mediaId, userId, interactionType).getOrThrow()
    }

    override suspend fun getPartyAnalytics(partyEventId: String): Result<PartyAnalytics> = runCatching {
        val remoteAnalytics = remoteDataSource.getPartyAnalytics(partyEventId).getOrThrow()
        mapper.toDomainPartyAnalytics(remoteAnalytics)
    }

    override suspend fun getUserEngagementAnalytics(userId: String, dateRange: Pair<Instant, Instant>): Result<UserEngagement> = runCatching {
        val remoteEngagement = remoteDataSource.getUserEngagementAnalytics(userId, dateRange).getOrThrow()
        mapper.toDomainUserEngagement(remoteEngagement)
    }

    override suspend fun getContentPerformanceAnalytics(mediaId: String): Result<ContentPerformance> = runCatching {
        val remotePerformance = remoteDataSource.getContentPerformanceAnalytics(mediaId).getOrThrow()
        mapper.toDomainContentPerformance(remotePerformance)
    }

    override suspend fun getPopularPartyTimes(): Result<Map<Int, Int>> = runCatching {
        remoteDataSource.getPopularPartyTimes().getOrThrow()
    }

    override suspend fun getPopularPartyLocations(limit: Int): Result<List<Pair<String, Int>>> = runCatching {
        remoteDataSource.getPopularPartyLocations(limit).getOrThrow()
    }

    override suspend fun getMostActiveUsers(limit: Int): Result<List<Pair<String, Int>>> = runCatching {
        remoteDataSource.getMostActiveUsers(limit).getOrThrow()
    }

    override suspend fun getPartyTrends(dateRange: Pair<Instant, Instant>): Result<Map<String, Int>> = runCatching {
        remoteDataSource.getPartyTrends(dateRange).getOrThrow()
    }

    override suspend fun getMediaTrends(dateRange: Pair<Instant, Instant>): Result<Map<String, Int>> = runCatching {
        remoteDataSource.getMediaTrends(dateRange).getOrThrow()
    }

    override suspend fun getPartyAttendanceStats(partyEventId: String): Result<Map<String, Int>> = runCatching {
        remoteDataSource.getPartyAttendanceStats(partyEventId).getOrThrow()
    }

    override suspend fun getPartyEngagementRate(partyEventId: String): Result<Double> = runCatching {
        remoteDataSource.getPartyEngagementRate(partyEventId).getOrThrow()
    }

    override suspend fun getAveragePartyDuration(): Result<Double> = runCatching {
        remoteDataSource.getAveragePartyDuration().getOrThrow()
    }

    override suspend fun trackAppSession(userId: String, sessionDuration: Long): Result<Unit> = runCatching {
        localDataSource.trackAppSession(userId, sessionDuration)
        remoteDataSource.trackAppSession(userId, sessionDuration).getOrThrow()
    }

    override suspend fun trackFeatureUsage(userId: String, feature: String, duration: Long): Result<Unit> = runCatching {
        localDataSource.trackFeatureUsage(userId, feature, duration)
        remoteDataSource.trackFeatureUsage(userId, feature, duration).getOrThrow()
    }

    override fun observePartyAnalytics(partyEventId: String): Flow<PartyAnalytics> {
        return remoteDataSource.observePartyAnalytics(partyEventId).map { remoteAnalytics ->
            mapper.toDomainPartyAnalytics(remoteAnalytics)
        }
    }

    override fun observeUserEngagement(userId: String): Flow<UserEngagement> {
        return remoteDataSource.observeUserEngagement(userId).map { remoteEngagement ->
            mapper.toDomainUserEngagement(remoteEngagement)
        }
    }

    override fun observeTrendingContent(): Flow<List<ContentPerformance>> {
        return remoteDataSource.observeTrendingContent().map { remoteContent ->
            remoteContent.map { mapper.toDomainContentPerformance(it) }
        }
    }

    override suspend fun generateAnalyticsReport(
        userId: String,
        dateRange: Pair<Instant, Instant>,
        reportType: String
    ): Result<Map<String, Any>> = runCatching {
        remoteDataSource.generateAnalyticsReport(userId, dateRange, reportType).getOrThrow()
    }
}