package com.quri.management.api.handlers.receipts

import com.quri.client.model.GetReceiptInput
import com.quri.client.model.GetReceiptOutput
import com.quri.management.api.outputs.receipts.ReceiptResponse
import com.quri.management.services.ReceiptService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the receipt retrieval operation.
 */
@RestController
@RequestMapping("/receipts/{receiptId}")
class GetReceipt(private val receiptService: ReceiptService) {
    @GetMapping
    suspend fun getReceipt(@PathVariable receiptId: String): ReceiptResponse {
        val input = GetReceiptInput.builder()
            .receiptId(receiptId)
            .build()

        val found = receiptService.getReceiptFromId(input)

        val output = GetReceiptOutput.builder()
            .receipt(found)
            .build()

        return ReceiptResponse.from(output)
    }
}
