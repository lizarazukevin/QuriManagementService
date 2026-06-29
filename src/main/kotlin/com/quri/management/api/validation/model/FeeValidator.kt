package com.quri.management.api.validation.model

import com.quri.client.model.Fee
import com.quri.management.api.validation.Validator
import com.quri.management.api.validation.require
import com.quri.management.api.validation.validateLength
import com.quri.management.api.validation.validateRate
import org.springframework.stereotype.Component

@Component
class FeeValidator(private val monetaryAmountValidator: MonetaryAmountValidator) : Validator<Fee> {
    override suspend fun validate(field: String, input: Fee) {
        input.name.validateLength("$field.name", MIN_FEE_NAME_LENGTH, MAX_FEE_NAME_LENGTH)
        input.value?.let { monetaryAmountValidator.validate("$field.value", it) }
        input.rate?.validateRate("$field.rate")

        require(input.value != null || input.rate != null) {
            "fee must have either amount or rate"
        }
        require(input.value == null || input.rate == null) {
            "fee cannot have both amount and rate"
        }
    }

    companion object {
        private const val MIN_FEE_NAME_LENGTH = 3
        private const val MAX_FEE_NAME_LENGTH = 50
    }
}
