package com.quri.management.db.mongo.documents

import com.quri.client.model.PaymentMethod
import com.quri.management.fixtures.documents.ReceiptDocumentFixtures
import com.quri.management.fixtures.models.ReceiptFixtures
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.Instant

@Suppress("unused")
class ReceiptDocumentTest :
    DescribeSpec({

        describe("toApi") {

            context("when generatedId is provided") {
                it("uses generatedId over the document id") {
                    val document = ReceiptDocumentFixtures.aReceiptDocument()
                    val generatedId = ObjectId().toString()

                    val result = document.toApi(generatedId)

                    result.id shouldBe generatedId
                }
            }

            context("when generatedId is null") {
                it("uses the document id") {
                    val document = ReceiptDocumentFixtures.aReceiptDocument()

                    val result = document.toApi()

                    result.id shouldBe document.id.toString()
                }
            }

            context("maps all required fields correctly") {
                it("produces an api Receipt with matching field values") {
                    val document = ReceiptDocumentFixtures.aReceiptDocument(
                        vendorName = "Test Vendor",
                        paymentMethod = "DEBIT",
                    )

                    val result = document.toApi()

                    assertSoftly(result) {
                        it.vendorName shouldBe "Test Vendor"
                        it.paymentMethod shouldBe PaymentMethod.DEBIT
                        it.createdBy shouldBe document.createdBy
                        it.createdAt shouldBe document.createdAt
                        it.updatedBy shouldBe document.updatedBy
                        it.updatedAt shouldBe document.updatedAt
                    }
                }
            }

            context("optional fields") {
                it("maps tax and tip when present") {
                    val tax = BigDecimal("0.08")
                    val tip = BigDecimal("0.15")
                    val document = ReceiptDocumentFixtures.aReceiptDocument(tax = tax, tip = tip)

                    val result = document.toApi()

                    result.tax shouldBe tax
                    result.tip shouldBe tip
                }

                it("produces null tax and tip when absent") {
                    val document = ReceiptDocumentFixtures.aReceiptDocument(tax = null, tip = null)

                    val result = document.toApi()

                    result.tax shouldBe null
                    result.tip shouldBe null
                }
            }
        }

        describe("from(CreateReceiptInput)") {

            it("maps all input fields and sets audit fields") {
                val input = ReceiptFixtures.aCreateReceiptInput()
                val ownerId = "owner-abc"

                val result = ReceiptDocument.from(input, ownerId)

                assertSoftly(result) {
                    it.vendorName shouldBe "Test Vendor"
                    it.paymentMethod shouldBe PaymentMethod.CREDIT.value
                    it.createdBy shouldBe ownerId
                    it.updatedBy shouldBe ownerId
                    it.createdAt shouldNotBe null
                    it.updatedAt shouldNotBe null
                    it.createdAt shouldBe it.updatedAt
                }
            }
        }

        describe("from(UpdateReceiptInput, original, userId)") {

            it("preserves original id, createdBy, and createdAt") {
                val original = ReceiptDocumentFixtures.aReceiptDocument(
                    createdBy = "original-owner",
                    createdAt = Instant.parse("2024-01-01T00:00:00Z"),
                )
                val input = ReceiptFixtures.anUpdateReceiptInput(
                    vendorName = "Updated Vendor",
                    paymentMethod = PaymentMethod.CREDIT,
                    subtotal = ReceiptFixtures.aMonetaryAmount(),
                    items = listOf(ReceiptFixtures.anItem()),
                    occurredAt = Instant.parse("2024-06-01T00:00:00Z"),
                )
                val userId = "updater-1"

                val result = ReceiptDocument.from(input, original, userId)

                assertSoftly(result) {
                    it.id shouldBe original.id
                    it.createdBy shouldBe "original-owner"
                    it.createdAt shouldBe Instant.parse("2024-01-01T00:00:00Z")
                    it.updatedBy shouldBe userId
                    it.updatedAt shouldNotBe null
                    it.vendorName shouldBe "Updated Vendor"
                }
            }
        }
    })
