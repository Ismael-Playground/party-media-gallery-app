package com.partygallery.di

import org.koin.core.module.Module

/**
 * Expect declaration for platform-specific Koin module.
 *
 * Each platform (Android, iOS, Desktop, JS) provides its own implementation
 * via actual declarations containing platform-specific dependencies.
 */
expect fun getPlatformModule(): Module
