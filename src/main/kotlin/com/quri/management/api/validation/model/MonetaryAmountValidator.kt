package com.quri.management.api.validation.model

import com.quri.client.model.MonetaryAmount
import com.quri.management.api.validation.Validator
import com.quri.management.api.validation.validatePattern
import org.springframework.stereotype.Component

@Component
class MonetaryAmountValidator : Validator<MonetaryAmount> {
    override suspend fun validate(field: String, input: MonetaryAmount) {
        input.currency.validatePattern(
            "$field.currency",
            Regex("^[A-Z]{3}$"),
            "must be a valid ISO 4217 currency code e.g. USD",
        )
    }
}
