package com.quri.management.security.identity

/**
 * Abstracts the current authenticated user from underlying auth provider.
 *
 * All methods are suspended because reading from reactive security context
 * is an async operation in WebFlux.
 *
 * Controllers and services depend on this interface rather than Spring Security
 * directly, meaning the auth provider can be swapped without affecting downstream logic.
 */
interface UserIdentity {
    /**
     * A user identifier from the JWT `sub` claim used as foreign key to created objects.
     */
    suspend fun userId(): String

    /**
     * Raw role string stored in auth provider's metadata.
     */
    suspend fun role(): String
}
