package com.partygallery.data.cache

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.partygallery.database.PartyGalleryDatabase

/**
 * iOS implementation of DatabaseDriverFactory.
 * Uses NativeSqliteDriver for native SQLite database access.
 *
 * S2.5-006: SQLDelight local cache setup
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = PartyGalleryDatabase.Schema,
            name = DATABASE_NAME,
        )
    }
}
