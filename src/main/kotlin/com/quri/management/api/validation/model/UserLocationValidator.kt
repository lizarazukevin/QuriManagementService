package com.quri.management.api.validation.model

import com.quri.client.model.UserLocation
import com.quri.management.api.validation.Validator
import org.springframework.stereotype.Component

@Component
class UserLocationValidator(private val addressFieldsValidator: AddressFieldsValidator) : Validator<UserLocation> {
    override suspend fun validate(
        field: String,
        input: UserLocation,
    ) {
        addressFieldsValidator.validate(
            field = field,
            city = input.city,
            state = input.state,
            country = input.country,
        )
    }
}
