package com.quri.management.api.outputs.bills

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.Bill
import com.quri.client.model.DeleteBillOutput

/**
 * Maps the [Bill] delete result to a client-facing response.
 */
data class DeleteBillResponse(@JsonProperty("billId") val billId: String) {
    companion object {
        fun from(model: DeleteBillOutput) =
            DeleteBillResponse(
                billId = model.billId,
            )
    }
}
