package com.partygallery.di

import org.koin.dsl.module

/**
 * JavaScript/Web-specific Koin module.
 *
 * Contains Web platform implementations:
 * - Browser storage (IndexedDB, localStorage)
 * - Web File API
 * - Web push notifications
 */
val jsModule = module {
    // JS/Web-specific implementations
    // Example:
    // single { WebStorageManager() }
    // single { WebNotificationService() }
}

/**
 * Get the JS platform module for Koin initialization.
 */
actual fun getPlatformModule() = jsModule
