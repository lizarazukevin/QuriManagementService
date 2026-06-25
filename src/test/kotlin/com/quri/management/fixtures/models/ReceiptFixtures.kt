package com.quri.management.fixtures.models

import com.quri.client.model.Address
import com.quri.client.model.CreateReceiptInput
import com.quri.client.model.DeleteReceiptInput
import com.quri.client.model.Discount
import com.quri.client.model.DiscountType
import com.quri.client.model.Fee
import com.quri.client.model.GetReceiptInput
import com.quri.client.model.Item
import com.quri.client.model.Liable
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

    fun aLiable(
        userId: String = DEFAULT_USER_ID,
        rate: BigDecimal = BigDecimal("1.0"),
    ): Liable =
        Liable.builder()
            .userId(userId)
            .rate(rate)
            .build()

    fun anAmountDiscount(
        category: DiscountType = DiscountType.PROMO,
        value: MonetaryAmount = aMonetaryAmount(amount = BigDecimal("1.00")),
    ): Discount =
        Discount.builder()
            .category(category)
            .value(value)
            .build()

    fun aRateDiscount(
        category: DiscountType = DiscountType.PROMO,
        rate: BigDecimal = BigDecimal("0.1"),
    ): Discount =
        Discount.builder()
            .category(category)
            .rate(rate)
            .build()

    fun aFlatFee(
        name: String = "Service Fee",
        value: MonetaryAmount = aMonetaryAmount(),
    ): Fee =
        Fee.builder()
            .name(name)
            .value(value)
            .build()

    fun aPercentageFee(
        name: String = "Service Fee",
        rate: BigDecimal = BigDecimal("0.05"),
    ): Fee =
        Fee.builder()
            .name(name)
            .rate(rate)
            .build()

    fun anItem(
        name: String = "Test Item",
        units: Int = 1,
        unitCost: MonetaryAmount = aMonetaryAmount(),
        liable: List<Liable>? = listOf(aLiable()),
        discounts: List<Discount>? = listOf(anAmountDiscount(), aRateDiscount()),
    ): Item =
        Item.builder()
            .name(name)
            .units(units)
            .unitCost(unitCost)
            .liable(liable)
            .discounts(discounts)
            .build()

    fun aValidAddress(
        street: String = "123 Main Street",
        city: String = "Arlington",
        state: String = "VA",
        postalCode: String = "20001",
        country: String = "US",
        unit: String? = "Apt 4B",
        rawInput: String? = "123 main st apt 4b arlington va 20001",
        formatted: String? = "123 Main Street, Apt 4B, Arlington, VA 20001, US",
    ): Address =
        Address.builder()
            .street(street)
            .city(city)
            .state(state)
            .postalCode(postalCode)
            .country(country)
            .apply { unit?.let { unit(it) } }
            .apply { rawInput?.let { rawInput(it) } }
            .apply { formatted?.let { formatted(it) } }
            .build()

    fun aMinimalAddress(
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

    @Suppress("CyclomaticComplexMethod")
    fun aReceipt(
        id: String = DEFAULT_RECEIPT_ID,
        vendorName: String = "Test Vendor",
        items: List<Item> = listOf(anItem()),
        occurredAt: Instant = Instant.parse("2024-01-01T00:00:00Z"),
        paymentMethod: PaymentMethod = PaymentMethod.CREDIT,
        subtotal: MonetaryAmount = aMonetaryAmount(),
        tax: BigDecimal = BigDecimal("0.06"),
        tip: BigDecimal = BigDecimal("0.15"),
        totalSavings: MonetaryAmount = aMonetaryAmount(amount = BigDecimal("2.00")),
        fees: List<Fee> = listOf(aFlatFee(), aPercentageFee()),
        address: Address = aValidAddress(),
        photoId: String = "photo-1",
        urls: List<String> = listOf("https://example.com/receipt.jpg"),
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
            .tax(tax)
            .tip(tip)
            .totalSavings(totalSavings)
            .fees(fees)
            .address(address)
            .photoId(photoId)
            .urls(urls)
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

    fun aGetReceiptInput(id: String = DEFAULT_RECEIPT_ID): GetReceiptInput =
        GetReceiptInput.builder()
            .id(id)
            .build()

    fun aDeleteReceiptInput(id: String = DEFAULT_RECEIPT_ID): DeleteReceiptInput =
        DeleteReceiptInput.builder()
            .id(id)
            .build()

    @Suppress("CyclomaticComplexMethod") // fixture builder: complexity is mechanical field mapping, not logic
    fun anUpdateReceiptInput(
        id: String = DEFAULT_RECEIPT_ID,
        vendorName: String? = null,
        items: List<Item>? = null,
        occurredAt: Instant? = null,
        paymentMethod: PaymentMethod? = null,
        subtotal: MonetaryAmount? = null,
        tax: BigDecimal? = null,
        tip: BigDecimal? = null,
        totalSavings: MonetaryAmount? = null,
        fees: List<Fee>? = null,
        address: Address? = null,
        photoId: String? = null,
        urls: List<String>? = null,
    ): UpdateReceiptInput =
        UpdateReceiptInput.builder()
            .id(id)
            .apply { vendorName?.let { vendorName(it) } }
            .apply { items?.let { items(it) } }
            .apply { occurredAt?.let { occurredAt(it) } }
            .apply { paymentMethod?.let { paymentMethod(it) } }
            .apply { subtotal?.let { subtotal(it) } }
            .apply { tax?.let { tax(it) } }
            .apply { tip?.let { tip(it) } }
            .apply { totalSavings?.let { totalSavings(it) } }
            .apply { fees?.let { fees(it) } }
            .apply { address?.let { address(it) } }
            .apply { photoId?.let { photoId(it) } }
            .apply { urls?.let { urls(it) } }
            .build()
}
