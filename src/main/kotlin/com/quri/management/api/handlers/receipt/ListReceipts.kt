package com.quri.management.api.handlers.receipt

import com.quri.client.model.ListReceiptsInput
import com.quri.client.model.ListReceiptsOutput
import com.quri.management.services.ReceiptService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the list receipts operation.
 */
@RestController
@RequestMapping("/receipts")
class ListReceipts(private val receiptService: ReceiptService) {
    @GetMapping
    suspend fun listReceipts(
        @RequestParam maxResults: Int?,
        @RequestParam nextToken: String?,
    ): ListReceiptsOutput {
        val pageSize = (maxResults ?: DEFAULT_RECEIPTS_PAGE_SIZE).coerceIn(1, MAX_RECEIPTS_PAGE_SIZE)
        val input = ListReceiptsInput.builder()
            .maxResults(pageSize)
            .nextToken(nextToken)
            .build()

        val (receipts, newToken) = receiptService.listReceipts(
            pageSize = input.maxResults,
            nextToken = input.nextToken,
        )

        return ListReceiptsOutput.builder()
            .receipts(receipts)
            .nextToken(newToken)
            .build()
    }

    companion object {
        private const val DEFAULT_RECEIPTS_PAGE_SIZE = 20
        private const val MAX_RECEIPTS_PAGE_SIZE = 100
    }
}
