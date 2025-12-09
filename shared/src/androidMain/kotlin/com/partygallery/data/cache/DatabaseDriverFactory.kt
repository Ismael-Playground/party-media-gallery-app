package com.partygallery.data.cache

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.partygallery.database.PartyGalleryDatabase

/**
 * Android implementation of DatabaseDriverFactory.
 * Uses AndroidSqliteDriver for SQLite database access.
 *
 * S2.5-006: SQLDelight local cache setup
 */
actual class DatabaseDriverFactory(
    private val context: Context,
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = PartyGalleryDatabase.Schema,
            context = context,
            name = DATABASE_NAME,
        )
    }
}
