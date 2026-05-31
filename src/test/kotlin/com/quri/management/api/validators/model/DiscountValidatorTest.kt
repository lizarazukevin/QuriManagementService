package com.quri.management.api.validators.model

import com.quri.client.model.Discount
import com.quri.client.model.DiscountType
import com.quri.client.model.ValidationException
import com.quri.management.api.validation.model.DiscountValidator
import com.quri.management.api.validation.model.MonetaryAmountValidator
import com.quri.management.fixtures.models.ReceiptFixtures
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import java.math.BigDecimal

@Suppress("unused")
class DiscountValidatorTest :
    DescribeSpec({

        val monetaryAmountValidator = MonetaryAmountValidator()
        val validator = DiscountValidator(monetaryAmountValidator)

        describe("validate") {

            context("when only value is provided") {
                it("passes") {
                    val discount = Discount.builder()
                        .category(DiscountType.SALE)
                        .value(ReceiptFixtures.aMonetaryAmount())
                        .build()
                    validator.validate("field", discount)
                }
            }

            context("when only rate is provided") {
                it("passes") {
                    val discount = Discount.builder()
                        .category(DiscountType.SALE)
                        .rate(BigDecimal("0.1"))
                        .build()
                    validator.validate("field", discount)
                }
            }

            context("when both value and rate are provided") {
                it("throws ValidationException") {
                    val discount = Discount.builder()
                        .category(DiscountType.SALE)
                        .value(ReceiptFixtures.aMonetaryAmount())
                        .rate(BigDecimal("0.1"))
                        .build()
                    shouldThrow<ValidationException> {
                        validator.validate("field", discount)
                    }
                }
            }

            context("when both value and rate are not provided") {
                it("throws ValidationException") {
                    val discount = Discount.builder()
                        .category(DiscountType.SALE)
                        .build()
                    shouldThrow<ValidationException> {
                        validator.validate("field", discount)
                    }
                }
            }

            context("when value has invalid currency") {
                it("throws ValidationException") {
                    val discount = Discount.builder()
                        .category(DiscountType.SALE)
                        .value(ReceiptFixtures.aMonetaryAmount(currency = "invalid"))
                        .build()
                    shouldThrow<ValidationException> {
                        validator.validate("field", discount)
                    }
                }
            }

            context("when rate is out of range") {
                it("throws ValidationException") {
                    val discount = Discount.builder()
                        .category(DiscountType.SALE)
                        .rate(BigDecimal("1.5"))
                        .build()
                    shouldThrow<ValidationException> {
                        validator.validate("field", discount)
                    }
                }
            }
        }
    })
