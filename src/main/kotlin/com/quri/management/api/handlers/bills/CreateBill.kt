package com.quri.management.api.handlers.bills

import com.quri.client.model.CreateBillOutput
import com.quri.management.api.inputs.bills.CreateBillInputRequest
import com.quri.management.api.outputs.bills.BillResponse
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
    suspend fun createBill(@RequestBody request: CreateBillInputRequest): BillResponse {
        val input = request.toSmithyInput()

        val created = billService.createBill(input, userIdentity.userId())

        val output = CreateBillOutput.builder()
            .bill(created)
            .build()

        return BillResponse.from(output)
    }
}
