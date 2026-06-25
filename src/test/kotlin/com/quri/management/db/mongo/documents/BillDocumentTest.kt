package com.quri.management.db.mongo.documents

import com.quri.client.model.BillStatus
import com.quri.management.fixtures.documents.BillDocumentFixtures
import com.quri.management.fixtures.documents.BillDocumentFixtures.DEFAULT_INSTANT
import com.quri.management.fixtures.documents.BillDocumentFixtures.DEFAULT_OWNER_ID
import com.quri.management.fixtures.documents.BillDocumentFixtures.DEFAULT_USER_ID
import com.quri.management.fixtures.models.BillFixtures
import com.quri.management.fixtures.models.ReceiptFixtures
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.bson.types.ObjectId

@Suppress("unused")
class BillDocumentTest :
    DescribeSpec({

        describe("toApi") {

            context("id mapping") {
                it("uses generatedId when provided") {
                    val document = BillDocumentFixtures.aBillDocument()
                    val generatedId = ObjectId().toString()

                    val result = document.toApi(generatedId)

                    result.id shouldBe generatedId
                }

                it("falls back to document id when generatedId is null") {
                    val document = BillDocumentFixtures.aBillDocument()

                    val result = document.toApi()

                    result.id shouldBe document.id.toString()
                }
            }

            context("required field mapping") {
                it("maps all required fields correctly") {
                    val document = BillDocumentFixtures.aBillDocument(
                        name = "My Bill",
                        status = "PENDING",
                        hidden = true,
                        createdBy = DEFAULT_OWNER_ID,
                        createdAt = DEFAULT_INSTANT,
                        updatedBy = DEFAULT_USER_ID,
                        updatedAt = DEFAULT_INSTANT,
                    )

                    val result = document.toApi()

                    assertSoftly(result) {
                        it.name shouldBe "My Bill"
                        it.status shouldBe BillStatus.PENDING
                        it.isHidden shouldBe true
                        it.createdBy shouldBe DEFAULT_OWNER_ID
                        it.createdAt shouldBe DEFAULT_INSTANT
                        it.updatedBy shouldBe DEFAULT_USER_ID
                        it.updatedAt shouldBe DEFAULT_INSTANT
                    }
                }

                it("maps every BillStatus value correctly") {
                    BillStatus.values().forEach { status ->
                        val document = BillDocumentFixtures.aBillDocument(status = status.value)
                        document.toApi().status shouldBe status
                    }
                }
            }

            context("optional field mapping") {
                context("when all optional fields are present") {
                    it("maps description, balance, and receipts correctly") {
                        val balance = ReceiptFixtures.aMonetaryAmount()
                        val receiptId = ObjectId()
                        val document = BillDocumentFixtures.aBillDocument(
                            description = "A description",
                            balance = balance,
                            receipts = listOf(receiptId),
                        )

                        val result = document.toApi()

                        assertSoftly(result) {
                            it.description shouldBe "A description"
                            it.balance shouldBe balance
                            it.receipts shouldBe listOf(receiptId.toHexString())
                        }
                    }
                }

                context("when receipts contains multiple entries") {
                    it("maps all ObjectIds to hex strings") {
                        val ids = listOf(ObjectId(), ObjectId(), ObjectId())
                        val document = BillDocumentFixtures.aBillDocument(receipts = ids)

                        val result = document.toApi()

                        result.receipts shouldBe ids.map(ObjectId::toHexString)
                    }
                }

                context("when all optional fields are absent") {
                    it("produces null for description, balance, and receipts") {
                        val document = BillDocumentFixtures.aBillDocument(
                            description = null,
                            balance = null,
                            receipts = null,
                        )

                        val result = document.toApi()

                        assertSoftly(result) {
                            it.description shouldBe null
                            it.balance shouldBe null
                            it.receipts shouldBe emptyList()
                        }
                    }
                }
            }
        }

        describe("from(CreateBillInput)") {

            it("maps all input fields correctly") {
                val input = BillFixtures.aCreateBillInput()
                val ownerId = "owner-abc"

                val result = BillDocument.from(input, ownerId)

                assertSoftly(result) {
                    it.name shouldBe "Test Bill"
                    it.hidden shouldBe false
                    it.status shouldBe input.status.value
                    it.createdBy shouldBe ownerId
                    it.updatedBy shouldBe ownerId
                    it.createdAt shouldBe it.updatedAt
                }
            }
        }
    })
