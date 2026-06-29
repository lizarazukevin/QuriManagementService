package com.quri.management.api.validation.receipt

import com.quri.client.model.CreateReceiptInput
import com.quri.management.api.validation.Validator
import org.springframework.stereotype.Component

@Component
class CreateReceiptValidator(private val receiptFieldsValidator: ReceiptFieldsValidator) :
    Validator<CreateReceiptInput> {
    override suspend fun validate(field: String, input: CreateReceiptInput) {
        receiptFieldsValidator.validate(
            field = field,
            vendorName = input.vendorName,
            items = input.items,
            occurredAt = input.occurredAt,
            subtotal = input.subtotal,
            tax = input.tax,
            tip = input.tip,
            totalSavings = input.totalSavings,
            fees = input.fees,
            address = input.address,
            photoId = input.photoId,
            urls = input.urls,
        )
    }
}
