package com.quri.management.api.handlers.receipts

import com.quri.client.model.DeleteReceiptInput
import com.quri.client.model.DeleteReceiptOutput
import com.quri.management.services.ReceiptService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the receipt deletion operation.
 */
@RestController
@RequestMapping("/receipts/{receiptId}")
class DeleteReceipt(private val receiptService: ReceiptService) {
    @DeleteMapping
    suspend fun deleteReceipt(@PathVariable receiptId: String): DeleteReceiptOutput {
        val input: DeleteReceiptInput = DeleteReceiptInput.builder()
            .receiptId(receiptId)
            .build()

        val deletedReceiptId = receiptService.deleteReceipt(input)

        return DeleteReceiptOutput.builder()
            .receiptId(deletedReceiptId)
            .build()
    }
}
