package com.quri.management.api.validation.model

import com.quri.client.model.Item
import com.quri.management.api.validation.Validator
import com.quri.management.api.validation.validateInteger
import com.quri.management.api.validation.validateLength
import org.springframework.stereotype.Component

@Component
class ItemValidator(
    private val monetaryAmountValidator: MonetaryAmountValidator,
    private val liableValidator: LiableValidator,
    private val discountValidator: DiscountValidator,
) : Validator<Item> {
    override suspend fun validate(
        field: String,
        input: Item,
    ) {
        input.name?.validateLength("$field.name", MIN_ITEM_NAME_LENGTH, MAX_ITEM_NAME_LENGTH)
        input.units.validateInteger("$field.units", MIN_UNIT_COUNT, MAX_UNIT_COUNT)
        input.unitCost?.let { monetaryAmountValidator.validate("$field.unitCost", it) }

        input.liable?.forEachIndexed { index, liable ->
            liableValidator.validate("$field.liable[$index]", liable)
        }

        input.discounts?.forEachIndexed { index, discount ->
            discountValidator.validate("$field.discounts[$index]", discount)
        }
    }

    companion object {
        private const val MIN_ITEM_NAME_LENGTH = 3
        private const val MAX_ITEM_NAME_LENGTH = 100
        private const val MIN_UNIT_COUNT = 1
        private const val MAX_UNIT_COUNT = 1000000000
    }
}
