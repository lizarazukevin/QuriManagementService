package com.quri.management.config

import io.kotest.core.spec.style.DescribeSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.MongoDBContainer

/**
 * Base class for all integration tests, boots the Spring context
 * and provides a shared MongoDB container via @ServiceConnection
 */
@SpringBootTest
@ActiveProfiles("integration")
abstract class IntegrationTest : DescribeSpec() {
    companion object {
        // Starts MongoDB container before Spring context initializes.
        // @ServiceConnection auto-wires the URI into Spring properties,
        // replacing manual @DynamicPropertySource configuration and single resource
        @ServiceConnection
        val mongo = MongoDBContainer("mongo:latest").apply {
            start()
        }
    }
}
