package com.quri.management.api.validation.model

import com.quri.client.model.UserLocation
import com.quri.management.api.validation.Validator
import com.quri.management.api.validation.model.AddressValidation.validateCity
import com.quri.management.api.validation.model.AddressValidation.validateCountry
import com.quri.management.api.validation.model.AddressValidation.validateState
import org.springframework.stereotype.Component

@Component
class UserLocationValidator : Validator<UserLocation> {
    override suspend fun validate(
        field: String,
        input: UserLocation,
    ) {
        validateCity(field, input.city)
        validateState(field, input.state)
        validateCountry(field, input.country)
    }
}
