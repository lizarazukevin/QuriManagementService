package com.quri.management.api.validation.bill

import com.quri.client.model.BillStatus
import com.quri.client.model.CreateBillInput
import com.quri.management.api.validation.Validator
import com.quri.management.api.validation.require
import com.quri.management.api.validation.validateLength
import org.springframework.stereotype.Component

@Component
class CreateBillValidator : Validator<CreateBillInput> {
    override suspend fun validate(
        field: String,
        input: CreateBillInput,
    ) {
        input.name?.validateLength("$field.name", MIN_BILL_NAME_LENGTH, MAX_BILL_NAME_LENGTH)
        input.status?.let {
            require(it == BillStatus.DRAFT || it == BillStatus.PUBLISHED) {
                "status on bill creation must be DRAFT or PUBLISHED"
            }
        }
        input.description?.validateLength("$field.description", MIN_DESCRIPTION_LENGTH, MAX_DESCRIPTION_LENGTH)
    }

    companion object {
        private const val MIN_BILL_NAME_LENGTH = 1
        private const val MAX_BILL_NAME_LENGTH = 32
        private const val MIN_DESCRIPTION_LENGTH = 1
        private const val MAX_DESCRIPTION_LENGTH = 150
    }
}
