package com.quri.management.fixtures.models

import com.quri.client.model.Address
import com.quri.client.model.CreateReceiptInput
import com.quri.client.model.DeleteReceiptInput
import com.quri.client.model.Fee
import com.quri.client.model.GetReceiptInput
import com.quri.client.model.Item
import com.quri.client.model.MonetaryAmount
import com.quri.client.model.PaymentMethod
import com.quri.client.model.Receipt
import com.quri.client.model.UpdateReceiptInput
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.Instant

object ReceiptFixtures {

    val DEFAULT_RECEIPT_ID = ObjectId().toString()
    const val DEFAULT_OWNER_ID = "owner-1"
    const val DEFAULT_USER_ID = "user-1"

    fun aMonetaryAmount(
        amount: BigDecimal = BigDecimal("10.00"),
        currency: String = "USD",
    ): MonetaryAmount =
        MonetaryAmount.builder()
            .amount(amount)
            .currency(currency)
            .build()

    fun anItem(
        name: String = "Test Item",
        units: Int = 1,
        unitCost: MonetaryAmount = aMonetaryAmount(),
    ): Item =
        Item.builder()
            .name(name)
            .units(units)
            .unitCost(unitCost)
            .build()

    fun aFee(
        name: String = "Service Fee",
        value: MonetaryAmount = aMonetaryAmount(),
    ): Fee =
        Fee.builder()
            .name(name)
            .value(value)
            .build()

    fun aReceipt(
        id: String = DEFAULT_RECEIPT_ID,
        vendorName: String = "Test Vendor",
        items: List<Item> = listOf(anItem()),
        occurredAt: Instant = Instant.parse("2024-01-01T00:00:00Z"),
        paymentMethod: PaymentMethod = PaymentMethod.CREDIT,
        subtotal: MonetaryAmount = aMonetaryAmount(),
        createdAt: Instant = Instant.parse("2024-01-01T00:00:00Z"),
        createdBy: String = DEFAULT_USER_ID,
        updatedAt: Instant = Instant.parse("2024-01-01T00:00:00Z"),
        updatedBy: String = DEFAULT_USER_ID,
    ): Receipt =
        Receipt.builder()
            .id(id)
            .vendorName(vendorName)
            .items(items)
            .occurredAt(occurredAt)
            .paymentMethod(paymentMethod)
            .subtotal(subtotal)
            .createdAt(createdAt)
            .createdBy(createdBy)
            .updatedAt(updatedAt)
            .updatedBy(updatedBy)
            .build()

    fun aCreateReceiptInput(
        vendorName: String = "Test Vendor",
        items: List<Item> = listOf(anItem()),
        occurredAt: Instant = Instant.parse("2024-01-01T00:00:00Z"),
        paymentMethod: PaymentMethod = PaymentMethod.CREDIT,
        subtotal: MonetaryAmount = aMonetaryAmount(),
    ): CreateReceiptInput =
        CreateReceiptInput.builder()
            .vendorName(vendorName)
            .items(items)
            .occurredAt(occurredAt)
            .paymentMethod(paymentMethod)
            .subtotal(subtotal)
            .build()

    fun aValidAddress(
        street: String = "123 Main Street",
        city: String = "Arlington",
        state: String = "VA",
        postalCode: String = "20001",
        country: String = "US",
    ): Address =
        Address.builder()
            .street(street)
            .city(city)
            .state(state)
            .postalCode(postalCode)
            .country(country)
            .build()

    fun aGetReceiptInput(receiptId: String = DEFAULT_RECEIPT_ID): GetReceiptInput =
        GetReceiptInput.builder()
            .id(receiptId)
            .build()

    fun aDeleteReceiptInput(receiptId: String = DEFAULT_RECEIPT_ID): DeleteReceiptInput =
        DeleteReceiptInput.builder()
            .id(receiptId)
            .build()

    @Suppress("CyclomaticComplexMethod") // fixture builder: complexity is mechanical field mapping, not logic
    fun anUpdateReceiptInput(
        receiptId: String = DEFAULT_RECEIPT_ID,
        vendorName: String? = null,
        items: List<Item>? = null,
        occurredAt: Instant? = null,
        paymentMethod: PaymentMethod? = null,
        subtotal: MonetaryAmount? = null,
        tax: BigDecimal? = null,
        tip: BigDecimal? = null,
        totalSavings: MonetaryAmount? = null,
        fees: List<Fee>? = null,
        photoId: String? = null,
    ): UpdateReceiptInput =
        UpdateReceiptInput.builder()
            .id(receiptId)
            .apply { vendorName?.let { vendorName(it) } }
            .apply { items?.let { items(it) } }
            .apply { occurredAt?.let { occurredAt(it) } }
            .apply { paymentMethod?.let { paymentMethod(it) } }
            .apply { subtotal?.let { subtotal(it) } }
            .apply { tax?.let { tax(it) } }
            .apply { tip?.let { tip(it) } }
            .apply { totalSavings?.let { totalSavings(it) } }
            .apply { fees?.let { fees(it) } }
            .apply { photoId?.let { photoId(it) } }
            .build()
}
