package com.quri.management.api.validation.model

import com.quri.client.model.Liable
import com.quri.client.model.ValidationException
import com.quri.management.fixtures.models.ProfileFixtures.DEFAULT_PROFILE_ID
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import java.math.BigDecimal

@Suppress("unused")
class LiableValidatorTest :
    DescribeSpec({

        val validator = LiableValidator()

        describe("validate") {

            context("when rate is within 0..1") {
                it("passes at zero") {
                    validator.validate(
                        "field",
                        Liable.builder()
                            .userId(DEFAULT_PROFILE_ID)
                            .rate(BigDecimal.ZERO)
                            .build(),
                    )
                }

                it("passes at one") {
                    validator.validate(
                        "field",
                        Liable.builder()
                            .userId(DEFAULT_PROFILE_ID)
                            .rate(BigDecimal.ONE)
                            .build(),
                    )
                }

                it("passes at mid range") {
                    validator.validate(
                        "field",
                        Liable.builder()
                            .userId(DEFAULT_PROFILE_ID)
                            .rate(BigDecimal("0.5"))
                            .build(),
                    )
                }
            }

            context("when rate is out of range") {
                it("throws ValidationException above one") {
                    shouldThrow<ValidationException> {
                        validator.validate(
                            "field",
                            Liable.builder()
                                .userId(DEFAULT_PROFILE_ID)
                                .rate(BigDecimal("1.01"))
                                .build(),
                        )
                    }
                }

                it("throws ValidationException below zero") {
                    shouldThrow<ValidationException> {
                        validator.validate(
                            "field",
                            Liable.builder()
                                .userId(DEFAULT_PROFILE_ID)
                                .rate(BigDecimal("-0.01"))
                                .build(),
                        )
                    }
                }
            }
        }
    })
