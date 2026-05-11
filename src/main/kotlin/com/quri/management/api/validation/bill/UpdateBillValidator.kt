package com.quri.management.api.validation.bill

import com.quri.client.model.UpdateBillInput
import com.quri.management.api.validation.Validator
import com.quri.management.api.validation.bill.BillValidation.validateDescription
import com.quri.management.api.validation.bill.BillValidation.validateName
import com.quri.management.api.validation.bill.BillValidation.validateReceiptIdList
import com.quri.management.api.validation.model.MonetaryAmountValidator
import org.springframework.stereotype.Component

@Component
class UpdateBillValidator(private val monetaryAmountValidator: MonetaryAmountValidator) : Validator<UpdateBillInput> {
    override suspend fun validate(
        field: String,
        input: UpdateBillInput,
    ) {
        input.name?.let { validateName(field, input.name) }
        input.description?.let { validateDescription(field, input.description) }
        input.balance?.let { monetaryAmountValidator.validate(field, input.balance) }
        input.receipts?.let { validateReceiptIdList(field, input.receipts) }
    }
}
