package com.quri.management.api.handlers.receipt

import com.quri.client.model.CreateReceiptInput
import com.quri.client.model.CreateReceiptOutput
import com.quri.management.api.security.identity.UserIdentity
import com.quri.management.services.ReceiptService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the receipt creation operation.
 */
@RestController
@RequestMapping("/receipts")
class CreateReceipt(private val receiptService: ReceiptService, private val userIdentity: UserIdentity) {
    @PostMapping
    suspend fun createReceipt(@RequestBody input: CreateReceiptInput): CreateReceiptOutput {
        val receipt = receiptService.createReceipt(input, userIdentity.userId())

        return CreateReceiptOutput.builder()
            .receipt(receipt)
            .build()
    }
}
