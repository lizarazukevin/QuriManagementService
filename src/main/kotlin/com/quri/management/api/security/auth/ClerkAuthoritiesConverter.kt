package com.quri.management.api.security.auth

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

/**
 * Extracts granted authorities from a Clerk-issued JWT.
 *
 * Clerk stores the user's role inside a nested `metadata` claim:
 * ```json
 * { "metadata": { "role": "admin" } }
 * ```
 * Ref: https://clerk.com/docs/guides/users/extending#metadata-in-the-session-token
 *
 * This converter translates that role into a Spring Security authority
 * using the `Role_` prefix convention required by [hasRole] checks.
 * Ref: https://docs.spring.io/spring-security/reference/servlet/authorization/architecture.html
 *
 * Returns an empty list if metadata claim is absent or contains no role,
 * results in the user being authenticated but having no granted roles.
 */
@Component
class ClerkAuthoritiesConverter : Converter<Jwt, Collection<GrantedAuthority>> {

    override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
        val role = jwt.extractRole() ?: return emptyList()
        return listOf(SimpleGrantedAuthority("${ROLE_PREFIX}${role.uppercase()}"))
    }

    private fun Jwt.extractRole(): String? =
        (claims[METADATA_CLAIM] as? Map<*, *>)
            ?.get(ROLE_CLAIM) as? String

    companion object {
        const val METADATA_CLAIM = "metadata"
        const val ROLE_CLAIM = "role"
        const val ROLE_PREFIX = "ROLE_"
        const val DEFAULT_ROLE = "guest"
    }
}
