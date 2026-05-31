package com.quri.management.api.validation.bill

import com.quri.client.model.BillStatus
import com.quri.client.model.CreateBillInput
import com.quri.management.api.validation.Validator
import com.quri.management.api.validation.require
import org.springframework.stereotype.Component

@Component
class CreateBillValidator(private val billFieldsValidator: BillFieldsValidator) : Validator<CreateBillInput> {
    override suspend fun validate(
        field: String,
        input: CreateBillInput,
    ) {
        billFieldsValidator.validate(
            field = field,
            name = input.name,
            description = input.description,
        )

        require(input.status == BillStatus.DRAFT || input.status == BillStatus.PUBLISHED) {
            "$field.status status on creation must be DRAFT or PUBLISHED"
        }
    }
}
