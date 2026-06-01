package com.quri.management.api.validators.bill

import com.quri.client.model.BillStatus
import com.quri.management.api.validation.bill.BillFieldsValidator
import com.quri.management.api.validation.bill.UpdateBillValidator
import com.quri.management.api.validation.model.MonetaryAmountValidator
import com.quri.management.fixtures.models.BillFixtures
import com.quri.management.fixtures.models.ReceiptFixtures.DEFAULT_RECEIPT_ID
import io.kotest.core.spec.style.DescribeSpec

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

            context("when all fields are valid") {
                it("passes") {
                    val input = BillFixtures.anUpdateBillInput(
                        name = "Updated Bill",
                        status = BillStatus.CLOSED,
                        hidden = true,
                        description = "test description",
                        balance = BillFixtures.aMonetaryAmount(),
                        receipts = listOf(DEFAULT_RECEIPT_ID),
                    )
                    validator.validate("updateBill", input)
                }
            }
        }
    })
