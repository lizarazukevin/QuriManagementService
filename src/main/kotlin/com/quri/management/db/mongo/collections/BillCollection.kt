package com.quri.management.db.mongo.collections

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import com.mongodb.client.model.Updates.combine
import com.mongodb.client.model.Updates.set
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.quri.client.model.Bill
import com.quri.client.model.CreateBillInput
import com.quri.client.model.UpdateBillInput
import com.quri.management.db.mongo.MongoSchema.Collections.BILLS
import com.quri.management.db.mongo.documents.BillDocument
import com.quri.management.db.mongo.paginate
import kotlinx.coroutines.flow.firstOrNull
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Data access layer for the bills collection in MongoDB.
 *
 * Handles all CRUD operations against the [BillDocument] collection and maps
 * persistence documents to Smithy-generated [Bill] models at the boundary.
 * No Smithy types leak into the persistence layer — mapping is handled internally
 * via [BillDocument.toApi].
 *
 * @param dataStoreDatabase the MongoDB database instance injected by Spring
 */
@Component
class BillCollection(private val dataStoreDatabase: MongoDatabase) {
    private val collection: MongoCollection<BillDocument> =
        dataStoreDatabase.getCollection(BILLS, BillDocument::class.java)

    /**
     * Finds a single bill by its MongoDB [ObjectId].
     *
     * @param id the [ObjectId] of the bill to retrieve
     * @return the matching [Bill], or `null` if not found
     */
    suspend fun findById(id: ObjectId): Bill? = collection.find(eq("_id", id)).firstOrNull()?.toApi()

    /**
     * Persists a new bill document and returns it alongside its generated ID.
     *
     * @param input the [CreateBillInput] to create an empty bill
     * @param ownerId the owning entity
     * @return the persisted [Bill] with [Bill.id] populated, or `null` if
     * the insert did not return a generated ID
     */
    suspend fun create(
        input: CreateBillInput,
        ownerId: String,
    ): Bill? {
        val doc = BillDocument.from(input, ownerId)
        val result = collection.insertOne(doc)
        val generatedId = result.insertedId?.asObjectId()?.value?.toString() ?: return null
        return doc.toApi(generatedId)
    }

    /**
     * Returns a paginated list of all bills in the collection in ascending order by ID.
     *
     * @param pageSize is the maximum results per page
     * @param nextToken is the bookmarked ID the next paginated list starts from
     * @return list of all [Bill] documents mapped to Smithy models and nullable pagination token
     */
    suspend fun listAll(
        pageSize: Int,
        nextToken: String?,
    ): Pair<List<Bill>, String?> =
        paginate(collection, pageSize, nextToken) { it.id }
            .let { (docs, token) -> docs.map { it.toApi() } to token }

    /**
     * Deletes a bill by its [ObjectId]
     *
     * @param id the [ObjectId] of the bill to delete
     * @return the deleted [ObjectId] if the document was found and removed, `null` otherwise
     */
    suspend fun deleteById(id: ObjectId): ObjectId? {
        val result = collection.deleteOne(eq("_id", id))
        return id.takeIf { result.deletedCount == 1L }
    }

    /**
     * Updates a bill by its [ObjectId].
     *
     * Combines a list of conditional updates to a single update,
     * auditing the actor behind this modification.
     *
     * @param input the [UpdateBillInput] contents for updating a bill
     * @param userId actor behind update
     * @return updated [Bill] document, `null` if update did not go through
     */
    suspend fun update(
        input: UpdateBillInput,
        userId: String,
    ): Bill? {
        val filter = eq("_id", ObjectId(input.id))
        val updates = buildUpdates(input, userId)
        val options = FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        return collection.findOneAndUpdate(filter, combine(updates), options)?.toApi()
    }

    private fun buildUpdates(
        input: UpdateBillInput,
        userId: String,
    ): List<Bson> {
        val updates = mutableListOf<Bson>()

        input.name?.let { updates.add(set("name", it)) }
        input.status?.let { updates.add(set("status", it.value)) }
        input.isHidden?.let { updates.add(set("hidden", it)) }
        input.description?.let { updates.add(set("description", it)) }
        input.balance?.let { updates.add(set("balance", it)) }
        if (input.hasReceipts()) {
            updates.add(set("receipts", input.receipts.map(::ObjectId)))
        }

        updates.add(set("updatedBy", userId))
        updates.add(set("updatedAt", Instant.now()))
        return updates
    }
}
