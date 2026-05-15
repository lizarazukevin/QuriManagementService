package com.quri.management.api.validation.receipt

import com.quri.client.model.Address
import com.quri.client.model.Fee
import com.quri.client.model.Item
import com.quri.client.model.MonetaryAmount
import com.quri.management.api.validation.model.AddressValidator
import com.quri.management.api.validation.model.FeeValidator
import com.quri.management.api.validation.model.ItemValidator
import com.quri.management.api.validation.model.MonetaryAmountValidator
import com.quri.management.api.validation.validateLength
import com.quri.management.api.validation.validateRate
import com.quri.management.api.validation.validateTimestamp
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Instant

@Component
class ReceiptFieldsValidator(
    private val itemValidator: ItemValidator,
    private val monetaryAmountValidator: MonetaryAmountValidator,
    private val feeValidator: FeeValidator,
    private val addressValidator: AddressValidator,
) {
    suspend fun validate(
        field: String,
        vendorName: String? = null,
        items: List<Item>? = null,
        occurredAt: Instant? = null,
        subtotal: MonetaryAmount? = null,
        tax: BigDecimal? = null,
        tip: BigDecimal? = null,
        totalSavings: MonetaryAmount? = null,
        fees: List<Fee>? = null,
        address: Address? = null,
        photoId: String? = null,
        urls: List<String>? = null,
    ) {
        vendorName?.validateLength("$field.vendorName", MIN_VENDOR_NAME_LENGTH, MAX_VENDOR_NAME_LENGTH)
        occurredAt?.validateTimestamp("$field.occurredAt")
        subtotal?.let { monetaryAmountValidator.validate("$field.subtotal", it) }
        tax?.validateRate("$field.tax")
        tip?.validateRate("$field.tip")
        totalSavings?.let { monetaryAmountValidator.validate("$field.totalSavings", it) }
        address?.let { addressValidator.validate("$field.address", it) }
        photoId?.validateLength("$field.photoId", MIN_PHOTO_ID_LENGTH, MAX_PHOTO_ID_LENGTH)

        items?.forEachIndexed { index, item ->
            itemValidator.validate("$field.items[$index]", item)
        }
        fees?.forEachIndexed { index, fee ->
            feeValidator.validate("$field.fees[$index]", fee)
        }
        urls?.forEachIndexed { index, url ->
            url.validateLength("$field.urls[$index]", MIN_URL_LENGTH, MAX_URL_LENGTH)
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
