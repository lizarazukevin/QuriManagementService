package com.quri.management.fixtures.documents

import com.quri.client.model.Address
import com.quri.client.model.Fee
import com.quri.client.model.Item
import com.quri.client.model.MonetaryAmount
import com.quri.management.db.mongo.documents.ReceiptDocument
import com.quri.management.fixtures.models.ReceiptFixtures
import com.quri.management.fixtures.models.ReceiptFixtures.DEFAULT_OWNER_ID
import com.quri.management.fixtures.models.ReceiptFixtures.DEFAULT_USER_ID
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.Instant

object ReceiptDocumentFixtures {

    val DEFAULT_ID = ObjectId()
    val DEFAULT_INSTANT: Instant = Instant.parse("2024-01-01T00:00:00Z")

    fun aReceiptDocument(
        id: ObjectId = DEFAULT_ID,
        vendorName: String = "Test Vendor",
        items: List<Item> = listOf(ReceiptFixtures.anItem()),
        occurredAt: Instant = DEFAULT_INSTANT,
        paymentMethod: String = "CREDIT",
        subtotal: MonetaryAmount = ReceiptFixtures.aMonetaryAmount(),
        tax: BigDecimal? = null,
        tip: BigDecimal? = null,
        totalSavings: MonetaryAmount? = null,
        fees: List<Fee>? = emptyList(),
        address: Address? = null,
        photoId: String? = null,
        urls: List<String> = emptyList(),
        createdBy: String = DEFAULT_OWNER_ID,
        createdAt: Instant = DEFAULT_INSTANT,
        updatedBy: String = DEFAULT_USER_ID,
        updatedAt: Instant = DEFAULT_INSTANT,
    ): ReceiptDocument = ReceiptDocument(
        id = id,
        vendorName = vendorName,
        items = items,
        occurredAt = occurredAt,
        paymentMethod = paymentMethod,
        subtotal = subtotal,
        tax = tax,
        tip = tip,
        totalSavings = totalSavings,
        fees = fees,
        address = address,
        photoId = photoId,
        urls = urls,
        createdBy = createdBy,
        createdAt = createdAt,
        updatedBy = updatedBy,
        updatedAt = updatedAt,
    )
}
