package com.partygallery.di

import org.koin.dsl.module

/**
 * Desktop (JVM)-specific Koin module.
 *
 * Contains Desktop platform implementations:
 * - Desktop file system access
 * - Platform-specific networking
 * - Desktop notification system
 */
val desktopModule = module {
    // Desktop-specific implementations
    // Example:
    // single { DesktopFileManager() }
    // single { DesktopNotificationService() }
}

/**
 * Get the Desktop platform module for Koin initialization.
 */
actual fun getPlatformModule() = desktopModule
