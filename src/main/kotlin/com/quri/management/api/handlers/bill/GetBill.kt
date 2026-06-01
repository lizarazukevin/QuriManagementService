package com.quri.management.api.handlers.bill

import com.quri.client.model.GetBillInput
import com.quri.client.model.GetBillOutput
import com.quri.management.services.BillService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the bill retrieval operation.
 */
@RestController
@RequestMapping("/bills/{id}")
class GetBill(private val billService: BillService) {
    @GetMapping
    suspend fun getBill(@PathVariable id: String): GetBillOutput {
        val input = GetBillInput.builder()
            .id(id)
            .build()

        val bill = billService.getBillFromId(input)

        return GetBillOutput.builder()
            .bill(bill)
            .build()
    }
}
