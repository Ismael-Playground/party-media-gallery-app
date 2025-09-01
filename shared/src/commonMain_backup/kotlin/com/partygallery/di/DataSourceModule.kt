package com.partygallery.di

import com.partygallery.data.datasource.local.*
import com.partygallery.data.datasource.remote.*
import org.koin.dsl.module

val dataSourceModule = module {
    
    // Local Data Sources
    single<UserLocalDataSource> { UserLocalDataSourceImpl(database = get()) }
    single<ContactLocalDataSource> { ContactLocalDataSourceImpl(database = get()) }
    single<PartyEventLocalDataSource> { PartyEventLocalDataSourceImpl(database = get()) }
    single<PartyTagLocalDataSource> { PartyTagLocalDataSourceImpl(database = get()) }
    single<MediaLocalDataSource> { MediaLocalDataSourceImpl(database = get()) }
    single<ChatLocalDataSource> { ChatLocalDataSourceImpl(database = get()) }
    single<AnalyticsLocalDataSource> { AnalyticsLocalDataSourceImpl(database = get()) }
    
    // Remote Data Sources - Firebase implementations
    single<UserRemoteDataSource> { FirebaseUserDataSource(firestore = get(), auth = get()) }
    single<ContactRemoteDataSource> { FirebaseContactDataSource(firestore = get()) }
    single<PartyEventRemoteDataSource> { FirebasePartyEventDataSource(firestore = get()) }
    single<PartyTagRemoteDataSource> { FirebasePartyTagDataSource(firestore = get()) }
    single<MediaRemoteDataSource> { FirebaseMediaDataSource(firestore = get(), storage = get()) }
    single<ChatRemoteDataSource> { FirebaseChatDataSource(firestore = get()) }
    single<AnalyticsRemoteDataSource> { FirebaseAnalyticsDataSource(analytics = get()) }
    single<PartyRecommendationRemoteDataSource> { FirebaseRecommendationDataSource(firestore = get()) }
    
    // Social Media Data Sources
    single<InstagramDataSource> { InstagramDataSourceImpl(apiClient = get()) }
    single<TikTokDataSource> { TikTokDataSourceImpl(apiClient = get()) }
    single<TwitterDataSource> { TwitterDataSourceImpl(apiClient = get()) }
    single<FacebookDataSource> { FacebookDataSourceImpl(apiClient = get()) }
    single<PinterestDataSource> { PinterestDataSourceImpl(apiClient = get()) }
    single<SpotifyDataSource> { SpotifyDataSourceImpl(apiClient = get()) }
}