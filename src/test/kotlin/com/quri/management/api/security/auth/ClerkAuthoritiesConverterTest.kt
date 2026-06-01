package com.quri.management.api.security.auth

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

@Suppress("unused")
class ClerkAuthoritiesConverterTest :
    DescribeSpec({

        val converter = ClerkAuthoritiesConverter()

        fun aJwt(claims: Map<String, Any> = emptyMap()): Jwt =
            mockk<Jwt> {
                every { this@mockk.claims } returns claims
            }

        describe("convert") {

            context("when metadata claim contains a role") {
                it("returns a ROLE_ prefixed authority uppercased") {
                    val jwt = aJwt(
                        claims = mapOf(
                            ClerkAuthoritiesConverter.METADATA_CLAIM to mapOf(
                                ClerkAuthoritiesConverter.ROLE_CLAIM to "admin",
                            ),
                        ),
                    )

                    val result = converter.convert(jwt)

                    result shouldHaveSize 1
                    result.first() shouldBe SimpleGrantedAuthority("ROLE_ADMIN")
                }

                it("uppercases the role value") {
                    val jwt = aJwt(
                        claims = mapOf(
                            ClerkAuthoritiesConverter.METADATA_CLAIM to mapOf(
                                ClerkAuthoritiesConverter.ROLE_CLAIM to "member",
                            ),
                        ),
                    )

                    val result = converter.convert(jwt)

                    result.first() shouldBe SimpleGrantedAuthority("ROLE_MEMBER")
                }
            }

            context("when metadata claim is absent") {
                it("returns empty list") {
                    val jwt = aJwt(claims = emptyMap())

                    val result = converter.convert(jwt)

                    result.shouldBeEmpty()
                }
            }

            context("when metadata claim exists but has no role key") {
                it("returns empty list") {
                    val jwt = aJwt(
                        claims = mapOf(
                            ClerkAuthoritiesConverter.METADATA_CLAIM to mapOf(
                                "other_key" to "some_value",
                            ),
                        ),
                    )

                    val result = converter.convert(jwt)

                    result.shouldBeEmpty()
                }
            }

            context("when metadata claim is not a map") {
                it("returns empty list") {
                    val jwt = aJwt(
                        claims = mapOf(
                            ClerkAuthoritiesConverter.METADATA_CLAIM to "not-a-map",
                        ),
                    )

                    val result = converter.convert(jwt)

                    result.shouldBeEmpty()
                }
            }

            context("when role claim is not a string") {
                it("returns empty list") {
                    val jwt = aJwt(
                        claims = mapOf(
                            ClerkAuthoritiesConverter.METADATA_CLAIM to mapOf(
                                ClerkAuthoritiesConverter.ROLE_CLAIM to 123,
                            ),
                        ),
                    )

                    val result = converter.convert(jwt)

                    result.shouldBeEmpty()
                }
            }
        }
    })
