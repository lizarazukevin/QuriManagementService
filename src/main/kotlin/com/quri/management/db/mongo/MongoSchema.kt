package com.quri.management.db.mongo

/**
 * Single source of truth for MongoDB database and collection name constants.
 *
 * Always reference these constants instead of raw strings to avoid
 * typos and make renaming straightforward.
 */
object MongoSchema {
    object Databases {
        const val DATA_STORE = "dataStore"
    }

    object Collections {
        const val BILLS = "bills"
        const val PROFILES = "profiles"
    }
}