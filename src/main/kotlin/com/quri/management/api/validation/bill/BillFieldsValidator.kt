package com.quri.management.api.validation.bill

import com.quri.client.model.MonetaryAmount
import com.quri.management.api.validation.model.MonetaryAmountValidator
import com.quri.management.api.validation.validateLength
import com.quri.management.api.validation.validateObjectIdList
import org.springframework.stereotype.Component

@Component
class BillFieldsValidator(private val monetaryAmountValidator: MonetaryAmountValidator) {
    suspend fun validate(
        field: String,
        name: String? = null,
        description: String? = null,
        balance: MonetaryAmount? = null,
        receipts: List<String>? = null,
    ) {
        name?.validateLength("$field.name", MIN_NAME_LENGTH, MAX_NAME_LENGTH)
        description?.validateLength("$field.description", MIN_DESCRIPTION_LENGTH, MAX_DESCRIPTION_LENGTH)
        balance?.let { monetaryAmountValidator.validate("$field.balance", it) }
        receipts?.validateObjectIdList("$field.receipts")
    }

    companion object {
        private const val MIN_NAME_LENGTH = 1
        private const val MAX_NAME_LENGTH = 32
        private const val MIN_DESCRIPTION_LENGTH = 1
        private const val MAX_DESCRIPTION_LENGTH = 150
    }
}
