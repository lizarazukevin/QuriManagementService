package com.quri.management.api.validators.profile

import com.quri.management.api.validation.model.AddressFieldsValidator
import com.quri.management.api.validation.model.UserLocationValidator
import com.quri.management.api.validation.profile.ProfileFieldsValidator
import com.quri.management.api.validation.profile.UpdateProfileValidator
import com.quri.management.fixtures.models.ProfileFixtures
import io.kotest.core.spec.style.DescribeSpec

@Suppress("unused")
class UpdateProfileValidatorTest :
    DescribeSpec({

        val addressFieldsValidator = AddressFieldsValidator()
        val userLocationValidator = UserLocationValidator(addressFieldsValidator)
        val profileFieldsValidator = ProfileFieldsValidator(userLocationValidator)
        val validator = UpdateProfileValidator(profileFieldsValidator)

        describe("validate") {

            context("when all fields are valid") {
                it("passes") {
                    val input = ProfileFixtures.anUpdateProfileInput()
                    validator.validate("updateProfile", input)
                }
            }
        }
    })
