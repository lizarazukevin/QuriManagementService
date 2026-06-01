package com.quri.management.api.validation.model

import com.quri.client.model.ValidationException
import com.quri.management.fixtures.models.ReceiptFixtures
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec

@Suppress("unused")
class MonetaryAmountValidatorTest :
    DescribeSpec({

        val validator = MonetaryAmountValidator()

        describe("validate") {

            context("when currency is a valid ISO 4217 code") {
                it("passes for USD") {
                    validator.validate("field", ReceiptFixtures.aMonetaryAmount(currency = "USD"))
                }

                it("passes for EUR") {
                    validator.validate("field", ReceiptFixtures.aMonetaryAmount(currency = "EUR"))
                }
            }

            context("when currency is invalid") {

                it("throws ValidationException for lowercase code") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", ReceiptFixtures.aMonetaryAmount(currency = "usd"))
                    }
                }

                it("throws ValidationException for numeric code") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", ReceiptFixtures.aMonetaryAmount(currency = "123"))
                    }
                }

                it("throws ValidationException for code longer than 3 chars") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", ReceiptFixtures.aMonetaryAmount(currency = "USDD"))
                    }
                }
            }
        }
    })
