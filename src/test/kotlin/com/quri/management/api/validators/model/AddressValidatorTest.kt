package com.quri.management.api.validators.model

import com.quri.management.api.validation.model.AddressFieldsValidator
import com.quri.management.api.validation.model.AddressValidator
import com.quri.management.fixtures.models.ReceiptFixtures
import io.kotest.core.spec.style.DescribeSpec

@Suppress("unused")
class AddressValidatorTest :
    DescribeSpec({

        val addressFieldsValidator = AddressFieldsValidator()
        val validator = AddressValidator(addressFieldsValidator)

        describe("validate") {

            context("when inputs are valid") {
                val input = ReceiptFixtures.aValidAddress()

                it("passes with no optional fields") {
                    validator.validate("field", input)
                }

                it("passes with optional fields") {
                    input.toBuilder()
                        .unit("404")
                        .rawInput("test raw input")
                        .formatted("test formatted input")
                        .build()
                    validator.validate("field", input)
                }
            }
        }
    })
