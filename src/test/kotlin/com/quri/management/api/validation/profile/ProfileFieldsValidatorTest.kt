package com.quri.management.api.validation.profile

import com.quri.client.model.ValidationException
import com.quri.management.api.validation.model.AddressFieldsValidator
import com.quri.management.api.validation.model.UserLocationValidator
import com.quri.management.fixtures.models.ProfileFixtures
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import org.bson.types.ObjectId
import java.time.Instant

@Suppress("unused")
class ProfileFieldsValidatorTest :
    DescribeSpec({

        val addressFieldsValidator = AddressFieldsValidator()
        val userLocationValidator = UserLocationValidator(addressFieldsValidator)
        val validator = ProfileFieldsValidator(userLocationValidator)

        describe("validate") {

            context("when all fields are null") {
                it("passes, all fields are optional") {
                    validator.validate("field")
                }
            }

            context("username") {
                it("passes at min boundary") {
                    validator.validate("field", username = "abc")
                }

                it("passes at max boundary") {
                    validator.validate("field", username = "a".repeat(30))
                }

                it("throws ValidationException below min length") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", username = "ab")
                    }
                }

                it("throws ValidationException above max length") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", username = "a".repeat(31))
                    }
                }
            }

            context("firstName") {
                it("passes at max boundary") {
                    validator.validate("field", firstName = "a".repeat(50))
                }

                it("throws ValidationException above max length") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", firstName = "a".repeat(51))
                    }
                }

                it("throws ValidationException when empty") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", firstName = "")
                    }
                }
            }

            context("lastName") {
                it("passes at max boundary") {
                    validator.validate("field", lastName = "a".repeat(50))
                }

                it("throws ValidationException above max length") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", lastName = "a".repeat(51))
                    }
                }

                it("throws ValidationException when empty") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", lastName = "")
                    }
                }
            }

            context("email") {
                it("passes for valid email") {
                    validator.validate("field", email = "test@quri.com")
                }

                it("throws ValidationException when missing @") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", email = "testquri.com")
                    }
                }

                it("throws ValidationException when missing domain") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", email = "test@")
                    }
                }

                it("throws ValidationException when contains spaces") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", email = "test @quri.com")
                    }
                }
            }

            context("dateOfBirth") {
                it("passes for a past date") {
                    validator.validate("field", dateOfBirth = Instant.parse("1995-04-15T00:00:00Z"))
                }

                it("throws ValidationException for a future date") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", dateOfBirth = Instant.now().plusSeconds(3600))
                    }
                }
            }

            context("middleName") {
                it("passes at max boundary") {
                    validator.validate("field", middleName = "a".repeat(50))
                }

                it("throws ValidationException above max length") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", middleName = "a".repeat(51))
                    }
                }
            }

            context("phoneNumber") {
                it("passes for valid US number") {
                    validator.validate("field", phoneNumber = "+1 (202) 555-0100")
                }

                it("passes for valid local number") {
                    validator.validate("field", phoneNumber = "2025550100")
                }

                it("throws ValidationException for too short") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", phoneNumber = "123")
                    }
                }

                it("throws ValidationException for too long") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", phoneNumber = "1".repeat(21))
                    }
                }
            }

            context("bio") {
                it("passes at max boundary") {
                    validator.validate("field", bio = "a".repeat(150))
                }

                it("throws ValidationException above max length") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", bio = "a".repeat(151))
                    }
                }

                it("throws ValidationException when empty") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", bio = "")
                    }
                }
            }

            context("following") {
                it("passes for valid ObjectId list") {
                    validator.validate("field", following = listOf(ObjectId().toString()))
                }

                it("throws ValidationException for invalid ObjectId") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", following = listOf("not-an-objectid"))
                    }
                }
            }

            context("followers") {
                it("passes for valid ObjectId list") {
                    validator.validate("field", followers = listOf(ObjectId().toString()))
                }

                it("throws ValidationException for invalid ObjectId") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", followers = listOf("not-an-objectid"))
                    }
                }
            }

            context("location") {
                it("passes for valid location") {
                    validator.validate(
                        "field",
                        location = ProfileFixtures.aUserLocation(),
                    )
                }

                it("throws ValidationException for invalid country code") {
                    shouldThrow<ValidationException> {
                        validator.validate(
                            "field",
                            location = ProfileFixtures.aUserLocation(country = "us"),
                        )
                    }
                }
            }
        }
    })
