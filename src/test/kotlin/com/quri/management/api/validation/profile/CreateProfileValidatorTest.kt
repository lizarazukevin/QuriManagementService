package com.quri.management.api.validation.profile

import com.quri.management.api.validation.model.AddressFieldsValidator
import com.quri.management.api.validation.model.UserLocationValidator
import com.quri.management.fixtures.models.ProfileFixtures
import io.kotest.core.spec.style.DescribeSpec

@Suppress("unused")
class CreateProfileValidatorTest :
    DescribeSpec({

        val addressFieldsValidator = AddressFieldsValidator()
        val userLocationValidator = UserLocationValidator(addressFieldsValidator)
        val profileFieldsValidator = ProfileFieldsValidator(userLocationValidator)
        val validator = CreateProfileValidator(profileFieldsValidator)

        describe("validate") {

            context("when all required fields are valid") {
                it("passes") {
                    val input = ProfileFixtures.aCreateProfileInput()
                    validator.validate("createProfile", input)
                }
            }
        }
    })
