package com.quri.management.db.mongo.collections

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.quri.client.model.Fee
import com.quri.client.model.PaymentMethod
import com.quri.management.config.IntegrationTest
import com.quri.management.db.mongo.MongoSchema.Collections
import com.quri.management.db.mongo.documents.ReceiptDocument
import com.quri.management.fixtures.models.ReceiptFixtures
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.time.Instant

@Suppress("unused")
class ReceiptCollectionTest : IntegrationTest() {

    @Autowired
    lateinit var receiptCollection: ReceiptCollection

    @Autowired
    lateinit var dataStoreDatabase: MongoDatabase

    init {
        afterEach {
            dataStoreDatabase
                .getCollection(Collections.RECEIPTS, ReceiptDocument::class.java)
                .drop()
        }

        describe("findById") {

            context("when a receipt exists for the given ID") {
                it("returns the matching receipt") {
                    val input = ReceiptFixtures.aCreateReceiptInput()
                    val created = receiptCollection.create(input, "owner-1")!!

                    val result = receiptCollection.findById(ObjectId(created.id))

                    result shouldNotBe null
                    result!!.id shouldBe created.id
                }
            }

            context("when no receipt exists for the given ID") {
                it("returns null") {
                    val result = receiptCollection.findById(ObjectId())
                    result shouldBe null
                }
            }
        }

        describe("create") {

            context("when input is valid") {
                it("persists and returns the receipt with a generated ID") {
                    val input = ReceiptFixtures.aCreateReceiptInput()

                    val result = receiptCollection.create(input, "owner-1")

                    assertSoftly(result!!) {
                        it.id shouldNotBe null
                        it.vendorName shouldBe "Test Vendor"
                        it.items shouldHaveSize 1
                        it.items shouldContain ReceiptFixtures.anItem()
                        it.occurredAt shouldBe Instant.parse("2024-01-01T00:00:00Z")
                        it.paymentMethod shouldBe PaymentMethod.CREDIT
                        it.subtotal shouldBe ReceiptFixtures.aMonetaryAmount()
                        it.createdBy shouldBe "owner-1"
                        it.updatedBy shouldBe "owner-1"
                    }
                }

                it("assigns distinct IDs to separate documents") {
                    val first = receiptCollection.create(ReceiptFixtures.aCreateReceiptInput(), "owner-1")
                    val second = receiptCollection.create(ReceiptFixtures.aCreateReceiptInput(), "owner-1")

                    first!!.id shouldNotBe second!!.id
                }

                it("round-trips nested liable and discount fields through a fresh fetch") {
                    val input = ReceiptFixtures.aCreateReceiptInput()
                    val created = receiptCollection.create(input, "owner-1")!!

                    val fetched = receiptCollection.findById(ObjectId(created.id))!!

                    assertSoftly(fetched) {
                        it.items shouldHaveSize 1
                        it.items[0].liable shouldHaveSize 1
                        it.items[0].liable shouldContain ReceiptFixtures.aLiable()
                        it.items[0].discounts shouldHaveSize 2
                        it.items[0].discounts shouldContain ReceiptFixtures.anAmountDiscount()
                        it.items[0].discounts shouldContain ReceiptFixtures.aRateDiscount()
                    }
                }
            }
        }

        describe("listAll") {

            context("when no receipts exist") {
                it("returns empty list and null token") {
                    val (results, token) = receiptCollection.listAll(10, null)

                    results shouldBe emptyList()
                    token shouldBe null
                }
            }

            context("when receipts exist within page size") {
                it("returns all receipts and null token") {
                    repeat(3) { receiptCollection.create(ReceiptFixtures.aCreateReceiptInput(), "owner-1") }

                    val (results, token) = receiptCollection.listAll(10, null)

                    results shouldHaveSize 3
                    token shouldBe null
                }
            }

            context("when receipts exceed page size") {
                it("returns pageSize results and a pagination token") {
                    repeat(5) { receiptCollection.create(ReceiptFixtures.aCreateReceiptInput(), "owner-1") }

                    val (results, token) = receiptCollection.listAll(3, null)

                    results shouldHaveSize 3
                    token shouldNotBe null
                }
            }

            context("when nextToken is provided") {
                it("returns the next page starting after the token") {
                    repeat(5) { receiptCollection.create(ReceiptFixtures.aCreateReceiptInput(), "owner-1") }

                    val (firstPage, token) = receiptCollection.listAll(3, null)
                    val (secondPage, nextToken) = receiptCollection.listAll(3, token)

                    firstPage shouldHaveSize 3
                    secondPage shouldHaveSize 2
                    nextToken shouldBe null
                }
            }
        }

        describe("deleteById") {

            context("when the receipt exists") {
                it("removes the document and returns the ObjectId") {
                    val created = receiptCollection.create(ReceiptFixtures.aCreateReceiptInput(), "owner-1")!!
                    val objectId = ObjectId(created.id)

                    val result = receiptCollection.deleteById(objectId)

                    result shouldBe objectId
                    receiptCollection.findById(objectId) shouldBe null
                }
            }

            context("when the receipt does not exist") {
                it("returns null") {
                    val result = receiptCollection.deleteById(ObjectId())
                    result shouldBe null
                }
            }
        }

        describe("update via PUT") {

            context("when the receipt exists") {
                it("updates specified fields and returns the updated receipt") {
                    val created = receiptCollection.create(ReceiptFixtures.aCreateReceiptInput(), "owner-1")!!
                    val input = ReceiptFixtures.anUpdateReceiptInput(
                        id = created.id,
                        vendorName = "Updated Test Vendor",
                        items = listOf(ReceiptFixtures.anItem()),
                        occurredAt = Instant.parse("2024-01-01T00:00:00Z"),
                        paymentMethod = PaymentMethod.CASH,
                        subtotal = ReceiptFixtures.aMonetaryAmount(),
                        tax = BigDecimal("6.90"),
                        tip = BigDecimal("6.70"),
                        totalSavings = ReceiptFixtures.aMonetaryAmount(),
                        fees = listOf(ReceiptFixtures.aFlatFee(), ReceiptFixtures.aPercentageFee()),
                        address = ReceiptFixtures.aValidAddress(),
                        photoId = "https://updated.photo.id",
                        urls = listOf("https://url.com"),
                    )

                    val result = receiptCollection.update(input, "user-1")

                    assertSoftly(result!!) {
                        it.id shouldBe created.id
                        it.vendorName shouldBe "Updated Test Vendor"
                        it.items shouldHaveSize 1
                        it.items shouldContain ReceiptFixtures.anItem()
                        it.occurredAt shouldNotBe null
                        it.paymentMethod shouldBe PaymentMethod.CASH
                        it.subtotal shouldBe ReceiptFixtures.aMonetaryAmount()
                        it.tax shouldBe BigDecimal("6.90")
                        it.tip shouldBe BigDecimal("6.70")
                        it.totalSavings shouldBe ReceiptFixtures.aMonetaryAmount()
                        it.fees shouldHaveSize 2
                        it.fees shouldContain ReceiptFixtures.aFlatFee()
                        it.fees shouldContain ReceiptFixtures.aPercentageFee()
                        it.address shouldBe ReceiptFixtures.aValidAddress()
                        it.photoId shouldBe "https://updated.photo.id"
                        it.urls shouldContain "https://url.com"
                        it.updatedBy shouldBe "user-1"
                        it.updatedAt shouldNotBe null
                    }
                }

                it("omits optional fields in address") {
                    val created = receiptCollection.create(ReceiptFixtures.aCreateReceiptInput(), "owner-1")!!
                    val input = ReceiptFixtures.anUpdateReceiptInput(
                        id = created.id,
                        vendorName = created.vendorName,
                        items = created.items,
                        occurredAt = created.occurredAt,
                        paymentMethod = created.paymentMethod,
                        subtotal = created.subtotal,
                        address = ReceiptFixtures.aMinimalAddress(),
                    )

                    val result = receiptCollection.update(input, "user-1")

                    assertSoftly(result!!.address!!) {
                        it.street shouldBe "123 Main Street"
                        it.unit shouldBe null
                        it.rawInput shouldBe null
                        it.formatted shouldBe null
                    }
                }

                it("omits all optional fields") {
                    val created = receiptCollection.create(
                        ReceiptFixtures.aCreateReceiptInput(vendorName = "Original Vendor Name"),
                        "owner-1",
                    )!!
                    val input = ReceiptFixtures.anUpdateReceiptInput(
                        id = created.id,
                        vendorName = "Updated Vendor Name",
                        items = created.items,
                        occurredAt = created.occurredAt,
                        paymentMethod = created.paymentMethod,
                        subtotal = created.subtotal,
                    )

                    val result = receiptCollection.update(input, "user-1")

                    assertSoftly(result!!) {
                        it.id shouldBe created.id
                        it.vendorName shouldBe "Updated Vendor Name"
                        it.items shouldContain ReceiptFixtures.anItem()
                        it.occurredAt shouldNotBe null
                        it.paymentMethod shouldBe PaymentMethod.CREDIT
                        it.subtotal shouldBe ReceiptFixtures.aMonetaryAmount()
                        it.tax shouldBe null
                        it.tip shouldBe null
                        it.totalSavings shouldBe null
                        it.fees shouldBe emptyList<Fee>()
                        it.address shouldBe null
                        it.photoId shouldBe null
                        it.urls shouldBe emptyList<String>()
                        it.updatedBy shouldBe "user-1"
                        it.updatedAt shouldNotBe null
                    }
                }
            }

            context("when the receipt does not exist") {
                it("returns null") {
                    val input = ReceiptFixtures.anUpdateReceiptInput(id = ObjectId().toString())
                    val result = receiptCollection.update(input, "user-1")
                    result shouldBe null
                }
            }
        }
    }
}
