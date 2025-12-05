package com.partygallery.di

import org.koin.dsl.module

/**
 * Android-specific Koin module.
 *
 * Contains Android platform implementations:
 * - Firebase services (Auth, Firestore, Storage)
 * - Android Context providers
 * - Platform-specific data sources
 */
val androidModule = module {
    // Android-specific implementations
    // Example:
    // single { AndroidFirebaseAuth() }
    // single { AndroidImagePicker() }
    // single { AndroidNotificationManager(get()) }
}

/**
 * Get the Android platform module for Koin initialization.
 */
actual fun getPlatformModule() = androidModule
