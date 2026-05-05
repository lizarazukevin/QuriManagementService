package com.quri.management.shared.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import com.quri.client.model.Fee as SmithyFee

/**
 * Maps to an individual fee attached to a receipt.
 *
 * @see SmithyFee
 */
data class Fee(
    @JsonProperty("name") val name: String,
    @JsonProperty("value") val value: MonetaryAmount? = null,
    @JsonProperty("rate") val rate: BigDecimal? = null,
) {
    init {
        require(
            name.isNotBlank() &&
            name.length in MIN_FEE_NAME_LENGTH..MAX_FEE_NAME_LENGTH
        ) { "fee name must be between $MIN_FEE_NAME_LENGTH and $MAX_FEE_NAME_LENGTH" }

        require(value != null || rate != null) { "fee must have either amount or rate" }
        require(value == null || rate == null) { "fee cannot have both amount and rate" }
        value?.let {
            require(it.amount >= BigDecimal.ZERO) { "fee value must be greater than zero" }
        }
        rate?.let {
            require(it in BigDecimal.ZERO..BigDecimal.ONE) { "fee rate must be between 0 and 1" }
        }
    }

    fun toSmithyModel(): SmithyFee =
        SmithyFee.builder()
            .name(name)
            .value(value?.toSmithyModel())
            .rate(rate)
            .build()

    companion object {
        private const val MIN_FEE_NAME_LENGTH = 3
        private const val MAX_FEE_NAME_LENGTH = 20

        fun from(model: SmithyFee) =
            Fee(
                name = model.name,
                value = model.value?.let { MonetaryAmount.from(it) },
                rate = model.rate,
            )
    }
}
