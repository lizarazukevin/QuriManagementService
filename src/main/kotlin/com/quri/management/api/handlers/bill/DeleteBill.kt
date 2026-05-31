package com.quri.management.api.handlers.bill

import com.quri.client.model.DeleteBillInput
import com.quri.client.model.DeleteBillOutput
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
    suspend fun deleteBill(@PathVariable billId: String): DeleteBillOutput {
        val input = DeleteBillInput.builder()
            .billId(billId)
            .build()

        val deletedBillId = billService.deleteBill(input)

        return DeleteBillOutput.builder()
            .billId(deletedBillId.toString())
            .build()
    }
}
