package com.quri.management.api.validators.receipt

import com.quri.management.api.validation.model.AddressFieldsValidator
import com.quri.management.api.validation.model.AddressValidator
import com.quri.management.api.validation.model.DiscountValidator
import com.quri.management.api.validation.model.FeeValidator
import com.quri.management.api.validation.model.ItemValidator
import com.quri.management.api.validation.model.LiableValidator
import com.quri.management.api.validation.model.MonetaryAmountValidator
import com.quri.management.api.validation.receipt.CreateReceiptValidator
import com.quri.management.api.validation.receipt.ReceiptFieldsValidator
import com.quri.management.fixtures.models.ReceiptFixtures
import io.kotest.core.spec.style.DescribeSpec

class CreateReceiptValidatorTest :
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
        val validator = CreateReceiptValidator(receiptFieldsValidator)

        describe("validate") {

            context("when all required fields are valid") {
                it("passes") {
                    val input = ReceiptFixtures.aCreateReceiptInput()
                    validator.validate("createReceipt", input)
                }
            }
        }
    })
