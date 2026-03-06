package com.quri.management.services

import com.quri.management.db.mongo.collections.BillCollection
import com.quri.server.model.Bill
import com.quri.server.model.CreateBillInput
import com.quri.server.model.DeleteBillInput
import com.quri.server.model.GetBillInput
import com.quri.server.model.InternalError
import com.quri.server.model.ResourceNotFoundException
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

/**
 * Business logic layer for bill operations.
 */
@Service
class BillService(
    private val billCollection: BillCollection
) {
    /**
     * Retrieves a bill by its ID.
     *
     * @param input contains the bill ID to look up
     * @return the matching [Bill]
     * @throws ResourceNotFoundException if no bill exists with the given ID
     */
    suspend fun getBill(input: GetBillInput): Bill =
        billCollection.findById(ObjectId(input.billId))
            ?: throw ResourceNotFoundException.builder()
                .message("Bill with ID '${input.billId}' not found")
                .build()

    /**
     * Creates a new bill.
     *
     * @param input contains the total and balance for the new bill
     * @return the persisted [Bill] with its generated ID
     * @throws InternalError if the insert did not return a generated ID
     */
    suspend fun createBill(input: CreateBillInput): Bill =
        billCollection.create(input)
            ?: throw InternalError.builder()
                .message("Failed to create bill")
                .build()

    /**
     * Returns all bills.
     *
     * @return list of all [Bill] records
     */
    suspend fun listBills(): List<Bill> = billCollection.findAll()

    /**
     * Deletes a bill by its ID.
     *
     * @param input contains the bill ID to delete
     * @return the deleted bill ID as a [String]
     * @throws ResourceNotFoundException if no bill exists with the given ID
     */
    suspend fun deleteBill(input: DeleteBillInput): String {
        return billCollection.deleteById(ObjectId(input.billId))?.toString()
            ?: throw ResourceNotFoundException.builder()
                .message("Bill with ID '${input.billId}' not found")
                .build()
    }
}