package com.quri.management.api.validation.bill

import com.quri.client.model.BillStatus
import com.quri.client.model.CreateBillInput
import com.quri.management.api.validation.Validator
import com.quri.management.api.validation.bill.BillValidation.validateDescription
import com.quri.management.api.validation.bill.BillValidation.validateName
import org.springframework.stereotype.Component

@Component
class CreateBillValidator : Validator<CreateBillInput> {
    override suspend fun validate(
        field: String,
        input: CreateBillInput,
    ) {
        validateName(field, input.name)
        validateDescription(field, input.description)

        require(input.status == BillStatus.DRAFT || input.status == BillStatus.PUBLISHED) {
            "$field.status status on creation must be DRAFT or PUBLISHED"
        }
    }
}
