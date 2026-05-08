package com.quri.management.api.handlers.bills

import com.quri.client.model.UpdateBillInput
import com.quri.client.model.UpdateBillOutput
import com.quri.management.api.security.identity.UserIdentity
import com.quri.management.services.BillService
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the bill update operation.
 */
@RestController
@RequestMapping("/bills")
class UpdateBill(private val billService: BillService, private val userIdentity: UserIdentity) {
    @PatchMapping
    suspend fun updateBill(@RequestBody input: UpdateBillInput): UpdateBillOutput {
        val bill = billService.updateBill(input, userIdentity.userId())

        return UpdateBillOutput.builder()
            .bill(bill)
            .build()
    }
}
