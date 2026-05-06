package com.quri.management.api.handlers.bills

import com.quri.client.model.ListBillsInput
import com.quri.client.model.ListBillsOutput
import com.quri.management.services.BillService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the list bills operation.
 */
@RestController
@RequestMapping("/bills")
class ListBills(private val billService: BillService) {
    @GetMapping
    suspend fun listBills(
        @RequestParam maxResults: Int?,
        @RequestParam nextToken: String?,
    ): ListBillsOutput {
        val pageSize = (maxResults ?: DEFAULT_BILLS_PAGE_SIZE).coerceIn(1, MAX_BILLS_PAGE_SIZE)
        val input = ListBillsInput.builder()
            .maxResults(pageSize)
            .nextToken(nextToken)
            .build()

        val (bills, newToken) = billService.listBills(
            pageSize = input.maxResults,
            nextToken = input.nextToken,
        )

        return ListBillsOutput.builder()
            .bills(bills)
            .nextToken(newToken)
            .build()
    }

    companion object {
        private const val DEFAULT_BILLS_PAGE_SIZE = 20
        private const val MAX_BILLS_PAGE_SIZE = 100
    }
}
