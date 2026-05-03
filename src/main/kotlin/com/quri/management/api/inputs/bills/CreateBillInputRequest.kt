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
        require(name.isNotBlank()) { "name must not be blank" }
        status?.let {
            require(it == BillStatus.DRAFT.value || it == BillStatus.PUBLISHED.value) {
                "status on creation must be DRAFT or PUBLISHED"
            }
        }
    }

    fun toSmithyInput(): CreateBillInput =
        CreateBillInput.builder()
            .name(name)
            .status(BillStatus.from(status) ?: BillStatus.DRAFT)
            .hidden(hidden ?: false)
            .description(description)
            .build()
}
