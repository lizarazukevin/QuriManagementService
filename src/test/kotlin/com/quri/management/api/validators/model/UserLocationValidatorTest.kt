package com.quri.management.api.validators.model

import com.quri.client.model.UserLocation
import com.quri.management.api.validation.model.AddressFieldsValidator
import com.quri.management.api.validation.model.UserLocationValidator
import io.kotest.core.spec.style.DescribeSpec

@Suppress("unused")
class UserLocationValidatorTest :
    DescribeSpec({
        val addressFieldsValidator = AddressFieldsValidator()
        val validator = UserLocationValidator(addressFieldsValidator)

        describe("validate") {
            context("when inputs are valid") {
                it("passes") {
                    val input = UserLocation.builder()
                        .city("Arlington")
                        .state("VA")
                        .country("US")
                        .build()
                    validator.validate("field", input)
                }
            }
        }
    })
