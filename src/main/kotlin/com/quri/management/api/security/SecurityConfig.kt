package com.quri.management.api.security

import com.quri.management.api.security.auth.ClerkAuthoritiesConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint

/**
 * Configures all Spring Security infrastructure for the WebFlux stack.
 *
 * Beans defined:
 *      - [reactiveJwtDecoder]      verifies JWT signature via Clerk's JWKS endpoint
 *      - [jwtAuthenticationConverter]  extracts roles from Clerk claims into Spring authorities
 *      - [securityWebFilterChain]  applies authentication to every request
 *
 * Request flow:
 * ```
 * Incoming request
 *   → SecurityWebFilterChain
 *   → NimbusReactiveJwtDecoder     verifies signature + expiry via JWKS
 *   → ClerkAuthoritiesConverter    maps metadata.role → ROLE_*
 *   → SecurityContext populated
 *   → Controller runs
 * ```
 *
 * Auth failure responses:
 *      - Missing or invalid token -> 401 Unauthorized
 */
@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val clerkAuthoritiesConverter: ClerkAuthoritiesConverter,
    @Value($$"${spring.security.oauth2.resourceserver.jwt.jwks-uri}")
    private val jwksUri: String,
) {

    /**
     * Verifies JWT signature using Clerk's public keys.
     * Keys are fetched on startup and refreshed automatically on rotation.
     * Ref: https://clerk.com/docs/guides/sessions/manual-jwt-verification#get-your-instances-public-key
     */
    @Bean
    fun reactiveJwtDecoder(): ReactiveJwtDecoder = NimbusReactiveJwtDecoder.withJwkSetUri(jwksUri).build()

    /**
     * Wires [ClerkAuthoritiesConverter] into Spring's JWT authentication pipeline.
     * Adapted to Reactor via [ReactiveJwtAuthenticationConverterAdapter].
     */
    @Bean
    fun jwtAuthenticationConverter(): ReactiveJwtAuthenticationConverterAdapter =
        JwtAuthenticationConverter()
            .apply { setJwtGrantedAuthoritiesConverter(clerkAuthoritiesConverter) }
            .let { ReactiveJwtAuthenticationConverterAdapter(it) }

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http
            .csrf { it.disable() }
            .authorizeExchange {
                it.anyExchange().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { it.jwtAuthenticationConverter(jwtAuthenticationConverter()) }
                oauth2.authenticationEntryPoint(UNAUTHORIZED_ENTRY_POINT)
            }
            .build()

    companion object {
        // Returns 401 JSON instead of redirecting to a login page.
        // REST clients expect status codes, not HTML redirect responses.
        private val UNAUTHORIZED_ENTRY_POINT =
            HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)
    }
}
