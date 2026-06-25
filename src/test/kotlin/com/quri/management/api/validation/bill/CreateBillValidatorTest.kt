package com.quri.management.api.validation.bill

import com.quri.client.model.BillStatus
import com.quri.client.model.CreateBillInput
import com.quri.client.model.ValidationException
import com.quri.management.api.validation.model.MonetaryAmountValidator
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
                    validator.validate(
                        "createBill",
                        CreateBillInput.builder()
                            .name("Test Bill")
                            .status(BillStatus.DRAFT)
                            .build(),
                    )
                }
            }

            context("when status is PUBLISHED") {
                it("passes") {
                    validator.validate(
                        "createBill",
                        CreateBillInput.builder()
                            .name("Test Bill")
                            .status(BillStatus.PUBLISHED)
                            .build(),
                    )
                }
            }

            context("when status is not PUBLISHED or DRAFT") {
                it("throws ValidationException") {
                    shouldThrow<ValidationException> {
                        validator.validate(
                            "createBill",
                            CreateBillInput.builder()
                                .name("Test Bill")
                                .status(BillStatus.COMPLETED)
                                .build(),
                        )
                    }
                }
            }
        }
    })
