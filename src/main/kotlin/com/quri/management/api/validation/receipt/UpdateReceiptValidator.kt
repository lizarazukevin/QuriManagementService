package com.quri.management.api.validation.receipt

import com.quri.client.model.UpdateReceiptInput
import com.quri.management.api.validation.Validator
import org.springframework.stereotype.Component

@Component
class UpdateReceiptValidator(private val receiptFieldsValidator: ReceiptFieldsValidator) :
    Validator<UpdateReceiptInput> {

    override suspend fun validate(field: String, input: UpdateReceiptInput) {
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
