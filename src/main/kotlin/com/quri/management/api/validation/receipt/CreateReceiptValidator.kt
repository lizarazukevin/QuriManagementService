package com.quri.management.api.validation.receipt

import com.quri.client.model.CreateReceiptInput
import com.quri.management.api.validation.Validator
import com.quri.management.api.validation.model.AddressValidator
import com.quri.management.api.validation.model.FeeValidator
import com.quri.management.api.validation.model.ItemValidator
import com.quri.management.api.validation.model.MonetaryAmountValidator
import com.quri.management.api.validation.validateLength
import com.quri.management.api.validation.validateRate
import com.quri.management.api.validation.validateTimestamp
import org.springframework.stereotype.Component

@Component
class CreateReceiptValidator(
    private val moneyAmountValidator: MonetaryAmountValidator,
    private val feeValidator: FeeValidator,
    private val addressValidator: AddressValidator,
    private val itemValidator: ItemValidator,
) : Validator<CreateReceiptInput> {
    override fun validate(
        field: String,
        input: CreateReceiptInput,
    ) {
        input.vendorName?.validateLength("$field.vendorName", MIN_VENDOR_NAME_LENGTH, MAX_VENDOR_NAME_LENGTH)
        input.occurredAt?.validateTimestamp("$field.occurredAt")
        input.subtotal?.let { moneyAmountValidator.validate("$field.subtotal", it) }
        input.tax?.validateRate("$field.tax")
        input.tip?.validateRate("$field.tip")
        input.totalSavings?.let { moneyAmountValidator.validate("$field.totalSavings", it) }
        input.address?.let { addressValidator.validate("$field.address", it) }
        input.photoId?.validateLength("$field.photoId", MIN_PHOTO_ID_LENGTH, MAX_PHOTO_ID_LENGTH)

        input.items?.forEachIndexed { index, item ->
            itemValidator.validate("$field.item[$index]", item)
        }
        input.fees?.forEachIndexed { index, fee ->
            feeValidator.validate("$field.item[$index]", fee)
        }

        input.urls?.forEachIndexed { index, url ->
            url.validateLength("urs[$index].url", MIN_URL_LENGTH, MAX_URL_LENGTH)
        }
    }

    companion object {
        private const val MIN_VENDOR_NAME_LENGTH = 3
        private const val MAX_VENDOR_NAME_LENGTH = 150
        private const val MIN_PHOTO_ID_LENGTH = 1
        private const val MAX_PHOTO_ID_LENGTH = 200
        private const val MIN_URL_LENGTH = 1
        private const val MAX_URL_LENGTH = 2048
    }
}
