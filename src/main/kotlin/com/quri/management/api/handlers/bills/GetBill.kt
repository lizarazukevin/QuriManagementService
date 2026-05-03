package com.quri.management.api.handlers.bills

import com.quri.client.model.GetBillInput
import com.quri.client.model.GetBillOutput
import com.quri.management.api.outputs.bills.BillResponse
import com.quri.management.services.BillService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the bill retrieval operation.
 */
@RestController
@RequestMapping("/bills/{billId}")
class GetBill(private val billService: BillService) {
    @GetMapping
    suspend fun getBill(@PathVariable billId: String): BillResponse {
        val input = GetBillInput.builder()
            .billId(billId)
            .build()

        val found = billService.getBillFromId(input)

        val output = GetBillOutput.builder()
            .bill(found)
            .build()

        return BillResponse.from(output)
    }
}
