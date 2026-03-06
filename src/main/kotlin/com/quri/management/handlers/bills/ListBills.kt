package com.quri.management.handlers.bills

import com.quri.management.services.BillService
import com.quri.server.model.ListBillsInput
import com.quri.server.model.ListBillsOutput
import com.quri.server.service.ListBillsOperation
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import software.amazon.smithy.java.server.RequestContext

/**
 * Handles the [ListBills] operation.
 *
 * TODO: Migrate to ListBillsOperationAsync once Kotlin server codegen is available.
 * runBlocking is a temporary workaround — it blocks the Smithy worker thread.
 *
 * @see BillService.listBills
 */
@Component
class ListBills(
    private val billService: BillService
): ListBillsOperation {
    override fun listBills(input: ListBillsInput, context: RequestContext?): ListBillsOutput {
        val billsFound = runBlocking {
            billService.listBills()
        }

        return ListBillsOutput.builder()
            .bills(billsFound)
            .build()
    }
}