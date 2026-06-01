package com.quri.management.api.handlers.receipt

import com.quri.client.model.GetReceiptInput
import com.quri.client.model.GetReceiptOutput
import com.quri.management.services.ReceiptService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the receipt retrieval operation.
 */
@RestController
@RequestMapping("/receipts/{id}")
class GetReceipt(private val receiptService: ReceiptService) {
    @GetMapping
    suspend fun getReceipt(@PathVariable id: String): GetReceiptOutput {
        val input = GetReceiptInput.builder()
            .id(id)
            .build()

        val receipt = receiptService.getReceiptFromId(input)

        return GetReceiptOutput.builder()
            .receipt(receipt)
            .build()
    }
}
