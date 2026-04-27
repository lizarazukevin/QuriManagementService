package com.quri.management.handlers.bills

import com.quri.client.model.GetBillInput
import com.quri.client.model.GetBillOutput
import com.quri.management.services.BillService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the [GetBill] operation.
 *
 * @see BillService.getBillFromId
 */
@RestController
@RequestMapping("/bills/{billId}")
class GetBill(private val billService: BillService) {
    @GetMapping
    suspend fun getBill(@PathVariable billId: String): GetBillOutput {
        val input = GetBillInput.builder()
            .billId(billId)
            .build()

        val billFound = billService.getBillFromId(input)

        return GetBillOutput.builder()
            .bill(billFound)
            .build()
    }
}
