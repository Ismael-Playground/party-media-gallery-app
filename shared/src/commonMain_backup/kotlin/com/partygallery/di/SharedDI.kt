package com.partygallery.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        // Core modules
        databaseModule,
        networkModule,
        mapperModule,
        
        // Data layer
        dataSourceModule,
        repositoryModule,
        
        // Domain layer  
        useCaseModule,
        validationModule,
        
        // Presentation layer
        storeModule,
        
        // Platform-specific modules will be added by each platform
    )
}

// Platform-specific initialization
expect fun platformModule(): org.koin.core.module.Module