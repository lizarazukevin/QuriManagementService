package com.quri.management.handlers.bills

import com.quri.management.services.BillService
import com.quri.server.model.GetBillInput
import com.quri.server.model.GetBillOutput
import com.quri.server.service.GetBillOperation
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import software.amazon.smithy.java.server.RequestContext

/**
 * Handles the [GetBill] operation.
 *
 * TODO: Migrate to GetBillOperationAsync once Kotlin server codegen is available.
 * runBlocking is a temporary workaround — it blocks the Smithy worker thread.
 *
 * @see BillService.getBill
 */
@Component
class GetBill (
    private val billService: BillService
): GetBillOperation {
    override fun getBill(input: GetBillInput, context: RequestContext?): GetBillOutput {
        val billFound = runBlocking {
            billService.getBill(input)
        }

        return GetBillOutput.builder()
            .bill(billFound)
            .build()
    }
}