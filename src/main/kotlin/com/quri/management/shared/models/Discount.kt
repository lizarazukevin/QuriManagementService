package com.quri.management.shared.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.DiscountType
import java.math.BigDecimal
import com.quri.client.model.Discount as SmithyDiscount

/**
 * Maps to a unit discount for an item on a receipt.
 *
 * @see SmithyDiscount
 */
data class Discount(
    @JsonProperty("type") val type: String,
    @JsonProperty("value") val value: MonetaryAmount? = null,
    @JsonProperty("rate") val rate: BigDecimal? = null,
) {
    init {
        DiscountType.from(type)

        require(value != null || rate != null) { "discount must have either amount or rate" }
        require(value == null || rate == null) { "discount cannot have both amount and rate" }
        value?.let { require(it.amount >= BigDecimal.ZERO) { "discount amount must be greater than zero" } }
        rate?.let { require(it in BigDecimal.ZERO..BigDecimal.ONE) { "discount rate must be between 0 and 1" } }
    }

    fun toSmithyModel(): SmithyDiscount =
        SmithyDiscount.builder()
            .typeMember(DiscountType.from(type))
            .value(value?.toSmithyModel())
            .rate(rate)
            .build()

    companion object {
        fun from(model: SmithyDiscount) =
            Discount(
                type = model.type.value,
                value = model.value?.let { MonetaryAmount.from(it) },
                rate = model.rate,
            )
    }
}
