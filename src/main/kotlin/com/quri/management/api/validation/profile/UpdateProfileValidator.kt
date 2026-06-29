package com.quri.management.api.validation.profile

import com.quri.client.model.UpdateProfileInput
import com.quri.management.api.validation.Validator
import org.springframework.stereotype.Component

@Component
class UpdateProfileValidator(private val profileFieldsValidator: ProfileFieldsValidator) :
    Validator<UpdateProfileInput> {
    override suspend fun validate(field: String, input: UpdateProfileInput) {
        profileFieldsValidator.validate(
            field = field,
            username = input.username,
            firstName = input.firstName,
            lastName = input.lastName,
            email = input.email,
            middleName = input.middleName,
            phoneNumber = input.phoneNumber,
            bio = input.bio,
            following = input.following,
            followers = input.followers,
            location = input.location,
        )
    }
}
