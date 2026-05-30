package com.quri.management.api.validation.profile

import com.quri.client.model.CreateProfileInput
import com.quri.management.api.validation.Validator
import com.quri.management.db.mongo.collections.ProfileCollection
import org.springframework.stereotype.Component

@Component
class CreateProfileValidator(
    private val profileCollection: ProfileCollection,
    private val profileFieldsValidator: ProfileFieldsValidator,
) : Validator<CreateProfileInput> {
    override suspend fun validate(
        field: String,
        input: CreateProfileInput,
    ) {
        profileFieldsValidator.validate(
            field = field,
            username = input.username,
            firstName = input.firstName,
            lastName = input.lastName,
            email = input.email,
            dateOfBirth = input.dateOfBirth,
            middleName = input.middleName,
            phoneNumber = input.phoneNumber,
        )
    }
}
