package com.quri.management.shared.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import com.quri.client.model.Liable as SmithyLiable

/**
 * Contributor's share of an item's total cost.
 *
 * @see SmithyLiable
 */
data class Liable(@JsonProperty("userId") val userId: String, @JsonProperty("rate") val rate: BigDecimal) {
    init {
        require(userId.isNotBlank()) { "userId must not be blank" }
        require(rate >= BigDecimal.ZERO && rate <= BigDecimal.ONE) { "rate must be between 0 and 1" }
    }

    fun toSmithyModel(): SmithyLiable =
        SmithyLiable.builder()
            .userId(userId)
            .rate(rate)
            .build()

    companion object {
        fun from(model: SmithyLiable) =
            Liable(
                userId = model.userId,
                rate = model.rate,
            )
    }
}
