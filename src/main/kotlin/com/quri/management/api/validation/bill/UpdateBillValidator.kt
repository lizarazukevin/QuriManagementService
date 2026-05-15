package com.quri.management.api.validation.bill

import com.quri.client.model.UpdateBillInput
import com.quri.management.api.validation.Validator
import org.springframework.stereotype.Component

@Component
class UpdateBillValidator(private val billFieldsValidator: BillFieldsValidator) : Validator<UpdateBillInput> {
    override suspend fun validate(
        field: String,
        input: UpdateBillInput,
    ) {
        billFieldsValidator.validate(
            field = field,
            name = input.name,
            description = input.description,
            balance = input.balance,
            receipts = input.receipts,
        )
    }
}
