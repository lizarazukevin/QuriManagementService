package com.quri.management.api.validation.bill

import com.quri.client.model.MonetaryAmount
import com.quri.management.api.validation.model.MonetaryAmountValidator
import io.kotest.core.spec.style.DescribeSpec
import org.bson.types.ObjectId
import java.math.BigDecimal

@Suppress("unused")
class BillFieldsValidatorTest :
    DescribeSpec({

        val monetaryAmountValidator = MonetaryAmountValidator()
        val validator = BillFieldsValidator(monetaryAmountValidator)

        describe("validate on shared bill properties") {

            context("when all fields are null") {
                it("passes") {
                    validator.validate("field")
                }
            }

            context("when all valid fields are present") {
                it("passes") {
                    validator.validate(
                        "field",
                        name = "test name",
                        description = "test description",
                        balance = MonetaryAmount.builder()
                            .amount(BigDecimal(100))
                            .currency("USD")
                            .build(),
                        receipts = listOf(ObjectId().toString()),
                    )
                }
            }
        }
    })
