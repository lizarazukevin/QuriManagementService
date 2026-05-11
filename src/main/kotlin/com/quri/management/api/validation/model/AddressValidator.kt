package com.quri.management.api.validation.model

import com.quri.client.model.Address
import com.quri.management.api.validation.Validator
import com.quri.management.api.validation.model.AddressValidation.validateCity
import com.quri.management.api.validation.model.AddressValidation.validateCountry
import com.quri.management.api.validation.model.AddressValidation.validateFormatted
import com.quri.management.api.validation.model.AddressValidation.validatePostalCode
import com.quri.management.api.validation.model.AddressValidation.validateRawInput
import com.quri.management.api.validation.model.AddressValidation.validateState
import com.quri.management.api.validation.model.AddressValidation.validateStreet
import com.quri.management.api.validation.model.AddressValidation.validateUnit
import org.springframework.stereotype.Component

@Component
class AddressValidator : Validator<Address> {
    override suspend fun validate(
        field: String,
        input: Address,
    ) {
        validateStreet(field, input.street)
        validateCity(field, input.city)
        validateState(field, input.state)
        validatePostalCode(field, input.postalCode)
        validateCountry(field, input.country)
        validateUnit(field, input.unit)
        validateRawInput(field, input.rawInput)
        validateFormatted(field, input.formatted)
    }
}
