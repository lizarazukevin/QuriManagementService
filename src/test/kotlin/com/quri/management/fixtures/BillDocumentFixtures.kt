package com.quri.management.fixtures

import com.quri.client.model.MonetaryAmount
import com.quri.management.db.mongo.documents.BillDocument
import org.bson.types.ObjectId
import java.time.Instant

object BillDocumentFixtures {

    val DEFAULT_ID = ObjectId()
    const val DEFAULT_OWNER_ID = "owner-1"
    const val DEFAULT_USER_ID = "user-1"
    val DEFAULT_INSTANT: Instant = Instant.parse("2024-01-01T00:00:00Z")

    fun aBillDocument(
        id: ObjectId = DEFAULT_ID,
        name: String = "Test Bill",
        status: String = "DRAFT",
        hidden: Boolean = false,
        description: String? = null,
        balance: MonetaryAmount? = null,
        receipts: List<ObjectId>? = null,
        createdBy: String = DEFAULT_OWNER_ID,
        createdAt: Instant = DEFAULT_INSTANT,
        updatedBy: String = DEFAULT_USER_ID,
        updatedAt: Instant = DEFAULT_INSTANT,
    ): BillDocument =
        BillDocument(
            id = id,
            name = name,
            status = status,
            hidden = hidden,
            description = description,
            balance = balance,
            receipts = receipts,
            createdBy = createdBy,
            createdAt = createdAt,
            updatedBy = updatedBy,
            updatedAt = updatedAt,
        )
}
