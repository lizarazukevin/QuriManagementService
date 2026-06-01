package com.quri.management.api.validation.model

import com.quri.management.fixtures.models.ProfileFixtures.aUserLocation
import io.kotest.core.spec.style.DescribeSpec

@Suppress("unused")
class UserLocationValidatorTest :
    DescribeSpec({
        val addressFieldsValidator = AddressFieldsValidator()
        val validator = UserLocationValidator(addressFieldsValidator)

        describe("validate") {
            context("when inputs are valid") {
                it("passes") {
                    validator.validate("field", aUserLocation())
                }
            }
        }
    })
