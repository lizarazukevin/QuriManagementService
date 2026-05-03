package com.quri.management.api.handlers.receipts

import com.quri.client.model.CreateReceiptOutput
import com.quri.management.api.inputs.receipts.CreateReceiptInputRequest
import com.quri.management.api.outputs.receipts.ReceiptResponse
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
    suspend fun createReceipt(@RequestBody request: CreateReceiptInputRequest): ReceiptResponse {
        val input = request.toSmithyInput()

        val created = receiptService.createReceipt(input, userIdentity.userId())

        val output = CreateReceiptOutput.builder()
            .receipt(created)
            .build()

        return ReceiptResponse.from(output)
    }
}
