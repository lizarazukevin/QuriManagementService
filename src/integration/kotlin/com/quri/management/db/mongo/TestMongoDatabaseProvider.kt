package com.quri.management.db.mongo

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

/**
 * Isolated database name prevents bleed between test runs and environments
 */
@Profile("integration")
@TestConfiguration
class TestMongoDatabaseProvider {
    @Bean
    @Primary
    fun mongoDatabase(mongoClient: MongoClient): MongoDatabase = mongoClient.getDatabase("quri-integration-test")
}
