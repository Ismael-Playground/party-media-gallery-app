package com.partygallery.data.cache

import app.cash.sqldelight.db.SqlDriver

/**
 * Factory for creating SQLDelight SqlDriver instances.
 * Platform-specific implementations provide the actual driver.
 *
 * S2.5-006: SQLDelight local cache setup
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

/**
 * Database name constant used across all platforms.
 */
const val DATABASE_NAME = "party_gallery.db"
