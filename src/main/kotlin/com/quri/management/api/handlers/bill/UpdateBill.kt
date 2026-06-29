package com.quri.management.api.handlers.bill

import com.quri.client.model.UpdateBillInput
import com.quri.client.model.UpdateBillOutput
import com.quri.management.api.security.identity.UserIdentity
import com.quri.management.services.BillService
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the bill update operation.
 */
@RestController
@RequestMapping("/bills/{id}")
class UpdateBill(private val billService: BillService, private val userIdentity: UserIdentity) {
    @PatchMapping
    suspend fun updateBill(@PathVariable id: String, @RequestBody input: UpdateBillInput): UpdateBillOutput {
        val input = input.toBuilder()
            .id(id)
            .build()

        val bill = billService.updateBill(input, userIdentity.userId())

        return UpdateBillOutput.builder()
            .bill(bill)
            .build()
    }
}
