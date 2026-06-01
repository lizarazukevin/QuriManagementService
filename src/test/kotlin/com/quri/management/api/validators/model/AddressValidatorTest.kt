package com.quri.management.api.validators.model

import com.quri.client.model.Address
import com.quri.management.api.validation.model.AddressFieldsValidator
import com.quri.management.api.validation.model.AddressValidator
import io.kotest.core.spec.style.DescribeSpec

@Suppress("unused")
class AddressValidatorTest :
    DescribeSpec({

        val addressFieldsValidator = AddressFieldsValidator()
        val validator = AddressValidator(addressFieldsValidator)

        describe("validate") {

            context("when inputs are valid") {
                val input = Address.builder()
                    .street("123 Main St")
                    .city("Arlington")
                    .state("VA")
                    .postalCode("20001")
                    .country("US")
                    .build()

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
