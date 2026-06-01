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
@RequestMapping("/bills/{id}")
class DeleteBill(private val billService: BillService) {
    @DeleteMapping
    suspend fun deleteBill(@PathVariable id: String): DeleteBillOutput {
        val input = DeleteBillInput.builder()
            .id(id)
            .build()

        val deletedBillId = billService.deleteBill(input)

        return DeleteBillOutput.builder()
            .id(deletedBillId.toString())
            .build()
    }
}
