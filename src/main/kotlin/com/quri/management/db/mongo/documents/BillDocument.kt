package com.quri.management.db.mongo.documents

import com.quri.client.model.Bill
import com.quri.client.model.BillStatus
import com.quri.management.shared.models.MonetaryAmount
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.Instant

/**
 * Persistence document for a bill in MongoDB.
 *
 * @see Bill
 */
data class BillDocument(
    @BsonId val id: ObjectId = ObjectId(),
    val name: String,
    val status: String,
    val hidden: Boolean,

    val description: String? = null,
    val balance: MonetaryAmount? = null,
    val receipts: List<ObjectId>? = null,

    val createdBy: String,
    val createdAt: Instant,
    val updatedBy: String,
    val updatedAt: Instant,
) {
    fun toSmithyModel(): Bill =
        Bill.builder()
            .id(id.toString())
            .name(name)
            .status(BillStatus.from(status))
            .hidden(hidden)
            .description(description)
            .balance(balance?.toSmithyModel())
            .receipts(receipts?.map { it.toString() })
            .createdBy(createdBy)
            .createdAt(createdAt)
            .updatedBy(updatedBy)
            .updatedAt(updatedAt)
            .build()
}
