package com.quri.management.shared.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.Item as SmithyItem

/**
 * Maps to a single line item in a receipt and tracks its completion.
 *
 * @see SmithyItem
 */
data class Item(
    @JsonProperty("name") val name: String,
    @JsonProperty("units") val units: Int,
    @JsonProperty("unitCost") val unitCost: MonetaryAmount,
    @JsonProperty("liable") val liable: List<Liable>? = emptyList(),
    @JsonProperty("discounts") val discounts: List<Discount>? = null,
) {
    init {
        require(name.isNotBlank()) { "item name must not be blank" }
        require(units >= 1) { "units must be at least 1" }
    }

    fun toSmithyModel(): SmithyItem =
        SmithyItem.builder()
            .name(name)
            .units(units)
            .unitCost(unitCost.toSmithyModel())
            .liable(liable?.map { it.toSmithyModel() })
            .discounts(discounts?.map { it.toSmithyModel() })
            .build()

    companion object {
        fun from(model: SmithyItem) =
            Item(
                name = model.name,
                units = model.units,
                unitCost = MonetaryAmount.from(model.unitCost),
                liable = model.liable.map { Liable.from(it) },
                discounts = model.discounts.map { Discount.from(it) },
            )
    }
}
