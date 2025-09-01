package com.partygallery.di

import org.koin.dsl.module

val databaseModule = module {
    
    // SQLDelight Database
    single { createDatabase(get()) } // Platform-specific database creation
    
    // Database DAOs/Queries
    single { get<PartyGalleryDatabase>().userQueries }
    single { get<PartyGalleryDatabase>().contactQueries }
    single { get<PartyGalleryDatabase>().partyEventQueries }
    single { get<PartyGalleryDatabase>().partyTagQueries }
    single { get<PartyGalleryDatabase>().mediaQueries }
    single { get<PartyGalleryDatabase>().chatQueries }
    single { get<PartyGalleryDatabase>().messageQueries }
    single { get<PartyGalleryDatabase>().analyticsQueries }
}

// Platform-specific database creation function
// Will be implemented in each platform's source set
expect fun createDatabase(driverFactory: DatabaseDriverFactory): PartyGalleryDatabase

// Platform-specific database driver factory
expect class DatabaseDriverFactory

// Placeholder for SQLDelight generated database
// Will be generated when .sq files are created
expect class PartyGalleryDatabase