package com.quri.management.db.mongo.documents

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.Instant

/**
 * Persistence document for a bill in MongoDB.
 *
 * @param total combined total of all items in the bill
 * @param balance remaining amount to be paid
 */
data class BillDocument(
    @BsonId val id: ObjectId = ObjectId(),
    val total: MonetaryAmountDocument,
    val balance: MonetaryAmountDocument,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
