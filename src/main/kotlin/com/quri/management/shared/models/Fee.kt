package com.quri.management.shared.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import com.quri.client.model.Fee as SmithyFee

/**
 * Maps to an individual fee attached to a receipt.
 *
 * @see SmithyFee
 */
data class Fee(@JsonProperty("name") val name: String, @JsonProperty("rate") val rate: BigDecimal) {
    init {
        require(name.isNotBlank()) { "fee name must not be blank" }
        require(rate >= BigDecimal.ZERO && rate <= BigDecimal.ONE) { "rate must be between 0 and 1" }
    }

    fun toSmithyModel(): SmithyFee =
        SmithyFee.builder()
            .name(name)
            .rate(rate)
            .build()

    companion object {
        fun from(model: SmithyFee) =
            Fee(
                name = model.name,
                rate = model.rate,
            )
    }
}
