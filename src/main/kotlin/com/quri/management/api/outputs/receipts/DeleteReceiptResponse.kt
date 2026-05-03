package com.quri.management.api.outputs.receipts

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.DeleteReceiptOutput
import com.quri.client.model.Receipt

/**
 * Maps the [Receipt] delete result to a client-facing response.
 */
data class DeleteReceiptResponse(@JsonProperty("receiptId") val receiptId: String) {
    companion object {
        fun from(model: DeleteReceiptOutput) =
            DeleteReceiptResponse(
                receiptId = model.receiptId,
            )
    }
}
