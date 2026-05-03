package com.quri.management.shared.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import com.quri.client.model.MonetaryAmount as SmithyMonetaryAmount

/**
 * Maps to monetary value, extendable to global currencies.
 *
 * [BigDecimal] is used over [Double] or [Float] to avoid floating-point
 * precision errors. Never use floating-point types for monetary values.
 *
 * @see SmithyMonetaryAmount
 */
data class MonetaryAmount(
    @JsonProperty("amount") val amount: BigDecimal,
    @JsonProperty("currency") val currency: String,
) {
    init {
        require(currency.matches(Regex("^[A-Z]{3}$"))) { "currency must be a valid ISO 4217 code e.g. USD" }
    }

    fun toSmithyModel(): SmithyMonetaryAmount =
        SmithyMonetaryAmount.builder()
            .amount(amount)
            .currency(currency)
            .build()

    companion object {
        fun from(model: SmithyMonetaryAmount) =
            MonetaryAmount(
                amount = model.amount,
                currency = model.currency,
            )
    }
}
