package com.quri.management.fixtures.models

import com.quri.client.model.Bill
import com.quri.client.model.BillStatus
import com.quri.client.model.CreateBillInput
import com.quri.client.model.DeleteBillInput
import com.quri.client.model.GetBillInput
import com.quri.client.model.MonetaryAmount
import com.quri.client.model.UpdateBillInput
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.Instant

object BillFixtures {

    val DEFAULT_BILL_ID = ObjectId().toString()
    const val DEFAULT_OWNER_ID = "owner-1"
    const val DEFAULT_USER_ID = "user-1"

    fun aMonetaryAmount(amount: BigDecimal = BigDecimal("10.00"), currency: String = "USD"): MonetaryAmount =
        MonetaryAmount.builder()
            .amount(amount)
            .currency(currency)
            .build()

    fun aBill(
        id: String = DEFAULT_BILL_ID,
        name: String = "Test Bill",
        status: BillStatus = BillStatus.DRAFT,
        hidden: Boolean = false,
        description: String = "Test bill description",
        createdAt: Instant = Instant.parse("2024-01-01T00:00:00Z"),
        createdBy: String = DEFAULT_USER_ID,
        updatedAt: Instant = Instant.parse("2024-01-01T00:00:00Z"),
        updatedBy: String = DEFAULT_USER_ID,
    ): Bill = Bill.builder()
        .id(id)
        .name(name)
        .status(status)
        .hidden(hidden)
        .description(description)
        .createdAt(createdAt)
        .createdBy(createdBy)
        .updatedAt(updatedAt)
        .updatedBy(updatedBy)
        .build()

    fun aCreateBillInput(name: String = "Test Bill"): CreateBillInput = CreateBillInput.builder()
        .name(name)
        .build()

    fun aGetBillInput(id: String = DEFAULT_BILL_ID): GetBillInput = GetBillInput.builder()
        .id(id)
        .build()

    fun aDeleteBillInput(id: String = DEFAULT_BILL_ID): DeleteBillInput = DeleteBillInput.builder()
        .id(id)
        .build()

    fun anUpdateBillInput(
        id: String = DEFAULT_BILL_ID,
        name: String? = null,
        status: BillStatus? = null,
        hidden: Boolean? = null,
        description: String? = null,
        balance: MonetaryAmount? = null,
        receipts: List<String>? = null,
    ): UpdateBillInput = UpdateBillInput.builder()
        .id(id)
        .apply { name?.let { name(it) } }
        .apply { status?.let { status(it) } }
        .apply { hidden?.let { hidden(it) } }
        .apply { description?.let { description(it) } }
        .apply { balance?.let { balance(it) } }
        .apply { receipts?.let { receipts(it) } }
        .build()
}
