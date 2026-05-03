package com.quri.management.api.handlers.bills

import com.quri.client.model.DeleteBillInput
import com.quri.client.model.DeleteBillOutput
import com.quri.management.api.outputs.bills.DeleteBillResponse
import com.quri.management.services.BillService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the bill deletion operation.
 */
@RestController
@RequestMapping("/bills/{billId}")
class DeleteBill(private val billService: BillService) {
    @DeleteMapping
    suspend fun deleteBill(@PathVariable billId: String): DeleteBillResponse {
        val input = DeleteBillInput.builder()
            .billId(billId)
            .build()

        val deletedBillId = billService.deleteBill(input)

        val output = DeleteBillOutput.builder()
            .billId(deletedBillId)
            .build()

        return DeleteBillResponse.from(output)
    }
}
