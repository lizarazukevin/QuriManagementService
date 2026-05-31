package com.quri.management.api.validators.model

import com.quri.client.model.ValidationException
import com.quri.management.api.validation.model.AddressFieldsValidator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec

@Suppress("unused")
class AddressFieldsValidatorTest :
    DescribeSpec({

        val validator = AddressFieldsValidator()

        describe("validate") {

            context("when all fields are null") {
                it("passes — all fields are optional") {
                    validator.validate("field")
                }
            }

            context("postalCode") {
                it("passes for valid US zip code") {
                    validator.validate("field", postalCode = "20001")
                }

                it("passes for valid zip+4") {
                    validator.validate("field", postalCode = "20001-1234")
                }

                it("throws ValidationException for invalid format") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", postalCode = "2000")
                    }
                }
            }

            context("country") {
                it("passes for valid ISO 3166 alpha-2 code") {
                    validator.validate("field", country = "US")
                }

                it("throws ValidationException for lowercase code") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", country = "us")
                    }
                }

                it("throws ValidationException for 3 letter code") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", country = "USA")
                    }
                }
            }

            context("street") {
                it("throws ValidationException when below min length") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", street = "")
                    }
                }
            }

            context("city") {
                it("throws ValidationException when below min length") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", city = "")
                    }
                }
            }

            context("state") {
                it("throws ValidationException when below min length") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", state = "")
                    }
                }
            }

            context("unit") {
                it("throws ValidationException when below min length") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", unit = "")
                    }
                }
            }

            context("rawInput") {
                it("throws ValidationException when below min length") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", rawInput = "")
                    }
                }
            }

            context("formatted") {
                it("throws ValidationException when below min length") {
                    shouldThrow<ValidationException> {
                        validator.validate("field", formatted = "")
                    }
                }
            }
        }
    })
