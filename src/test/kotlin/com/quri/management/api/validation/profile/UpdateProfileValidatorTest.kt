package com.quri.management.api.validation.profile

import com.quri.client.model.Gender
import com.quri.management.api.validation.model.AddressFieldsValidator
import com.quri.management.api.validation.model.UserLocationValidator
import com.quri.management.fixtures.models.ProfileFixtures
import com.quri.management.fixtures.models.ProfileFixtures.DEFAULT_PROFILE_ID
import io.kotest.core.spec.style.DescribeSpec

@Suppress("unused")
class UpdateProfileValidatorTest :
    DescribeSpec({

        val addressFieldsValidator = AddressFieldsValidator()
        val userLocationValidator = UserLocationValidator(addressFieldsValidator)
        val profileFieldsValidator = ProfileFieldsValidator(userLocationValidator)
        val validator = UpdateProfileValidator(profileFieldsValidator)

        describe("validate") {

            context("when all fields are null") {
                it("passes") {
                    val input = ProfileFixtures.anUpdateProfileInput()
                    validator.validate("UpdateProfile", input)
                }
            }

            context("when all fields are valid") {
                it("passes") {
                    val input = ProfileFixtures.anUpdateProfileInput(
                        username = "john.smith",
                        firstName = "John",
                        lastName = "Smith",
                        email = "johnsmith@gmail.com",
                        middleName = "J",
                        phoneNumber = "0123456789",
                        bio = "test bio",
                        following = listOf(DEFAULT_PROFILE_ID),
                        followers = listOf(DEFAULT_PROFILE_ID),
                        gender = Gender.MALE,
                        location = ProfileFixtures.aUserLocation(),
                    )
                    validator.validate("UpdateProfile", input)
                }
            }
        }
    })
