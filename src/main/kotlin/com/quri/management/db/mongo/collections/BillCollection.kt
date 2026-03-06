package com.quri.management.db.mongo.collections

import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.quri.management.db.mongo.MongoSchema.Collections.BILLS
import com.quri.management.db.mongo.documents.BillDocument
import com.quri.management.db.mongo.documents.MonetaryAmountDocument
import com.quri.server.model.Bill
import com.quri.server.model.CreateBillInput
import com.quri.server.model.MonetaryAmount
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

/**
 * Data access layer for the bills collection in MongoDB.
 *
 * Handles all CRUD operations against the [BillDocument] collection and maps
 * persistence documents to Smithy-generated [Bill] models at the boundary.
 * No Smithy types leak into the persistence layer — mapping is handled internally
 * via [BillDocument.toSmithyModel].
 *
 * @param dataStoreDatabase the MongoDB database instance injected by Spring
 */
@Component
class BillCollection(
    dataStoreDatabase: MongoDatabase
) {
    private val collection: MongoCollection<BillDocument> =
        dataStoreDatabase.getCollection(BILLS, BillDocument::class.java)

    /**
     * Finds a single bill by its MongoDB [ObjectId].
     *
     * @param id the [ObjectId] of the bill to retrieve
     * @return the matching [Bill], or `null` if not found
     */
    suspend fun findById(id: ObjectId): Bill? {
        return collection.find(eq("_id", id)).firstOrNull()?.toSmithyModel()
    }

    /**
     * Persists a new bill document and returns it alongside its generated ID.
     *
     * @param input the [CreateBillInput] containing total and balance
     * @return the persisted [Bill] with [Bill.billId] populated, or `null` if
     * the insert did not return a generated ID
     */
    suspend fun create(input: CreateBillInput): Bill? {
        val doc = BillDocument(
            total = MonetaryAmountDocument(input.total.amount, input.total.currencyCode),
            balance = MonetaryAmountDocument(input.balance.amount, input.balance.currencyCode)
        )
        val result = collection.insertOne(doc)
        val generatedId = result.insertedId?.asObjectId()?.value?.toString() ?: return null
        return doc.toSmithyModel().toBuilder()
            .billId(generatedId)
            .createdAt(doc.createdAt)
            .updatedAt(doc.updatedAt)
            .build()
    }

    /**
     * Returns all bills in the collection.
     *
     * @return list of all [Bill] documents mapped to Smithy models
     */
    suspend fun findAll(): List<Bill> {
        return collection.find().toList().map { it.toSmithyModel() }
    }

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

    private fun BillDocument.toSmithyModel(): Bill =
        Bill.builder()
            .billId(id.toString())
            .total(
                MonetaryAmount.builder()
                    .amount(total.amount)
                    .currencyCode(total.currencyCode)
                    .build()
            )
            .balance(
                MonetaryAmount.builder()
                    .amount(balance.amount)
                    .currencyCode(balance.currencyCode)
                    .build()
            )
            .build()
}