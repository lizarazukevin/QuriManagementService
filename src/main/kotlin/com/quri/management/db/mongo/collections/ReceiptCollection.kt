package com.quri.management.db.mongo.collections

import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.quri.client.model.Bill
import com.quri.client.model.CreateReceiptInput
import com.quri.client.model.Receipt
import com.quri.management.db.mongo.MongoSchema.Collections.RECEIPTS
import com.quri.management.db.mongo.documents.ReceiptDocument
import com.quri.management.db.mongo.paginate
import com.quri.management.shared.models.Address
import com.quri.management.shared.models.Fee
import com.quri.management.shared.models.Item
import com.quri.management.shared.models.MonetaryAmount
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Data access layer for the receipts collection in MongoDB.
 *
 * Handles all CRUD operations against the [ReceiptDocument] collection and maps
 * persistence documents to Smithy-generated [Receipt] models at the boundary.
 * No smithy types leak into the persistence layer — mapping is handled internally
 * via [ReceiptDocument.toSmithyModel]
 *
 * All operations rely on the existence of a parent [Bill].
 *
 * @param dataStoreDatabase the MongoDB database instance injected by Spring
 */
@Component
class ReceiptCollection(dataStoreDatabase: MongoDatabase) {
    private val collection: MongoCollection<ReceiptDocument> =
        dataStoreDatabase.getCollection(RECEIPTS, ReceiptDocument::class.java)

    /**
     * Finds a single receipt by its MongoDB [ObjectId].
     *
     * @param id the [ObjectId] of the receipt to retrieve
     * @return the matching [Receipt], or `null` if not found
     */
    suspend fun findById(id: ObjectId): Receipt? =
        collection.find(eq("_id", id))
            .firstOrNull()?.toSmithyModel()

    /**
     * Persists a new receipt document and returns it alongside its generated ID.
     *
     * @param input the [CreateReceiptInput] containing total and balance
     * @param ownerId the owning entity
     * @return the persisted [Receipt] with [Receipt.id] populated, or `null` if
     * the insert did not return a generated ID
     */
    suspend fun create(
        input: CreateReceiptInput,
        ownerId: String,
    ): Receipt? {
        val currTime = Instant.now()
        val doc = ReceiptDocument(
            vendorName = input.vendorName,
            items = input.items.map { Item.from(it) },
            occurredAt = input.occurredAt,
            paymentMethod = input.paymentMethod.value,
            subtotal = input.subtotal.let { MonetaryAmount.from(it) },
            tax = input.tax,
            tip = input.tip,
            totalSavings = input.totalSavings?.let { MonetaryAmount.from(it) },
            fees = input.fees?.map { Fee.from(it) },
            address = input.address?.let { Address.from(it) },
            photoId = input.photoId,
            urls = input.urls,
            createdBy = ownerId,
            createdAt = currTime,
            updatedBy = ownerId,
            updatedAt = currTime,
        )
        val result = collection.insertOne(doc)
        val generatedId = result.insertedId?.asObjectId()?.value?.toString() ?: return null
        return doc.toSmithyModel().toBuilder()
            .id(generatedId)
            .build()
    }

    /**
     * Returns a paginated list of all receipts in the collection in ascending order by ID.
     *
     * @param pageSize is the maximum results per page
     * @param nextToken is the bookmarked ID the next paginated list starts from
     *
     * @return list of all [Receipt] documents mapped to Smithy models and nullable pagination token
     */
    suspend fun listAll(
        pageSize: Int,
        nextToken: String?,
    ): Pair<List<Receipt>, String?> =
        paginate(collection, pageSize, nextToken) { it.id }
            .let { (docs, token) -> docs.map { it.toSmithyModel() } to token }

    /**
     * Deletes a receipt by its [ObjectId]
     *
     * @param id the [ObjectId] of the receipt to delete
     * @return the deleted [ObjectId] if the document was found and removed, `null` otherwise
     */
    suspend fun deleteById(id: ObjectId): ObjectId? {
        val result = collection.deleteOne(eq("_id", id))
        return id.takeIf { result.deletedCount == 1L }
    }
}
