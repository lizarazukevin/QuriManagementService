package com.quri.management.api.handlers.receipt

import com.quri.client.model.UpdateReceiptInput
import com.quri.client.model.UpdateReceiptOutput
import com.quri.management.api.security.identity.UserIdentity
import com.quri.management.services.ReceiptService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the receipt update operation.
 */
@RestController
@RequestMapping("/receipts/{id}")
class UpdateReceipt(private val receiptService: ReceiptService, private val userIdentity: UserIdentity) {
    @PutMapping
    suspend fun updateReceipt(@PathVariable id: String, @RequestBody input: UpdateReceiptInput): UpdateReceiptOutput {
        val input = input.toBuilder()
            .id(id)
            .build()

        val receipt = receiptService.updateReceipt(input, userIdentity.userId())

        return UpdateReceiptOutput.builder()
            .receipt(receipt)
            .build()
    }
}
