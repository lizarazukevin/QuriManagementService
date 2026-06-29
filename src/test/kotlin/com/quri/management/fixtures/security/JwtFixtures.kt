package com.quri.management.fixtures.security

import org.springframework.security.oauth2.jwt.Jwt
import java.time.Instant

object JwtFixtures {

    const val DEFAULT_TOKEN_VALUE = "token"
    const val DEFAULT_ALG = "none"
    const val DEFAULT_JWT_SUBJECT = "user-1"
    val DEFAULT_ISSUED_AT: Instant = Instant.parse("2024-01-01T00:00:00Z")
    val DEFAULT_EXPIRES_AT: Instant = DEFAULT_ISSUED_AT.plusSeconds(3600)

    fun aJwt(
        subject: String = DEFAULT_JWT_SUBJECT,
        tokenValue: String = DEFAULT_TOKEN_VALUE,
        issuedAt: Instant = DEFAULT_ISSUED_AT,
        expiresAt: Instant = DEFAULT_EXPIRES_AT,
    ): Jwt = Jwt.withTokenValue(tokenValue)
        .header("alg", DEFAULT_ALG)
        .subject(subject)
        .claim("sub", subject)
        .issuedAt(issuedAt)
        .expiresAt(expiresAt)
        .build()

    fun aJwtWithNoSubject(
        tokenValue: String = DEFAULT_TOKEN_VALUE,
        issuedAt: Instant = DEFAULT_ISSUED_AT,
        expiresAt: Instant = DEFAULT_EXPIRES_AT,
    ): Jwt = Jwt(
        tokenValue,
        issuedAt,
        expiresAt,
        mapOf("alg" to DEFAULT_ALG),
        mapOf("aud" to listOf("test")),
    )
}
