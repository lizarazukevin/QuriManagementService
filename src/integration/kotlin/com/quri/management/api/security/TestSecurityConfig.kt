package com.quri.management.api.security

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

/**
 * Replaces the SecurityConfig in Webflux slice tests.
 * Disables JWT authentication so handler tests focus on HTTP contract.
 */
@Profile("integration")
@TestConfiguration
@EnableWebFluxSecurity
class TestSecurityConfig {

    @Bean
    fun testSecurityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http
            .csrf { it.disable() }
            .authorizeExchange { it.anyExchange().permitAll() }
            .build()
}
