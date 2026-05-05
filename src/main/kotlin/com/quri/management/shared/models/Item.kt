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
    @JsonProperty("discounts") val discounts: List<Discount>? = emptyList(),
) {
    init {
        require(
            name.isNotBlank() &&
            name.length in MIN_ITEM_NAME_LENGTH..MAX_ITEM_NAME_LENGTH,
        ) { "item name must $MIN_ITEM_NAME_LENGTH-$MAX_ITEM_NAME_LENGTH characters" }
        require(units >= MIN_UNIT_COUNT) { "units must be at least $MIN_UNIT_COUNT" }
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
        private const val MIN_ITEM_NAME_LENGTH = 1
        private const val MAX_ITEM_NAME_LENGTH = 150
        private const val MIN_UNIT_COUNT = 1

        fun from(model: SmithyItem) =
            Item(
                name = model.name,
                units = model.units,
                unitCost = MonetaryAmount.from(model.unitCost),
                liable = model.liable?.map { Liable.from(it) },
                discounts = model.discounts?.map { Discount.from(it) },
            )
    }
}
