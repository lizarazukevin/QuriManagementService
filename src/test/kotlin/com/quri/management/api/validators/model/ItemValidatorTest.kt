package com.quri.management.api.validators.model

import com.quri.client.model.Discount
import com.quri.client.model.DiscountType
import com.quri.client.model.Item
import com.quri.client.model.Liable
import com.quri.client.model.ValidationException
import com.quri.management.api.validation.model.DiscountValidator
import com.quri.management.api.validation.model.ItemValidator
import com.quri.management.api.validation.model.LiableValidator
import com.quri.management.api.validation.model.MonetaryAmountValidator
import com.quri.management.fixtures.models.ProfileFixtures.DEFAULT_PROFILE_ID
import com.quri.management.fixtures.models.ReceiptFixtures
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import java.math.BigDecimal

@Suppress("unused")
class ItemValidatorTest :
    DescribeSpec({

        val monetaryAmountValidator = MonetaryAmountValidator()
        val liableValidator = LiableValidator()
        val discountValidator = DiscountValidator(monetaryAmountValidator)
        val validator = ItemValidator(monetaryAmountValidator, liableValidator, discountValidator)

        describe("validate") {

            context("when all required fields are valid") {
                it("passes") {
                    val item = ReceiptFixtures.anItem()
                    validator.validate("field", item)
                }
            }

            context("units") {
                it("throws ValidationException when below min") {
                    val item = Item.builder()
                        .name("Item")
                        .units(0)
                        .unitCost(ReceiptFixtures.aMonetaryAmount())
                        .build()
                    shouldThrow<ValidationException> {
                        validator.validate("field", item)
                    }
                }

                it("throws ValidationException when above max") {
                    val item = Item.builder()
                        .name("Item")
                        .units(1_000_000_001)
                        .unitCost(ReceiptFixtures.aMonetaryAmount())
                        .build()
                    shouldThrow<ValidationException> {
                        validator.validate("field", item)
                    }
                }
            }

            context("name") {
                it("throws ValidationException when exceeds max length") {
                    val item = Item.builder()
                        .name("a".repeat(256))
                        .units(1)
                        .unitCost(ReceiptFixtures.aMonetaryAmount())
                        .build()
                    shouldThrow<ValidationException> {
                        validator.validate("field", item)
                    }
                }
            }

            context("discounts") {
                it("passes with a valid discount list") {
                    val item = Item.builder()
                        .name("Item")
                        .units(1)
                        .unitCost(ReceiptFixtures.aMonetaryAmount())
                        .discounts(
                            listOf(
                                Discount.builder()
                                    .category(DiscountType.SALE)
                                    .rate(BigDecimal("0.69"))
                                    .build(),
                            ),
                        )
                        .build()

                    validator.validate("field", item)
                }

                it("throws ValidationException when discount has both value and rate") {
                    val item = Item.builder()
                        .name("Item")
                        .units(1)
                        .unitCost(ReceiptFixtures.aMonetaryAmount())
                        .discounts(
                            listOf(
                                Discount.builder()
                                    .category(DiscountType.SALE)
                                    .value(ReceiptFixtures.aMonetaryAmount())
                                    .rate(BigDecimal("0.1"))
                                    .build(),
                            ),
                        )
                        .build()

                    shouldThrow<ValidationException> {
                        validator.validate("field", item)
                    }
                }
            }

            context("liable") {
                it("passes with a valid liable list") {
                    val item = Item.builder()
                        .name("Item")
                        .units(1)
                        .unitCost(ReceiptFixtures.aMonetaryAmount())
                        .liable(
                            listOf(
                                Liable.builder()
                                    .userId(DEFAULT_PROFILE_ID)
                                    .rate(BigDecimal("0.69"))
                                    .build(),
                            ),
                        )
                        .build()

                    validator.validate("field", item)
                }

                it("throws ValidationException when user ID is not valid") {
                    val item = Item.builder()
                        .name("Item")
                        .units(1)
                        .unitCost(ReceiptFixtures.aMonetaryAmount())
                        .liable(
                            listOf(
                                Liable.builder()
                                    .userId("fake-id")
                                    .rate(BigDecimal("0.69"))
                                    .build(),
                            ),
                        )
                        .build()

                    shouldThrow<ValidationException> {
                        validator.validate("field", item)
                    }
                }
            }
        }
    })
