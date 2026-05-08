package com.quri.management.api.validation.bill

import com.quri.client.model.CreateBillInput
import com.quri.management.api.validation.Validator
import com.quri.management.api.validation.bill.BillValidation.validateDescription
import com.quri.management.api.validation.bill.BillValidation.validateName
import com.quri.management.api.validation.bill.BillValidation.validateStatusOnCreate
import org.springframework.stereotype.Component

@Component
class CreateBillValidator : Validator<CreateBillInput> {
    override suspend fun validate(
        field: String,
        input: CreateBillInput,
    ) {
        validateName(field, input.name)
        validateStatusOnCreate(field, input.status)
        validateDescription(field, input.description)
    }
}
