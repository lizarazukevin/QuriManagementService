package com.quri.management.security.identity

import com.quri.management.security.auth.ClerkAuthoritiesConverter
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

/**
 * Reads the current user's identity from the reactive security context.
 */
@Component
class ClerkUserIdentity : UserIdentity {

    override suspend fun userId(): String =
        authentication().token.subject
            ?: error("Could not resolve userId")

    override suspend fun role(): String =
        authentication().authorities
            .firstOrNull { it.authority?.startsWith(ClerkAuthoritiesConverter.ROLE_PREFIX) == true }
            ?.authority
            ?.removePrefix(ClerkAuthoritiesConverter.ROLE_PREFIX)
            ?.lowercase()
            ?: ClerkAuthoritiesConverter.DEFAULT_ROLE

    /**
     * Retrieves JWT authentication token from the reactive security context.
     * Populated after verifying the JWT signature, it throws if called outside
     * an authenticated request.
     */
    private suspend fun authentication(): JwtAuthenticationToken =
        ReactiveSecurityContextHolder.getContext()
            .map {
                it.authentication as? JwtAuthenticationToken
                    ?: error("No authenticated JWT in SecurityContext")
            }
            .awaitSingle()
}
