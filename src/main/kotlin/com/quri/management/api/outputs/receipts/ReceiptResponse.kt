package com.quri.management.api.outputs.receipts

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.CreateReceiptOutput
import com.quri.client.model.GetReceiptOutput
import com.quri.client.model.Receipt
import com.quri.management.shared.models.Address
import com.quri.management.shared.models.Fee
import com.quri.management.shared.models.Item
import com.quri.management.shared.models.MonetaryAmount
import java.math.BigDecimal
import java.time.Instant

/**
 * Maps a Smithy [Receipt] to a client-facing response.
 *
 * Owns the response contract independently of Smithy codegen,
 * no internal types leak to the client.
 */
data class ReceiptResponse(
    @JsonProperty("id") val id: String,
    @JsonProperty("vendorName") val vendorName: String,
    @JsonProperty("items") val items: List<Item>,
    @JsonProperty("occurredAt") val occurredAt: Instant,
    @JsonProperty("paymentMethod") val paymentMethod: String,

    @JsonProperty("tax") val tax: BigDecimal? = null,
    @JsonProperty("tip") val tip: BigDecimal? = null,
    @JsonProperty("fees") val fees: List<Fee>? = null,
    @JsonProperty("subtotal") val subtotal: MonetaryAmount? = null,
    @JsonProperty("address") val address: Address? = null,
    @JsonProperty("photoId") val photoId: String? = null,
    @JsonProperty("urls") val urls: List<String>? = null,

    @JsonProperty("createdAt") val createdAt: Instant,
    @JsonProperty("createdBy") val createdBy: String,
    @JsonProperty("updatedAt") val updatedAt: Instant,
    @JsonProperty("updatedBy") val updatedBy: String,
) {
    companion object {
        fun from(model: CreateReceiptOutput) = fromReceipt(model.receipt)
        fun from(model: GetReceiptOutput) = fromReceipt(model.receipt)
        fun from(model: Receipt) = fromReceipt(model)

        private fun fromReceipt(model: Receipt) =
            ReceiptResponse(
                id = model.id,
                vendorName = model.vendorName,
                items = model.items.map { Item.from(it) },
                occurredAt = model.occurredAt,
                paymentMethod = model.paymentMethod.value,
                tax = model.tax,
                tip = model.tip,
                fees = model.fees?.map { Fee.from(it) },
                subtotal = model.subtotal?.let { MonetaryAmount.from(it) },
                address = model.address?.let { Address.from(it) },
                photoId = model.photoId,
                urls = model.urls,
                createdAt = model.createdAt,
                createdBy = model.createdBy,
                updatedAt = model.updatedAt,
                updatedBy = model.updatedBy,
            )
    }
}
