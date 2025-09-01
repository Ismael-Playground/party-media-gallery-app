package com.partygallery.di

import com.partygallery.domain.repository.*
import com.partygallery.data.repository.*
import org.koin.dsl.module

val repositoryModule = module {
    
    // User Repository
    single<UserRepository> {
        UserRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            mapper = get()
        )
    }
    
    // Contact Repository
    single<ContactRepository> {
        ContactRepositoryImpl(
            localDataSource = get(),
            remoteDataSource = get(),
            userLocalDataSource = get(),
            mapper = get()
        )
    }
    
    // Party Event Repository
    single<PartyEventRepository> {
        PartyEventRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            mapper = get()
        )
    }
    
    // Party Tag Repository
    single<PartyTagRepository> {
        PartyTagRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            mapper = get()
        )
    }
    
    // Media Repository
    single<MediaRepository> {
        MediaRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            mapper = get()
        )
    }
    
    // Chat Repository
    single<ChatRepository> {
        ChatRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            mapper = get()
        )
    }
    
    // Analytics Repository
    single<AnalyticsRepository> {
        AnalyticsRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            mapper = get()
        )
    }
    
    // Party Recommendation Repository
    single<PartyRecommendationRepository> {
        PartyRecommendationRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            userRepository = get(),
            mapper = get()
        )
    }
}