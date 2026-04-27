package com.quri.management.validators.inputs.bills

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.CreateBillInput
import com.quri.management.validators.models.MonetaryAmountRequest

/**
 * Maps this request to a Smithy [CreateBillInput].
 */
data class CreateBillInputRequest(
    @JsonProperty("total") val total: MonetaryAmountRequest? = null,
    @JsonProperty("balance") val balance: MonetaryAmountRequest? = null,
) {
    fun toSmithyModel(): CreateBillInput =
        CreateBillInput.builder()
            .total(total?.toSmithyModel())
            .balance(balance?.toSmithyModel())
            .build()
}
