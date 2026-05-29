package com.quri.management.fixtures

import com.quri.client.model.Bill
import com.quri.client.model.BillStatus
import com.quri.client.model.CreateBillInput
import com.quri.client.model.DeleteBillInput
import com.quri.client.model.GetBillInput
import com.quri.client.model.MonetaryAmount
import com.quri.client.model.UpdateBillInput
import org.bson.types.ObjectId
import java.time.Instant

object BillFixtures {

    val DEFAULT_BILL_ID = ObjectId().toString()
    const val DEFAULT_OWNER_ID = "owner-1"
    const val DEFAULT_USER_ID = "user-1"

    fun aBill(
        id: String = DEFAULT_BILL_ID,
        name: String = "Test Bill",
        status: BillStatus = BillStatus.DRAFT,
        hidden: Boolean = false,
        description: String? = null,
        balance: MonetaryAmount? = null,
        receipts: List<String>? = null,
        createdAt: Instant = Instant.parse("2024-01-01T00:00:00Z"),
        createdBy: String = DEFAULT_USER_ID,
        updatedAt: Instant = Instant.parse("2024-01-01T00:00:00Z"),
        updatedBy: String = DEFAULT_USER_ID,
    ): Bill =
        Bill.builder()
            .id(id)
            .name(name)
            .status(status)
            .hidden(hidden)
            .createdAt(createdAt)
            .createdBy(createdBy)
            .updatedAt(updatedAt)
            .updatedBy(updatedBy)
            .apply { description?.let { description(it) } }
            .apply { balance?.let { balance(it) } }
            .apply { receipts?.let { receipts(it) } }
            .build()

    fun aCreateBillInput(
        name: String = "Test Bill",
        status: BillStatus = BillStatus.DRAFT,
        hidden: Boolean = false,
        description: String? = null,
    ): CreateBillInput =
        CreateBillInput.builder()
            .name(name)
            .status(status)
            .hidden(hidden)
            .apply { description?.let { description(it) } }
            .build()

    fun aGetBillInput(billId: String = DEFAULT_BILL_ID): GetBillInput =
        GetBillInput.builder()
            .billId(billId)
            .build()

    fun aDeleteBillInput(billId: String = DEFAULT_BILL_ID): DeleteBillInput =
        DeleteBillInput.builder()
            .billId(billId)
            .build()

    fun anUpdateBillInput(
        billId: String = DEFAULT_BILL_ID,
        name: String? = null,
        status: BillStatus? = null,
        hidden: Boolean? = null,
        description: String? = null,
        balance: MonetaryAmount? = null,
        receipts: List<String>? = null,
    ): UpdateBillInput =
        UpdateBillInput.builder()
            .billId(billId)
            .apply { status?.let { status(it) } }
            .apply { name?.let { name(it) } }
            .apply { hidden?.let { hidden(it) } }
            .apply { description?.let { description(it) } }
            .apply { balance?.let { balance(it) } }
            .apply { receipts?.let { receipts(it) } }
            .build()
}
