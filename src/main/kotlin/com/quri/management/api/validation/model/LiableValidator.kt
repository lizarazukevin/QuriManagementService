package com.quri.management.api.validation.model

import com.quri.client.model.Liable
import com.quri.management.api.validation.Validator
import com.quri.management.api.validation.validateRate
import org.springframework.stereotype.Component

@Component
class LiableValidator : Validator<Liable> {
    override suspend fun validate(
        field: String,
        input: Liable,
    ) {
        input.rate.validateRate("$field.rate")
    }
}
