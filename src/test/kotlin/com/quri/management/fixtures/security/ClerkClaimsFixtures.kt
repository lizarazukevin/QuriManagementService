package com.quri.management.fixtures.security

import com.quri.management.api.security.auth.ClerkAuthoritiesConverter
import org.springframework.security.oauth2.jwt.Jwt

object ClerkClaimsFixtures {

    fun aJwt(claims: Map<String, Any> = EMPTY_CLAIMS): Jwt =
        Jwt(
            JwtFixtures.DEFAULT_TOKEN_VALUE,
            JwtFixtures.DEFAULT_ISSUED_AT,
            JwtFixtures.DEFAULT_EXPIRES_AT,
            mapOf("alg" to JwtFixtures.DEFAULT_ALG),
            claims,
        )

    val EMPTY_CLAIMS: Map<String, Any> = mapOf("sub" to JwtFixtures.DEFAULT_JWT_SUBJECT)

    fun claimsWithRole(role: String = ClerkAuthoritiesConverter.DEFAULT_ROLE): Map<String, Any> =
        mapOf(
            ClerkAuthoritiesConverter.METADATA_CLAIM to mapOf(
                ClerkAuthoritiesConverter.ROLE_CLAIM to role,
            ),
        )

    fun claimsWithNonStringRole(role: Any = 123): Map<String, Any> =
        mapOf(
            ClerkAuthoritiesConverter.METADATA_CLAIM to mapOf(
                ClerkAuthoritiesConverter.ROLE_CLAIM to role,
            ),
        )

    fun claimsWithMetadataNotAMap(metadata: Any = "not-a-map"): Map<String, Any> =
        mapOf(ClerkAuthoritiesConverter.METADATA_CLAIM to metadata)

    fun claimsWithoutRoleKey(): Map<String, Any> =
        mapOf(
            ClerkAuthoritiesConverter.METADATA_CLAIM to mapOf(
                "other_key" to "some_value",
            ),
        )
}
