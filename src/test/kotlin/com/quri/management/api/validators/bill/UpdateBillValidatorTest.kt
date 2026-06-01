package com.quri.management.api.validators.bill

import com.quri.client.model.ValidationException
import com.quri.management.api.validation.bill.BillFieldsValidator
import com.quri.management.api.validation.bill.UpdateBillValidator
import com.quri.management.api.validation.model.MonetaryAmountValidator
import com.quri.management.fixtures.models.BillFixtures
import com.quri.management.fixtures.models.ReceiptFixtures
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import org.bson.types.ObjectId

@Suppress("unused")
class UpdateBillValidatorTest :
    DescribeSpec({

        val monetaryAmountValidator = MonetaryAmountValidator()
        val billFieldsValidator = BillFieldsValidator(monetaryAmountValidator)
        val validator = UpdateBillValidator(billFieldsValidator)

        describe("validate") {

            context("when all fields are null") {
                it("passes, all fields are optional on update") {
                    val input = BillFixtures.anUpdateBillInput()
                    validator.validate("updateBill", input)
                }
            }

            context("when name is valid") {
                it("passes") {
                    val input = BillFixtures.anUpdateBillInput(name = "Updated Bill")
                    validator.validate("updateBill", input)
                }
            }

            context("when name exceeds max length") {
                it("throws ValidationException") {
                    val input = BillFixtures.anUpdateBillInput(name = "a".repeat(33))
                    shouldThrow<ValidationException> {
                        validator.validate("updateBill", input)
                    }
                }
            }

            context("when balance has invalid currency") {
                it("throws ValidationException") {
                    val input = BillFixtures.anUpdateBillInput(
                        balance = ReceiptFixtures.aMonetaryAmount(currency = "invalid"),
                    )
                    shouldThrow<ValidationException> {
                        validator.validate("updateBill", input)
                    }
                }
            }

            context("when receipts contain an invalid ObjectId") {
                it("throws ValidationException") {
                    val input = BillFixtures.anUpdateBillInput(
                        receipts = listOf("not-an-objectid"),
                    )
                    shouldThrow<ValidationException> {
                        validator.validate("updateBill", input)
                    }
                }
            }

            context("when receipts are valid ObjectIds") {
                it("passes") {
                    val input = BillFixtures.anUpdateBillInput(
                        receipts = listOf(ObjectId().toString()),
                    )
                    validator.validate("updateBill", input)
                }
            }
        }
    })
