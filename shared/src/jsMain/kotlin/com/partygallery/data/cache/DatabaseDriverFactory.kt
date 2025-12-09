package com.partygallery.data.cache

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import com.partygallery.database.PartyGalleryDatabase
import org.w3c.dom.Worker

/**
 * JavaScript/Web implementation of DatabaseDriverFactory.
 * Uses WebWorkerDriver with sql.js for browser-based SQLite.
 *
 * S2.5-006: SQLDelight local cache setup
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return WebWorkerDriver(
            Worker(
                js("""new URL("sql.js/dist/sql-wasm.js", import.meta.url)""") as String,
            ),
        ).also { driver ->
            PartyGalleryDatabase.Schema.create(driver)
        }
    }
}
