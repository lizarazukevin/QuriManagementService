package com.quri.management.api.outputs.receipts

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.ListReceiptsOutput
import com.quri.client.model.Receipt

/**
 * Maps a paginated list of Smithy [Receipt] models to a client-facing response.
 */
data class ListReceiptsResponse(
    @JsonProperty("receipts") val receipts: List<ReceiptResponse>,
    @JsonProperty("nextToken") val nextToken: String? = null,
) {
    companion object {
        fun from(model: ListReceiptsOutput) =
            ListReceiptsResponse(
                receipts = model.receipts.map { ReceiptResponse.from(it) },
                nextToken = model.nextToken,
            )
    }
}
