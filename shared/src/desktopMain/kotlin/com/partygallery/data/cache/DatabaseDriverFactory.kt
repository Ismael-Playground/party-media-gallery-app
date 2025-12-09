package com.partygallery.data.cache

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.partygallery.database.PartyGalleryDatabase
import java.io.File

/**
 * Desktop (JVM) implementation of DatabaseDriverFactory.
 * Uses JdbcSqliteDriver for JDBC-based SQLite database access.
 *
 * S2.5-006: SQLDelight local cache setup
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val databasePath = getDatabasePath()
        val driver = JdbcSqliteDriver("jdbc:sqlite:$databasePath")

        // Create tables if they don't exist
        PartyGalleryDatabase.Schema.create(driver)

        return driver
    }

    private fun getDatabasePath(): String {
        val userHome = System.getProperty("user.home")
        val appDataDir = File(userHome, ".partygallery")
        if (!appDataDir.exists()) {
            appDataDir.mkdirs()
        }
        return File(appDataDir, DATABASE_NAME).absolutePath
    }
}
