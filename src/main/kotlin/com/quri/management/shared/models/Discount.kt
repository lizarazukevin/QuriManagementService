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
    @JsonProperty("saving") val saving: MonetaryAmount? = null,
    @JsonProperty("rate") val rate: BigDecimal? = null,
) {
    init {
        require(saving != null || rate != null) { "discount must have either amount or rate" }
        require(saving == null || rate == null) { "discount cannot have both amount and rate" }
        rate?.let { require(it >= BigDecimal.ZERO && it <= BigDecimal.ONE) { "discount rate must be between 0 and 1" } }
        saving?.let { require(it.amount >= BigDecimal.ZERO) { "discount amount must be greater than zero" } }
        DiscountType.from(type)
    }

    fun toSmithyModel(): SmithyDiscount =
        SmithyDiscount.builder()
            .typeMember(DiscountType.from(type))
            .saving(saving?.toSmithyModel())
            .rate(rate)
            .build()

    companion object {
        fun from(model: SmithyDiscount) =
            Discount(
                type = model.type.value,
                saving = model.saving?.let { MonetaryAmount.from(it) },
                rate = model.rate,
            )
    }
}
