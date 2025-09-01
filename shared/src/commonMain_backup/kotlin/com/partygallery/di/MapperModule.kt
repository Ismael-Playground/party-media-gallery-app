package com.partygallery.di

import com.partygallery.data.mapper.*
import org.koin.dsl.module

val mapperModule = module {
    
    // Domain to Data mappers
    single { UserMapper() }
    single { ContactMapper() }
    single { PartyEventMapper() }
    single { PartyTagMapper() }
    single { MediaMapper() }
    single { ChatMapper() }
    single { AnalyticsMapper() }
    single { RecommendationMapper() }
    
    // Platform-specific mappers (if needed)
    single { FirebaseMapper() }
    single { SQLDelightMapper() }
}