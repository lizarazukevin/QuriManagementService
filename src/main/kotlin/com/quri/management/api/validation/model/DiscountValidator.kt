package com.quri.management.api.validation.model

import com.quri.client.model.Discount
import com.quri.management.api.validation.Validator
import com.quri.management.api.validation.require
import com.quri.management.api.validation.validateRate
import org.springframework.stereotype.Component

@Component
class DiscountValidator(private val monetaryAmountValidator: MonetaryAmountValidator) : Validator<Discount> {
    override suspend fun validate(field: String, input: Discount) {
        input.value?.let { monetaryAmountValidator.validate("$field.value", it) }
        input.rate?.validateRate("$field.rate")

        require(input.value != null || input.rate != null) {
            "discount must have either amount or rate"
        }

        require(input.value == null || input.rate == null) {
            "discount cannot have both amount and rate"
        }
    }
}
