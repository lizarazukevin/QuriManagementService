package com.quri.management.api.validation.bill

import com.quri.client.model.BillStatus
import com.quri.client.model.ValidationException
import com.quri.management.api.validation.model.MonetaryAmountValidator
import com.quri.management.fixtures.models.BillFixtures
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec

@Suppress("unused")
class CreateBillValidatorTest :
    DescribeSpec({

        val monetaryAmountValidator = MonetaryAmountValidator()
        val billFieldsValidator = BillFieldsValidator(monetaryAmountValidator)
        val validator = CreateBillValidator(billFieldsValidator)

        describe("validate") {

            context("when status is DRAFT") {
                it("passes") {
                    val input = BillFixtures.aCreateBillInput(status = BillStatus.DRAFT)
                    validator.validate("createBill", input)
                }
            }

            context("when status is PUBLISHED") {
                it("passes") {
                    val input = BillFixtures.aCreateBillInput(status = BillStatus.PUBLISHED)
                    validator.validate("createBill", input)
                }
            }

            context("when status is PENDING") {
                it("throws ValidationException") {
                    val input = BillFixtures.aCreateBillInput(status = BillStatus.PENDING)
                    shouldThrow<ValidationException> {
                        validator.validate("createBill", input)
                    }
                }
            }

            context("when status is COMPLETED") {
                it("throws ValidationException") {
                    val input = BillFixtures.aCreateBillInput(status = BillStatus.COMPLETED)
                    shouldThrow<ValidationException> {
                        validator.validate("createBill", input)
                    }
                }
            }

            context("when status is CLOSED") {
                it("throws ValidationException") {
                    val input = BillFixtures.aCreateBillInput(status = BillStatus.CLOSED)
                    shouldThrow<ValidationException> {
                        validator.validate("createBill", input)
                    }
                }
            }
        }
    })
