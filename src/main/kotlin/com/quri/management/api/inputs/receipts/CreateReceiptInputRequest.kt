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
        require(
            vendorName.isNotBlank() &&
                vendorName.length in MIN_VENDOR_NAME_LENGTH..MAX_VENDOR_NAME_LENGTH,
        ) { "vendorName must be $MIN_VENDOR_NAME_LENGTH-$MAX_VENDOR_NAME_LENGTH characters" }
        require(items.isNotEmpty()) { "items must not be empty" }
        require(occurredAt <= Instant.now()) { "occurredAt cannot be in the future" }
        PaymentMethod.from(paymentMethod)

        tax?.let { require(it in BigDecimal.ZERO..BigDecimal.ONE) { "tax must be between 0 and 1" } }
        tip?.let { require(it in BigDecimal.ZERO..BigDecimal.ONE) { "tip must be between 0 and 1" } }
        photoId?.let { require(it.isNotBlank()) { "photoId cannot be blank" } }
        urls?.let { list ->
            require(list.isNotEmpty()) { "urls must not be empty" }
            require(
                list.all { it.isNotBlank() && it.length in MIN_URL_LENGTH..MAX_URL_LENGTH },
            ) { "url member must be $MIN_URL_LENGTH-$MAX_URL_LENGTH characters" }
        }
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

    companion object {
        private const val MIN_VENDOR_NAME_LENGTH = 3
        private const val MAX_VENDOR_NAME_LENGTH = 150
        private const val MIN_URL_LENGTH = 1
        private const val MAX_URL_LENGTH = 2048
    }
}
