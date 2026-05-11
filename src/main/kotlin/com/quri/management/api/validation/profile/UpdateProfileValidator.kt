package com.quri.management.api.validation.profile

import com.quri.client.model.UpdateProfileInput
import com.quri.management.api.validation.Validator
import com.quri.management.api.validation.model.UserLocationValidator
import com.quri.management.api.validation.profile.ProfileValidation.validateBio
import com.quri.management.api.validation.profile.ProfileValidation.validateEmail
import com.quri.management.api.validation.profile.ProfileValidation.validateName
import com.quri.management.api.validation.profile.ProfileValidation.validatePhoneNumber
import com.quri.management.api.validation.profile.ProfileValidation.validateUserIdList
import com.quri.management.api.validation.profile.ProfileValidation.validateUsername
import org.springframework.stereotype.Component

@Component
class UpdateProfileValidator(private val userLocationValidator: UserLocationValidator) : Validator<UpdateProfileInput> {
    override suspend fun validate(
        field: String,
        input: UpdateProfileInput,
    ) {
        input.username?.let { validateUsername(field, it) }
        input.firstName?.let { validateName("$field.first", it) }
        input.lastName?.let { validateName("$field.last", it) }
        input.email?.let { validateEmail(field, it) }
        input.middleName?.let { validateName("$field.middle", it) }
        input.phoneNumber?.let { validatePhoneNumber(field, it) }
        input.bio?.let { validateBio(field, it) }
        input.following?.let { validateUserIdList("$field.following", it) }
        input.followers?.let { validateUserIdList("$field.followers", it) }
        input.location?.let { userLocationValidator.validate("$field.location", it) }
    }
}
