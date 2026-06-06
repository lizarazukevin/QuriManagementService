package com.quri.management.db.mongo.collections

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.quri.client.model.BillStatus
import com.quri.management.config.IntegrationTest
import com.quri.management.db.mongo.MongoSchema.Collections
import com.quri.management.db.mongo.documents.BillDocument
import com.quri.management.fixtures.models.BillFixtures
import com.quri.management.fixtures.models.BillFixtures.DEFAULT_BILL_ID
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired

@Suppress("unused")
class BillCollectionTest : IntegrationTest() {

    @Autowired
    lateinit var billCollection: BillCollection

    @Autowired
    lateinit var dataStoreDatabase: MongoDatabase

    init {
        afterEach {
            dataStoreDatabase
                .getCollection(Collections.BILLS, BillDocument::class.java)
                .drop()
        }

        describe("findById") {

            context("when a bill exists for the given ID") {
                it("returns the matching bill") {
                    val input = BillFixtures.aCreateBillInput()
                    val created = billCollection.create(input, "owner-1")!!

                    val result = billCollection.findById(ObjectId(created.id))

                    result shouldNotBe null
                    result!!.id shouldBe created.id
                }
            }

            context("when no bill exists for the given ID") {
                it("returns null") {
                    val result = billCollection.findById(ObjectId())
                    result shouldBe null
                }
            }
        }

        describe("create") {

            context("when input is valid") {
                it("persists and returns the bill with a generated ID") {
                    val input = BillFixtures.aCreateBillInput()

                    val result = billCollection.create(input, "owner-1")

                    assertSoftly(result!!) {
                        it.id shouldNotBe null
                        it.name shouldBe "Test Bill"
                        it.status shouldBe BillStatus.DRAFT
                        it.isHidden shouldBe false
                        it.createdBy shouldBe "owner-1"
                        it.updatedBy shouldBe "owner-1"
                    }
                }

                it("assigns distinct IDs to separate documents") {
                    val first = billCollection.create(BillFixtures.aCreateBillInput(), "owner-1")
                    val second = billCollection.create(BillFixtures.aCreateBillInput(), "owner-1")

                    first!!.id shouldNotBe second!!.id
                }
            }
        }

        describe("listAll") {

            context("when no bills exist") {
                it("returns empty list and null token") {
                    val (results, token) = billCollection.listAll(10, null)

                    results shouldBe emptyList()
                    token shouldBe null
                }
            }

            context("when bills exist within page size") {
                it("returns all bills and null token") {
                    repeat(3) { billCollection.create(BillFixtures.aCreateBillInput(), "owner-1") }

                    val (results, token) = billCollection.listAll(10, null)

                    results shouldHaveSize 3
                    token shouldBe null
                }
            }

            context("when bills exceed page size") {
                it("returns pageSize results and a pagination token") {
                    repeat(5) { billCollection.create(BillFixtures.aCreateBillInput(), "owner-1") }

                    val (results, token) = billCollection.listAll(3, null)

                    results shouldHaveSize 3
                    token shouldNotBe null
                }
            }

            context("when nextToken is provided") {
                it("returns the next page starting after the token") {
                    repeat(5) { billCollection.create(BillFixtures.aCreateBillInput(), "owner-1") }

                    val (firstPage, token) = billCollection.listAll(3, null)
                    val (secondPage, nextToken) = billCollection.listAll(3, token)

                    firstPage shouldHaveSize 3
                    secondPage shouldHaveSize 2
                    nextToken shouldBe null
                }
            }
        }

        describe("deleteById") {

            context("when the bill exists") {
                it("removes the document and returns the ObjectId") {
                    val created = billCollection.create(BillFixtures.aCreateBillInput(), "owner-1")!!
                    val objectId = ObjectId(created.id)

                    val result = billCollection.deleteById(objectId)

                    result shouldBe objectId
                    billCollection.findById(objectId) shouldBe null
                }
            }

            context("when the bill does not exist") {
                it("returns null") {
                    val result = billCollection.deleteById(ObjectId())
                    result shouldBe null
                }
            }
        }

        describe("update") {

            context("when the bill exists") {
                it("updates specified fields and returns the updated bill") {
                    val created = billCollection.create(BillFixtures.aCreateBillInput(), "owner-1")!!
                    val input = BillFixtures.anUpdateBillInput(
                        id = created.id,
                        name = "Updated Name",
                        status = BillStatus.PUBLISHED,
                        hidden = true,
                        description = "Updated Description",
                        balance = BillFixtures.aMonetaryAmount(),
                        receipts = listOf(DEFAULT_BILL_ID),
                    )

                    val result = billCollection.update(input, "user-1")

                    assertSoftly(result!!) {
                        it.id shouldBe created.id
                        it.name shouldBe "Updated Name"
                        it.status shouldBe BillStatus.PUBLISHED
                        it.isHidden shouldBe true
                        it.description shouldBe "Updated Description"
                        it.balance shouldBe BillFixtures.aMonetaryAmount()
                        it.receipts shouldContain DEFAULT_BILL_ID
                        it.updatedBy shouldBe "user-1"
                        it.updatedAt shouldNotBe null
                    }
                }

                it("leaves unspecified fields unchanged") {
                    val created = billCollection.create(
                        BillFixtures.aCreateBillInput(name = "Original Name"),
                        "owner-1",
                    )!!
                    val input = BillFixtures.anUpdateBillInput(id = created.id, hidden = true)

                    val result = billCollection.update(input, "user-1")

                    assertSoftly(result!!) {
                        it.name shouldBe "Original Name"
                        it.isHidden shouldBe true
                    }
                }
            }

            context("when the bill does not exist") {
                it("returns null") {
                    val input = BillFixtures.anUpdateBillInput(id = ObjectId().toString())
                    val result = billCollection.update(input, "user-1")
                    result shouldBe null
                }
            }
        }
    }
}
