package com.quri.management.handlers.bills

import com.quri.management.services.BillService
import com.quri.server.model.DeleteBillInput
import com.quri.server.model.DeleteBillOutput
import com.quri.server.service.DeleteBillOperation
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import software.amazon.smithy.java.server.RequestContext

/**
 * Handles the [DeleteBill] operation.
 *
 * TODO: Migrate to DeleteBillOperationAsync once Kotlin server codegen is available.
 * runBlocking is a temporary workaround — it blocks the Smithy worker thread.
 *
 * @see BillService.deleteBill
 */
@Component
class DeleteBill(
    private val billService: BillService
) : DeleteBillOperation {
    override fun deleteBill(input: DeleteBillInput, context: RequestContext?): DeleteBillOutput {
        val deletedBillId = runBlocking {
            billService.deleteBill(input)
        }
        return DeleteBillOutput.builder()
            .billId(deletedBillId)
            .build()
    }
}