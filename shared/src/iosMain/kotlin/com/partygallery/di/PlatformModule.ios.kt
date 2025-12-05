package com.partygallery.di

import org.koin.dsl.module

/**
 * iOS-specific Koin module.
 *
 * Contains iOS platform implementations:
 * - iOS Firebase SDK bindings
 * - iOS Photos framework access
 * - iOS push notifications
 */
val iosModule = module {
    // iOS-specific implementations
    // Example:
    // single { IOSFirebaseAuth() }
    // single { IOSImagePicker() }
    // single { IOSNotificationManager() }
}

/**
 * Get the iOS platform module for Koin initialization.
 */
actual fun getPlatformModule() = iosModule
