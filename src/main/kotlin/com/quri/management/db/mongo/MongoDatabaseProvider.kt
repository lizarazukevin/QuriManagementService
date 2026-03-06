package com.quri.management.db.mongo

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.quri.management.db.mongo.MongoSchema.Databases.DATA_STORE
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Provides MongoDB database instances as Spring beans.
 *
 * Add a new [Bean] here for each additional database the application needs.
 */
@Configuration
class MongoDatabaseProvider(
    private val mongoClient: MongoClient
) {
    @Bean
    fun dataStoreDatabase(): MongoDatabase = mongoClient.getDatabase(DATA_STORE)
}