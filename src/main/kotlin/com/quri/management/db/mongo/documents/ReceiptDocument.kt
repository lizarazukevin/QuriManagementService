package com.quri.management.db.mongo.documents

import com.quri.client.model.Address
import com.quri.client.model.CreateReceiptInput
import com.quri.client.model.Fee
import com.quri.client.model.Item
import com.quri.client.model.MonetaryAmount
import com.quri.client.model.PaymentMethod
import com.quri.client.model.Receipt
import com.quri.client.model.UpdateReceiptInput
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.Instant

/**
 * Persistence document for a receipt object stored in MongoDB.
 *
 * @see Receipt
 */
data class ReceiptDocument(
    @BsonId val id: ObjectId = ObjectId(),
    val vendorName: String,
    val items: List<Item>,
    val occurredAt: Instant,
    val paymentMethod: String,
    val subtotal: MonetaryAmount,

    val tax: BigDecimal? = null,
    val tip: BigDecimal? = null,
    val totalSavings: MonetaryAmount? = null,
    val fees: List<Fee>? = emptyList(),
    val address: Address? = null,
    val photoId: String? = null,
    val urls: List<String>? = emptyList(),

    val createdBy: String,
    val createdAt: Instant,
    val updatedBy: String,
    val updatedAt: Instant,
) {
    fun toApi(generatedId: String? = null): Receipt = Receipt.builder()
        .id(generatedId ?: id.toString())
        .vendorName(vendorName)
        .items(items)
        .occurredAt(occurredAt)
        .paymentMethod(PaymentMethod.from(paymentMethod))
        .subtotal(subtotal)
        .tax(tax)
        .tip(tip)
        .totalSavings(totalSavings)
        .fees(fees)
        .address(address)
        .photoId(photoId)
        .urls(urls)
        .createdBy(createdBy)
        .createdAt(createdAt)
        .updatedBy(updatedBy)
        .updatedAt(updatedAt)
        .build()

    companion object {
        fun from(input: CreateReceiptInput, ownerId: String): ReceiptDocument {
            val now = Instant.now()
            return ReceiptDocument(
                vendorName = input.vendorName,
                items = input.items,
                occurredAt = input.occurredAt,
                paymentMethod = input.paymentMethod.value,
                subtotal = input.subtotal,
                tax = input.tax,
                tip = input.tip,
                totalSavings = input.totalSavings,
                fees = input.fees,
                address = input.address,
                photoId = input.photoId,
                urls = input.urls,
                createdBy = ownerId,
                createdAt = now,
                updatedBy = ownerId,
                updatedAt = now,
            )
        }

        fun from(input: UpdateReceiptInput, original: ReceiptDocument, userId: String): ReceiptDocument {
            val now = Instant.now()
            return ReceiptDocument(
                id = original.id,
                vendorName = input.vendorName,
                items = input.items,
                occurredAt = input.occurredAt,
                paymentMethod = input.paymentMethod.value,
                subtotal = input.subtotal,
                tax = input.tax,
                tip = input.tip,
                totalSavings = input.totalSavings,
                fees = input.fees,
                address = input.address,
                photoId = input.photoId,
                urls = input.urls,
                createdBy = original.createdBy,
                createdAt = original.createdAt,
                updatedBy = userId,
                updatedAt = now,
            )
        }
    }
}
