package com.quri.management.api.validation.bill

import com.quri.management.api.validation.validateLength
import com.quri.management.api.validation.validateObjectIdList

object BillValidation {
    const val MIN_NAME_LENGTH = 1
    const val MAX_NAME_LENGTH = 32
    const val MIN_DESCRIPTION_LENGTH = 1
    const val MAX_DESCRIPTION_LENGTH = 150

    fun validateName(
        field: String,
        name: String,
    ) = name.validateLength("$field.name", MIN_NAME_LENGTH, MAX_NAME_LENGTH)

    fun validateDescription(
        field: String,
        description: String,
    ) = description.validateLength("$field.description", MIN_DESCRIPTION_LENGTH, MAX_DESCRIPTION_LENGTH)

    fun validateReceiptIdList(
        field: String,
        receipts: List<String>,
    ) = receipts.validateObjectIdList("$field.receipts")
}
