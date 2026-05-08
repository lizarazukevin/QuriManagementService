package com.quri.management.services

import com.quri.client.model.Bill
import com.quri.client.model.CreateBillInput
import com.quri.client.model.DeleteBillInput
import com.quri.client.model.GetBillInput
import com.quri.client.model.InternalFailureException
import com.quri.client.model.ResourceNotFoundException
import com.quri.client.model.UpdateBillInput
import com.quri.management.api.validation.bill.CreateBillValidator
import com.quri.management.api.validation.bill.UpdateBillValidator
import com.quri.management.db.mongo.collections.BillCollection
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

/**
 * Business logic layer for bill operations.
 */
@Service
class BillService(
    private val billCollection: BillCollection,
    private val createBillValidator: CreateBillValidator,
    private val updateBillValidator: UpdateBillValidator,
) {
    /**
     * Retrieves a bill by its ID.
     *
     * @param input contains the bill ID to look up
     * @return the matching [Bill]
     * @throws ResourceNotFoundException if no bill exists with the ID provided
     */
    suspend fun getBillFromId(input: GetBillInput): Bill =
        billCollection.findById(ObjectId(input.billId))
            ?: throw ResourceNotFoundException.builder()
                .message("Bill with ID `${input.billId}` not found")
                .build()

    /**
     * Creates a new bill.
     *
     * @param input minimal to create empty bill
     * @param ownerId the owning entity
     * @return the persisted [Bill] with db-generated ID
     * @throws InternalFailureException if the insert did not return a generated ID
     */
    suspend fun createBill(
        input: CreateBillInput,
        ownerId: String,
    ): Bill {
        createBillValidator.validate("createBill", input)
        return billCollection.create(input, ownerId)
            ?: throw InternalFailureException.builder()
                .message("Failed to create bill")
                .build()
    }

    /**
     * Returns a paginated list of bills.
     *
     * @param pageSize is the maximum results per page
     * @param nextToken is the bookmarked ID the next paginated list starts from
     * @return list of all [Bill] records and nullable pagination token
     */
    suspend fun listBills(
        pageSize: Int,
        nextToken: String?,
    ): Pair<List<Bill>, String?> = billCollection.listAll(pageSize, nextToken)

    /**
     * Deletes a bill by its ID.
     *
     * @param input contains the bill ID to delete
     * @return the deleted bill ID
     * @throws ResourceNotFoundException if no bill exists with the ID provided
     */
    suspend fun deleteBill(input: DeleteBillInput): String =
        billCollection.deleteById(ObjectId(input.billId))?.toString()
            ?: throw ResourceNotFoundException.builder()
                .message("Bill with ID `${input.billId}` not found")
                .build()

    /**
     * Updates a bill with user changes.
     *
     * @param input contents to update bill
     * @param userId actor behind update
     * @return [Bill] after update
     * @throws ResourceNotFoundException if no bill exists with the ID provided
     */
    suspend fun updateBill(
        input: UpdateBillInput,
        userId: String,
    ): Bill {
        updateBillValidator.validate("updateBill", input)
        return billCollection.update(input, userId)
            ?: throw ResourceNotFoundException.builder()
                .message("Bill with ID `${input.billId}` not found")
                .build()
    }
}
