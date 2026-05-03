package com.quri.management.db.mongo.documents

import com.quri.client.model.PaymentMethod
import com.quri.client.model.Receipt
import com.quri.management.shared.models.Address
import com.quri.management.shared.models.Fee
import com.quri.management.shared.models.Item
import com.quri.management.shared.models.MonetaryAmount
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
    val fees: List<Fee>? = null,
    val address: Address? = null,
    val photoId: String? = null,
    val urls: List<String>? = null,

    val createdBy: String,
    val createdAt: Instant,
    val updatedBy: String,
    val updatedAt: Instant,
) {
    fun toSmithyModel(): Receipt =
        Receipt.builder()
            .id(id.toString())
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
            .createdBy(createdBy)
            .createdAt(createdAt)
            .updatedBy(updatedBy)
            .updatedAt(updatedAt)
            .build()
}
