package com.quri.management.api.validation.model

import com.quri.client.model.Address
import com.quri.management.api.validation.Validator
import org.springframework.stereotype.Component

@Component
class AddressValidator(private val addressFieldsValidator: AddressFieldsValidator) : Validator<Address> {
    override suspend fun validate(field: String, input: Address) {
        addressFieldsValidator.validate(
            field = field,
            street = input.street,
            city = input.city,
            state = input.state,
            postalCode = input.postalCode,
            country = input.country,
            unit = input.unit,
            rawInput = input.rawInput,
            formatted = input.formatted,
        )
    }
}
