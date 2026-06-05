package com.quri.management.clients

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import org.bson.codecs.configuration.CodecRegistry
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.testcontainers.containers.MongoDBContainer

/**
 * Shared MongoDB container for all integration tests.
 * Started before Spring context initializes so integration
 * test can reference the connection URL during bean creation.
 */
private val mongoContainer = MongoDBContainer("mongo:latest").apply { start() }

@TestConfiguration
@Profile("integration")
class MongoClientProviderTest(private val customCodecRegistry: CodecRegistry) {

    /**
     * Replaces [MongoClientProvider] in the integration profile.
     * Wires the test container connection into the Spring context
     * using the same codec registry as production.
     */
    @Bean
    @Primary
    fun mongoClient(): MongoClient {
        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(mongoContainer.replicaSetUrl))
            .codecRegistry(customCodecRegistry)
            .build()
        return MongoClient.create(settings)
    }
}
