package com.quri.management.services

import com.quri.client.model.CreateReceiptInput
import com.quri.client.model.DeleteReceiptInput
import com.quri.client.model.GetReceiptInput
import com.quri.client.model.InternalFailureException
import com.quri.client.model.Receipt
import com.quri.client.model.ResourceNotFoundException
import com.quri.client.model.UpdateReceiptInput
import com.quri.management.api.validation.receipt.CreateReceiptValidator
import com.quri.management.api.validation.receipt.UpdateReceiptValidator
import com.quri.management.db.mongo.collections.ReceiptCollection
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

/**
 * Business logic layer for receipt operations.
 */
@Service
class ReceiptService(
    private val receiptCollection: ReceiptCollection,
    private val createReceiptValidator: CreateReceiptValidator,
    private val updateReceiptValidator: UpdateReceiptValidator,
) {
    /**
     * Retrieves a receipt by its ID.
     *
     * @param input contains the receipt ID to look up
     * @return the matching [Receipt]
     * @throws ResourceNotFoundException if no receipt exists with the ID provided
     */
    suspend fun getReceiptFromId(input: GetReceiptInput): Receipt =
        receiptCollection.findById(ObjectId(input.receiptId))
            ?: throw ResourceNotFoundException.builder()
                .message("Receipt with ID `${input.receiptId}` not found")
                .build()

    /**
     * Creates a new receipt.
     * @param input includes fees, items, and liability authorities
     * @param ownerId the owning entity
     * @return the persisted [Receipt] with db-generated ID
     * @throws InternalFailureException if the insert did not return a generated ID
     */
    suspend fun createReceipt(
        input: CreateReceiptInput,
        ownerId: String,
    ): Receipt {
        createReceiptValidator.validate("createReceipt", input)
        return receiptCollection.create(input, ownerId)
            ?: throw InternalFailureException.builder()
                .message("Failed to create receipt")
                .build()
    }

    /**
     * Returns a paginated list of receipts.
     *
     * @param pageSize is the maximum results per page
     * @param nextToken is the bookmarked ID the next paginated list starts from
     * @return list of all [Receipt] records and nullable pagination token
     */
    suspend fun listReceipts(
        pageSize: Int,
        nextToken: String?,
    ): Pair<List<Receipt>, String?> = receiptCollection.listAll(pageSize, nextToken)

    /**
     * Deletes a receipt by it's ID.
     *
     * @param input contains the receipt ID to delete
     * @return the deleted receipt ID
     * @throws ResourceNotFoundException if no receipt exists with the ID provided
     */
    suspend fun deleteReceipt(input: DeleteReceiptInput): ObjectId =
        receiptCollection.deleteById(ObjectId(input.receiptId))
            ?: throw ResourceNotFoundException.builder()
                .message("Receipt with ID `${input.receiptId}` not found")
                .build()

    /**
     * Updates a receipt with user changes.
     *
     * @param input contents to update receipt
     * @param userId actor behind update
     * @return [Receipt] after update
     * @throws ResourceNotFoundException if no receipt exists with the ID provided
     */
    suspend fun updateReceipt(
        input: UpdateReceiptInput,
        userId: String,
    ): Receipt {
        updateReceiptValidator.validate("updateReceipt", input)
        return receiptCollection.update(input, userId)
            ?: throw ResourceNotFoundException.builder()
                .message("Receipt with ID `${input.receiptId}` not found")
                .build()
    }
}
