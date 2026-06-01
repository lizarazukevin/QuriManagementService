package com.quri.management.api.validation.model

import com.quri.client.model.Fee
import com.quri.client.model.ValidationException
import com.quri.management.fixtures.models.ReceiptFixtures
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import java.math.BigDecimal

@Suppress("unused")
class FeeValidatorTest :
    DescribeSpec({

        val monetaryAmountValidator = MonetaryAmountValidator()
        val validator = FeeValidator(monetaryAmountValidator)

        describe("validate") {

            context("when only value is provided") {
                it("passes") {
                    val fee = Fee.builder()
                        .name("Service Fee")
                        .value(ReceiptFixtures.aMonetaryAmount())
                        .build()
                    validator.validate("field", fee)
                }
            }

            context("when only rate is provided") {
                it("passes") {
                    val fee = Fee.builder()
                        .name("Service Fee")
                        .rate(BigDecimal("0.1"))
                        .build()
                    validator.validate("field", fee)
                }
            }

            context("when both value and rate are provided") {
                it("throws ValidationException") {
                    val fee = Fee.builder()
                        .name("Service Fee")
                        .value(ReceiptFixtures.aMonetaryAmount())
                        .rate(BigDecimal("0.1"))
                        .build()
                    shouldThrow<ValidationException> {
                        validator.validate("field", fee)
                    }
                }
            }

            context("when neither value nor rate is provided") {
                it("throws ValidationException") {
                    val fee = Fee.builder().name("Service Fee").build()
                    shouldThrow<ValidationException> {
                        validator.validate("field", fee)
                    }
                }
            }

            context("when name exceeds max length") {
                it("throws ValidationException") {
                    val fee = Fee.builder()
                        .name("a".repeat(51))
                        .value(ReceiptFixtures.aMonetaryAmount())
                        .build()
                    shouldThrow<ValidationException> {
                        validator.validate("field", fee)
                    }
                }
            }

            context("when name length is lower than min length") {
                it("throws ValidationException") {
                    val fee = Fee.builder()
                        .name("a".repeat(1))
                        .value(ReceiptFixtures.aMonetaryAmount())
                        .build()
                    shouldThrow<ValidationException> {
                        validator.validate("field", fee)
                    }
                }
            }
        }
    })
