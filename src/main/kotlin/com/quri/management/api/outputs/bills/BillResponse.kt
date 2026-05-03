package com.quri.management.api.outputs.bills

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.Bill
import com.quri.client.model.CreateBillOutput
import com.quri.client.model.GetBillOutput
import com.quri.management.shared.models.MonetaryAmount
import java.time.Instant

/**
 * Maps a Smithy [Bill] to a client-facing response.
 *
 * Owns the response contract independently of Smithy codegen,
 * no internal types leak to the client.
 */
data class BillResponse(
    @JsonProperty("id") val id: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("status") val status: String,
    @JsonProperty("hidden") val hidden: Boolean,

    @JsonProperty("description") val description: String? = null,
    @JsonProperty("balance") val balance: MonetaryAmount? = null,
    @JsonProperty("receipts") val receipts: List<String>? = null,

    @JsonProperty("createdAt") val createdAt: Instant,
    @JsonProperty("createdBy") val createdBy: String,
    @JsonProperty("updatedAt") val updatedAt: Instant,
    @JsonProperty("updatedBy") val updatedBy: String,
) {
    companion object {
        fun from(model: CreateBillOutput) = fromBill(model.bill)
        fun from(model: GetBillOutput) = fromBill(model.bill)
        fun from(model: Bill) = fromBill(model)

        private fun fromBill(model: Bill) =
            BillResponse(
                id = model.id,
                name = model.name,
                status = model.status.value,
                hidden = model.isHidden,
                description = model.description,
                balance = model.balance?.let { MonetaryAmount.from(it) },
                receipts = model.receipts,
                createdAt = model.createdAt,
                createdBy = model.createdBy,
                updatedAt = model.updatedAt,
                updatedBy = model.updatedBy,
            )
    }
}
