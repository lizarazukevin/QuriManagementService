package com.quri.management.api.handlers.bills

import com.quri.client.model.CreateBillInput
import com.quri.client.model.CreateBillOutput
import com.quri.management.api.security.identity.UserIdentity
import com.quri.management.services.BillService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the bill creation operation.
 */
@RestController
@RequestMapping("/bills")
class CreateBill(private val billService: BillService, private val userIdentity: UserIdentity) {
    @PostMapping
    suspend fun createBill(@RequestBody input: CreateBillInput): CreateBillOutput {
        val bill = billService.createBill(input, userIdentity.userId())

        return CreateBillOutput.builder()
            .bill(bill)
            .build()
    }
}
