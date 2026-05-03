package com.quri.management.api.inputs.receipts

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.CreateReceiptInput
import com.quri.client.model.PaymentMethod
import com.quri.management.shared.models.Address
import com.quri.management.shared.models.Fee
import com.quri.management.shared.models.Item
import com.quri.management.shared.models.MonetaryAmount
import java.math.BigDecimal
import java.time.Instant

/**
 * Maps this request to a Smithy [CreateReceiptInput]
 */
data class CreateReceiptInputRequest(
    @JsonProperty("vendorName") val vendorName: String,
    @JsonProperty("items") val items: List<Item>,
    @JsonProperty("occurredAt") val occurredAt: Instant,
    @JsonProperty("paymentMethod") val paymentMethod: String,
    @JsonProperty("subtotal") val subtotal: MonetaryAmount,

    @JsonProperty("tax") val tax: BigDecimal? = null,
    @JsonProperty("tip") val tip: BigDecimal? = null,
    @JsonProperty("totalSavings") val totalSavings: MonetaryAmount? = null,
    @JsonProperty("fees") val fees: List<Fee>? = null,
    @JsonProperty("address") val address: Address? = null,
    @JsonProperty("photoId") val photoId: String? = null,
    @JsonProperty("urls") val urls: List<String>? = null,
) {
    init {
        require(vendorName.isNotBlank()) { "vendorName must not be blank" }
        require(items.isNotEmpty()) { "items must not be empty" }
        tax?.let { require(it >= BigDecimal.ZERO && it <= BigDecimal.ONE) { "tax must be between 0 and 1" } }
        tip?.let { require(it >= BigDecimal.ZERO && it <= BigDecimal.ONE) { "tip must be between 0 and 1" } }
        PaymentMethod.from(paymentMethod)
    }

    fun toSmithyInput(): CreateReceiptInput =
        CreateReceiptInput.builder()
            .vendorName(vendorName)
            .items(items.map { it.toSmithyModel() })
            .occurredAt(occurredAt)
            .paymentMethod(PaymentMethod.from(paymentMethod))
            .subtotal(subtotal.toSmithyModel())
            .tax(tax)
            .tip(tip)
            .totalSavings(totalSavings?.toSmithyModel())
            .fees(fees?.map { it.toSmithyModel() })
            .address(address?.toSmithyModel())
            .photoId(photoId)
            .urls(urls)
            .build()
}
