package com.quri.management.api.validation.receipt

import com.quri.client.model.Fee
import com.quri.client.model.ValidationException
import com.quri.management.api.validation.model.AddressFieldsValidator
import com.quri.management.api.validation.model.AddressValidator
import com.quri.management.api.validation.model.DiscountValidator
import com.quri.management.api.validation.model.FeeValidator
import com.quri.management.api.validation.model.ItemValidator
import com.quri.management.api.validation.model.LiableValidator
import com.quri.management.api.validation.model.MonetaryAmountValidator
import com.quri.management.fixtures.models.ReceiptFixtures
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import java.math.BigDecimal
import java.time.Instant

@Suppress("unused")
class ReceiptFieldsValidatorTest :
    DescribeSpec({

        val monetaryAmountValidator = MonetaryAmountValidator()
        val liableValidator = LiableValidator()
        val discountValidator = DiscountValidator(monetaryAmountValidator)
        val itemValidator = ItemValidator(monetaryAmountValidator, liableValidator, discountValidator)
        val feeValidator = FeeValidator(monetaryAmountValidator)
        val addressFieldsValidator = AddressFieldsValidator()
        val addressValidator = AddressValidator(addressFieldsValidator)
        val validator = ReceiptFieldsValidator(itemValidator, monetaryAmountValidator, feeValidator, addressValidator)

        describe("validate") {

            context("when all fields are null") {
                it("passes, all fields are optional") {
                    validator.validate("field")
                }
            }

            context("vendorName") {
                it("passes at min boundary") {
                    validator.validate("field", vendorName = "abc")
                }

                it("passes at max boundary") {
                    validator.validate("field", vendorName = "a".repeat(150))
                }

                it("throws ValidationException below min length") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", vendorName = "ab")
                    }
                }

                it("throws ValidationException above max length") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", vendorName = "a".repeat(151))
                    }
                }
            }

            context("occurredAt") {
                it("passes for a past instant") {
                    validator.validate("field", occurredAt = Instant.now().minusSeconds(3600))
                }

                it("throws ValidationException for a future instant") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", occurredAt = Instant.now().plusSeconds(3600))
                    }
                }
            }

            context("subtotal") {
                it("passes for valid monetary amount") {
                    validator.validate("field", subtotal = ReceiptFixtures.aMonetaryAmount())
                }

                it("throws ValidationException for invalid currency") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", subtotal = ReceiptFixtures.aMonetaryAmount(currency = "invalid"))
                    }
                }
            }

            context("tax") {
                it("passes at zero") {
                    validator.validate("field", tax = BigDecimal.ZERO)
                }

                it("passes at one") {
                    validator.validate("field", tax = BigDecimal.ONE)
                }

                it("throws ValidationException above one") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", tax = BigDecimal("1.01"))
                    }
                }

                it("throws ValidationException below zero") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", tax = BigDecimal("-0.01"))
                    }
                }
            }

            context("tip") {
                it("passes at zero") {
                    validator.validate("field", tip = BigDecimal.ZERO)
                }

                it("passes at one") {
                    validator.validate("field", tip = BigDecimal.ONE)
                }

                it("throws ValidationException above one") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", tip = BigDecimal("1.01"))
                    }
                }

                it("throws ValidationException below zero") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", tip = BigDecimal("-0.01"))
                    }
                }
            }

            context("totalSavings") {
                it("passes for valid monetary amount") {
                    validator.validate("field", totalSavings = ReceiptFixtures.aMonetaryAmount())
                }

                it("throws ValidationException for invalid currency") {
                    shouldThrow<ValidationException> {
                        validator.validate(
                            "field",
                            totalSavings = ReceiptFixtures.aMonetaryAmount(currency = "invalid"),
                        )
                    }
                }
            }

            context("address") {
                it("passes for valid address") {
                    validator.validate("field", address = ReceiptFixtures.aValidAddress())
                }

                it("throws ValidationException for invalid postal code") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", address = ReceiptFixtures.aValidAddress(postalCode = "2000"))
                    }
                }

                it("throws ValidationException for invalid country code") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", address = ReceiptFixtures.aValidAddress(country = "us"))
                    }
                }
            }

            context("photoId") {
                it("passes at min boundary") {
                    validator.validate("field", photoId = "a")
                }

                it("passes at max boundary") {
                    validator.validate("field", photoId = "a".repeat(200))
                }

                it("throws ValidationException above max length") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", photoId = "a".repeat(201))
                    }
                }
            }

            context("items") {
                it("passes for valid item list") {
                    validator.validate("field", items = listOf(ReceiptFixtures.anItem()))
                }

                it("throws ValidationException for item with invalid units") {
                    shouldThrow<ValidationException> {
                        validator.validate(
                            "field",
                            items = listOf(ReceiptFixtures.anItem(units = 0)),
                        )
                    }
                }

                it("validates each item in the list independently") {
                    shouldThrow<ValidationException> {
                        validator.validate(
                            "field",
                            items = listOf(
                                ReceiptFixtures.anItem(),
                                ReceiptFixtures.anItem(units = 0),
                            ),
                        )
                    }
                }
            }

            context("fees") {
                it("passes for valid fee list") {
                    validator.validate(
                        "field",
                        fees = listOf(ReceiptFixtures.aFlatFee(), ReceiptFixtures.aPercentageFee()),
                    )
                }

                it("throws ValidationException for fee with neither value nor rate") {
                    shouldThrow<ValidationException> {
                        validator.validate(
                            "field",
                            fees = listOf(Fee.builder().name("Service").build()),
                        )
                    }
                }

                it("validates each fee in the list independently") {
                    shouldThrow<ValidationException> {
                        validator.validate(
                            "field",
                            fees = listOf(
                                ReceiptFixtures.aFlatFee(),
                                ReceiptFixtures.aPercentageFee(),
                                Fee.builder().name("Service").build(),
                            ),
                        )
                    }
                }
            }

            context("urls") {
                it("passes for valid url list") {
                    validator.validate("field", urls = listOf("https://quri.com/receipt.jpg"))
                }

                it("passes at max url length") {
                    validator.validate("field", urls = listOf("a".repeat(2048)))
                }

                it("throws ValidationException for url exceeding max length") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", urls = listOf("a".repeat(2049)))
                    }
                }

                it("validates each url in the list independently") {
                    shouldThrow<ValidationException> {
                        validator.validate(
                            "field",
                            urls = listOf(
                                "https://quri.com/receipt.jpg",
                                "a".repeat(2049),
                            ),
                        )
                    }
                }
            }
        }
    })
