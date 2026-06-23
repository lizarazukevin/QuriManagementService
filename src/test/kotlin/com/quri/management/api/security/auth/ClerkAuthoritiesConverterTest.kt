package com.quri.management.api.security.auth

import com.quri.management.fixtures.security.ClerkClaimsFixtures
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Suppress("unused")
class ClerkAuthoritiesConverterTest :
    DescribeSpec({

        val converter = ClerkAuthoritiesConverter()

        describe("convert") {

            context("when metadata claim contains a role") {
                it("returns a single ROLE_ prefixed authority, uppercased") {
                    val jwt = ClerkClaimsFixtures.aJwt(claims = ClerkClaimsFixtures.claimsWithRole(role = "admin"))

                    val result = converter.convert(jwt)

                    result shouldBe listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
                }

                it("uppercases a differently-cased role value") {
                    val jwt = ClerkClaimsFixtures.aJwt(claims = ClerkClaimsFixtures.claimsWithRole(role = "member"))

                    val result = converter.convert(jwt)

                    result shouldBe listOf(SimpleGrantedAuthority("ROLE_MEMBER"))
                }
            }

            context("when metadata claim is absent") {
                it("returns empty list") {
                    val jwt = ClerkClaimsFixtures.aJwt(claims = ClerkClaimsFixtures.EMPTY_CLAIMS)

                    val result = converter.convert(jwt)

                    result.shouldBeEmpty()
                }
            }

            context("when metadata claim exists but has no role key") {
                it("returns empty list") {
                    val jwt = ClerkClaimsFixtures.aJwt(claims = ClerkClaimsFixtures.claimsWithoutRoleKey())

                    val result = converter.convert(jwt)

                    result.shouldBeEmpty()
                }
            }

            context("when metadata claim is not a map") {
                it("returns empty list") {
                    val jwt = ClerkClaimsFixtures.aJwt(claims = ClerkClaimsFixtures.claimsWithMetadataNotAMap())

                    val result = converter.convert(jwt)

                    result.shouldBeEmpty()
                }
            }

            context("when role claim is not a string") {
                it("returns empty list") {
                    val jwt = ClerkClaimsFixtures.aJwt(claims = ClerkClaimsFixtures.claimsWithNonStringRole())

                    val result = converter.convert(jwt)

                    result.shouldBeEmpty()
                }
            }
        }
    })
