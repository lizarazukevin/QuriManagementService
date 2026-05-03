package com.quri.management.api.outputs.bills

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.Bill
import com.quri.client.model.ListBillsOutput

/**
 * Maps a paginated list of Smithy [Bill] models
 */
data class ListBillsResponse(
    @JsonProperty("bills") val bills: List<BillResponse>,
    @JsonProperty("nextToken") val nextToken: String? = null,
) {
    companion object {
        fun from(model: ListBillsOutput) =
            ListBillsResponse(
                bills = model.bills.map { BillResponse.from(it) },
                nextToken = model.nextToken,
            )
    }
}
