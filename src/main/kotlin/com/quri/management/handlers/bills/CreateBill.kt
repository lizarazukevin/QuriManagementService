package com.quri.management.handlers.bills

import com.quri.management.services.BillService
import com.quri.server.service.CreateBillOperation
import com.quri.server.model.CreateBillInput
import com.quri.server.model.CreateBillOutput
import kotlinx.coroutines.runBlocking
import software.amazon.smithy.java.server.RequestContext
import org.springframework.stereotype.Component

/**
 * Handles the [CreateBill] operation.
 *
 * TODO: Migrate to CreateBillOperationAsync once Kotlin server codegen is available.
 * runBlocking is a temporary workaround — it blocks the Smithy worker thread.
 *
 * @see BillService.createBill
 */
@Component
class CreateBill(
    private val billService: BillService
) : CreateBillOperation {
    override fun createBill(input: CreateBillInput, context: RequestContext?): CreateBillOutput {
        val createdBill = runBlocking {
            billService.createBill(input)
        }
        return CreateBillOutput.builder()
            .bill(createdBill)
            .build()
    }
}