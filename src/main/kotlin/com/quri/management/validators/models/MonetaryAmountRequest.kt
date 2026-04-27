package com.quri.management.validators.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.MonetaryAmount
import java.math.BigDecimal

/**
 * Maps this request to a Smithy [MonetaryAmount].
 */
data class MonetaryAmountRequest(
    @JsonProperty("amount") val amount: BigDecimal? = null,
    @JsonProperty("currency") val currency: String? = null,
) {
    fun toSmithyModel(): MonetaryAmount =
        MonetaryAmount.builder()
            .amount(amount)
            .currency(currency)
            .build()
}
