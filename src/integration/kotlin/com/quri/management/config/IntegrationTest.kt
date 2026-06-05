package com.quri.management.config

import com.quri.management.clients.MongoClientProviderTest
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

/**
 * Base class for all integration tests, boots the Spring context
 * Boots the full Spring context against a real MongoDB instance.
 */
@SpringBootTest
@ActiveProfiles("integration")
@ApplyExtension(SpringExtension::class)
@Import(MongoClientProviderTest::class)
abstract class IntegrationTest : DescribeSpec() {
    override val extensions = listOf(SpringExtension())
}
