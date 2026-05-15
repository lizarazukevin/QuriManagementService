package com.quri.management.api.validation.model

import com.quri.management.api.validation.validateLength
import com.quri.management.api.validation.validatePattern
import org.springframework.stereotype.Component

@Component
class AddressFieldsValidator {

    suspend fun validate(
        field: String,
        street: String? = null,
        city: String? = null,
        state: String? = null,
        postalCode: String? = null,
        country: String? = null,
        unit: String? = null,
        rawInput: String? = null,
        formatted: String? = null,
    ) {
        street?.validateLength("$field.street", MIN_STREET_NAME_LENGTH, MAX_STREET_NAME_LENGTH)
        city?.validateLength("$field.city", MIN_CITY_NAME_LENGTH, MAX_CITY_NAME_LENGTH)
        state?.validateLength("$field.state", MIN_STATE_NAME_LENGTH, MAX_STATE_NAME_LENGTH)
        postalCode?.validatePattern(
            "$field.postalCode",
            Regex("^[0-9]{5}(-[0-9]{4})?$"),
            "must be a valid ISO 3166-1 alpha-2 zip code e.g. 20001",
        )
        country?.validatePattern(
            "$field.country",
            Regex("^[A-Z]{2}$"),
            "must be a valid ISO 3166 alpha-2 country code e.g. US",
        )
        unit?.validateLength("$field.unit", MIN_UNIT_NAME_LENGTH, MAX_UNIT_NAME_LENGTH)
        rawInput?.validateLength("$field.rawInput", MIN_RAW_INPUT_LENGTH, MAX_RAW_INPUT_LENGTH)
        formatted?.validateLength("$field.formatted", MIN_FORMATTED_LENGTH, MAX_FORMATTED_LENGTH)
    }

    companion object {
        private const val MIN_STREET_NAME_LENGTH = 1
        private const val MAX_STREET_NAME_LENGTH = 50
        private const val MIN_CITY_NAME_LENGTH = 1
        private const val MAX_CITY_NAME_LENGTH = 50
        private const val MIN_STATE_NAME_LENGTH = 1
        private const val MAX_STATE_NAME_LENGTH = 50
        private const val MIN_UNIT_NAME_LENGTH = 1
        private const val MAX_UNIT_NAME_LENGTH = 10
        private const val MIN_RAW_INPUT_LENGTH = 1
        private const val MAX_RAW_INPUT_LENGTH = 100
        private const val MIN_FORMATTED_LENGTH = 1
        private const val MAX_FORMATTED_LENGTH = 200
    }
}
