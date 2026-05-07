package com.quri.management.api.validation.profile

import com.quri.client.model.CreateProfileInput
import com.quri.management.api.validation.Validator
import com.quri.management.api.validation.validateLength
import com.quri.management.api.validation.validatePattern
import org.springframework.stereotype.Component

@Component
class CreateProfileValidator : Validator<CreateProfileInput> {
    override fun validate(
        field: String,
        input: CreateProfileInput,
    ) {
        input.username?.validateLength("$field.username", MIN_USERNAME_LENGTH, MAX_USERNAME_LENGTH)
        input.firstName?.validateLength("$field.firstName", MIN_NAME_LENGTH, MAX_NAME_LENGTH)
        input.lastName?.validateLength("$field.lastName", MIN_NAME_LENGTH, MAX_NAME_LENGTH)
        input.email?.validatePattern("$field.email", Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"), "email is invalid")
        input.middleName?.validateLength("$field.middleName", MIN_NAME_LENGTH, MAX_NAME_LENGTH)
        input.phoneNumber?.validatePattern(
            "$field.phoneNumber",
            Regex("^[+0-9()\\-\\s]{10,20}$"),
            "phone number is invalid",
        )
    }

    companion object {
        private const val MIN_USERNAME_LENGTH = 3
        private const val MAX_USERNAME_LENGTH = 30
        private const val MIN_NAME_LENGTH = 1
        private const val MAX_NAME_LENGTH = 50
    }
}
