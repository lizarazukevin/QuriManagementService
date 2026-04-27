package com.quri.management.handlers.bills

import com.quri.client.model.CreateBillInput
import com.quri.client.model.CreateBillOutput
import com.quri.management.security.identity.UserIdentity
import com.quri.management.services.BillService
import com.quri.management.validators.inputs.bills.CreateBillInputRequest
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
class CreateBill(private val billService: BillService, private val userIdentity: UserIdentity) {
    @PostMapping
    suspend fun createBill(@RequestBody request: CreateBillInputRequest): CreateBillOutput {
        val input = CreateBillInput.builder()
            .total(request.total?.toSmithyModel())
            .balance(request.balance?.toSmithyModel())
            .build()

        val created = billService.createBill(input, userIdentity.userId())

        return CreateBillOutput.builder()
            .bill(created)
            .build()
    }
}
