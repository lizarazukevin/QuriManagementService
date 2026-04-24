package com.quri.management.handlers.bills

import com.quri.client.model.CreateBillInput
import com.quri.client.model.CreateBillOutput
import com.quri.management.services.BillService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the [CreateBill] operation.
 *
 * @see BillService.createBill
 */
@RestController
@RequestMapping("/bills")
class CreateBill(private val billService: BillService) {
    @PostMapping
    suspend fun createBill(@RequestBody input: CreateBillInput): CreateBillOutput {
        val created = billService.createBill(input)

        return CreateBillOutput.builder()
            .bill(created)
            .build()
    }

    // TODO: Add input validation for [CreateBillInput]
}
