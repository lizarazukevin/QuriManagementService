package com.quri.management.api.validation.model

import com.quri.client.model.Discount
import com.quri.client.model.ValidationException
import com.quri.management.api.validation.Validator
import com.quri.management.api.validation.validateRate
import org.springframework.stereotype.Component

@Component
class DiscountValidator(private val monetaryAmountValidator: MonetaryAmountValidator) : Validator<Discount> {
    override suspend fun validate(
        field: String,
        input: Discount,
    ) {
        input.value?.let { monetaryAmountValidator.validate("$field.value", it) }
        input.rate?.validateRate("$field.rate")

        require(input.value != null || input.rate != null) {
            throw ValidationException.builder()
                .message("discount must have either amount or rate")
                .build()
        }

        require(input.value == null || input.rate == null) {
            throw ValidationException.builder()
                .message("discount cannot have both amount and rate")
                .build()
        }
    }
}
