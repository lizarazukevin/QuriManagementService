package com.quri.management.api.validators.receipt

import com.quri.client.model.PaymentMethod
import com.quri.management.api.validation.model.AddressFieldsValidator
import com.quri.management.api.validation.model.AddressValidator
import com.quri.management.api.validation.model.DiscountValidator
import com.quri.management.api.validation.model.FeeValidator
import com.quri.management.api.validation.model.ItemValidator
import com.quri.management.api.validation.model.LiableValidator
import com.quri.management.api.validation.model.MonetaryAmountValidator
import com.quri.management.api.validation.receipt.ReceiptFieldsValidator
import com.quri.management.api.validation.receipt.UpdateReceiptValidator
import com.quri.management.fixtures.models.ReceiptFixtures
import io.kotest.core.spec.style.DescribeSpec
import java.math.BigDecimal
import java.time.Instant

@Suppress("unused")
class UpdateReceiptValidatorTest :
    DescribeSpec({

        val monetaryAmountValidator = MonetaryAmountValidator()
        val liableValidator = LiableValidator()
        val discountValidator = DiscountValidator(monetaryAmountValidator)
        val itemValidator = ItemValidator(monetaryAmountValidator, liableValidator, discountValidator)
        val feeValidator = FeeValidator(monetaryAmountValidator)
        val addressFieldsValidator = AddressFieldsValidator()
        val addressValidator = AddressValidator(addressFieldsValidator)
        val receiptFieldsValidator = ReceiptFieldsValidator(
            itemValidator,
            monetaryAmountValidator,
            feeValidator,
            addressValidator,
        )
        val validator = UpdateReceiptValidator(receiptFieldsValidator)

        describe("validate") {

            context("when all fields are null") {
                it("passes") {
                    val input = ReceiptFixtures.anUpdateReceiptInput()
                    validator.validate("UpdateReceipt", input)
                }
            }

            context("when all fields are valid") {
                it("passes") {
                    val input = ReceiptFixtures.anUpdateReceiptInput(
                        vendorName = "test vendor name",
                        items = listOf(ReceiptFixtures.anItem()),
                        occurredAt = Instant.now(),
                        paymentMethod = PaymentMethod.CREDIT,
                        subtotal = ReceiptFixtures.aMonetaryAmount(),
                        tax = BigDecimal("0.05"),
                        tip = BigDecimal("0.05"),
                        totalSavings = ReceiptFixtures.aMonetaryAmount(),
                        fees = listOf(ReceiptFixtures.aFee()),
                        photoId = "test photo id",
                    )
                    validator.validate("UpdateReceipt", input)
                }
            }
        }
    })
