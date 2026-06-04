package com.quri.management.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension

/**
 * Global config for integration spec classes.
 *
 * SpringExtension requires you to activate, not active by default.
 * Without it Kotest has no knowledge of Spring, alternatively for each class
 * `@ApplyExtension(SpringExtension::class)` registers the extension.
 * Ref: https://kotest.io/docs/extensions/spring.html
 */
class IntegrationTestProjectConfig : AbstractProjectConfig() {
    override val extensions = listOf(SpringExtension())
}
