package com.quri.management.api.validation.profile

import com.mongodb.client.model.Filters.eq
import com.quri.client.model.CreateProfileInput
import com.quri.client.model.ValidationException
import com.quri.management.api.validation.Validator
import com.quri.management.api.validation.validateLength
import com.quri.management.api.validation.validatePattern
import com.quri.management.db.mongo.collections.ProfileCollection
import com.quri.management.db.mongo.documents.ProfileDocument
import org.springframework.stereotype.Component

@Component
class CreateProfileValidator(private val profileCollection: ProfileCollection) : Validator<CreateProfileInput> {
    override suspend fun validate(
        field: String,
        input: CreateProfileInput,
    ) {
        // Inputs
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

        // Logic
        require(!profileCollection.exists(eq(ProfileDocument::email.name, input.email))) {
            throw ValidationException.builder()
                .message("A profile with email '${input.email}' already exists")
                .build()
        }
    }

    companion object {
        private const val MIN_USERNAME_LENGTH = 3
        private const val MAX_USERNAME_LENGTH = 30
        private const val MIN_NAME_LENGTH = 1
        private const val MAX_NAME_LENGTH = 50
    }
}
