package com.quri.management.api.inputs.bills

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.BillStatus
import com.quri.client.model.CreateBillInput

/**
 * Maps this request to a Smithy [CreateBillInput].
 */
data class CreateBillInputRequest(
    @JsonProperty("name") val name: String,

    @JsonProperty("status") val status: String? = null,
    @JsonProperty("hidden") val hidden: Boolean? = null,
    @JsonProperty("description") val description: String? = null,
) {
    init {
        require(
            name.isNotBlank() &&
            name.length in MIN_BILL_NAME_LENGTH..MAX_BILL_NAME_LENGTH,
        ) { "name must be $MIN_BILL_NAME_LENGTH-$MAX_BILL_NAME_LENGTH characters" }

        status?.let { BillStatus.from(it) }
        description?.let {
            require(
                it.isNotBlank() && it.length <= MAX_DESCRIPTION_LENGTH,
            ) { "description exceeds $MAX_DESCRIPTION_LENGTH characters" }
        }
    }

    fun toSmithyInput(): CreateBillInput =
        CreateBillInput.builder()
            .name(name)
            .status(status?.let { BillStatus.from(it) } ?: BillStatus.DRAFT)
            .hidden(hidden ?: false)
            .description(description)
            .build()

    companion object {
        private const val MIN_BILL_NAME_LENGTH = 1
        private const val MAX_BILL_NAME_LENGTH = 32
        private const val MAX_DESCRIPTION_LENGTH = 150
    }
}
