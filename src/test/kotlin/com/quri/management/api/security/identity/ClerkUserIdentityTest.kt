package com.quri.management.api.security.identity

import com.quri.management.api.security.auth.ClerkAuthoritiesConverter
import com.quri.management.fixtures.security.JwtFixtures.aJwt
import com.quri.management.fixtures.security.JwtFixtures.aJwtWithNoSubject
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

@Suppress("unused")
class ClerkUserIdentityTest :
    DescribeSpec({

        val identity = ClerkUserIdentity()

        describe("userId") {

            context("when an authenticated JWT is present") {
                it("returns the JWT subject") {
                    val token = JwtAuthenticationToken(aJwt("user-42"), emptyList())

                    val result = mono { identity.userId() }
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(token))
                        .awaitSingle()

                    result shouldBe "user-42"
                }
            }

            context("when the JWT subject is null") {
                it("throws IllegalStateException") {
                    val token = JwtAuthenticationToken(aJwtWithNoSubject(), emptyList())

                    shouldThrow<IllegalStateException> {
                        mono { identity.userId() }
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(token))
                            .awaitSingle()
                    }
                }
            }
        }

        describe("role") {

            context("when the JWT has a role authority") {
                it("returns the role lowercased without the ROLE_ prefix") {
                    val authorities: List<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
                    val token = JwtAuthenticationToken(aJwt("user-1"), authorities)

                    val result = mono { identity.role() }
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(token))
                        .awaitSingle()

                    result shouldBe "admin"
                }
            }

            context("when the JWT role authority is already lowercase") {
                it("returns it unchanged") {
                    val authorities: List<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_admin"))
                    val token = JwtAuthenticationToken(aJwt("user-1"), authorities)

                    val result = mono { identity.role() }
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(token))
                        .awaitSingle()

                    result shouldBe "admin"
                }
            }

            context("when the JWT has no role authority") {
                it("returns the default role") {
                    val token = JwtAuthenticationToken(aJwt("user-1"), emptyList())

                    val result = mono { identity.role() }
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(token))
                        .awaitSingle()

                    result shouldBe ClerkAuthoritiesConverter.DEFAULT_ROLE
                }
            }
        }

        describe("authentication failures shared by userId and role") {

            context("when no authentication is present") {
                it("userId throws") {
                    shouldThrow<NoSuchElementException> {
                        mono { identity.userId() }.awaitSingle()
                    }
                }

                it("role throws") {
                    shouldThrow<NoSuchElementException> {
                        mono { identity.role() }.awaitSingle()
                    }
                }
            }

            context("when the authentication is not a JwtAuthenticationToken") {
                it("userId throws IllegalStateException") {
                    val token = UsernamePasswordAuthenticationToken("user", "pw", emptyList())

                    shouldThrow<IllegalStateException> {
                        mono { identity.userId() }
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(token))
                            .awaitSingle()
                    }
                }

                it("role throws IllegalStateException") {
                    val token = UsernamePasswordAuthenticationToken("user", "pw", emptyList())

                    shouldThrow<IllegalStateException> {
                        mono { identity.role() }
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(token))
                            .awaitSingle()
                    }
                }
            }
        }
    })
