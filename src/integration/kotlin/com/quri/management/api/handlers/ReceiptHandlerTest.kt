package com.quri.management.api.handlers

import com.ninjasquad.springmockk.MockkBean
import com.quri.client.model.Receipt
import com.quri.client.model.ResourceNotFoundException
import com.quri.client.model.ValidationException
import com.quri.management.api.handlers.receipt.CreateReceipt
import com.quri.management.api.handlers.receipt.DeleteReceipt
import com.quri.management.api.handlers.receipt.GetReceipt
import com.quri.management.api.handlers.receipt.ListReceipts
import com.quri.management.api.handlers.receipt.UpdateReceipt
import com.quri.management.api.security.identity.UserIdentity
import com.quri.management.config.HandlerTest
import com.quri.management.fixtures.models.ReceiptFixtures
import com.quri.management.services.ReceiptService
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import org.bson.types.ObjectId
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.http.MediaType

@Suppress("unused")
@WebFluxTest(
    controllers = [
        CreateReceipt::class,
        DeleteReceipt::class,
        GetReceipt::class,
        ListReceipts::class,
        UpdateReceipt::class,
    ],
)
class ReceiptHandlerTest : HandlerTest() {

    @MockkBean
    lateinit var userIdentity: UserIdentity
    @MockkBean
    lateinit var receiptService: ReceiptService

    init {
        beforeEach {
            coEvery { userIdentity.userId() } returns ReceiptFixtures.DEFAULT_USER_ID
        }

        describe("createReceipt") {

            context("when input is valid") {
                it("returns 201 with the created receipt") {
                    coEvery { receiptService.createReceipt(any(), any()) } returns ReceiptFixtures.aReceipt()

                    val result = webTestClient.post()
                        .uri("/receipts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ReceiptFixtures.aCreateReceiptInput())
                        .exchange()
                        .expectStatus().isCreated
                        .expectBody()
                        .returnResult()

                    result.jsonPath("$.receipt.id") shouldBe ReceiptFixtures.DEFAULT_RECEIPT_ID
                    result.jsonPath("$.receipt.vendorName") shouldBe "Test Vendor"
                    result.jsonPath("$.receipt.createdBy") shouldBe ReceiptFixtures.DEFAULT_USER_ID
                }
            }
        }

        describe("getReceipt") {

            context("when receipt exists") {
                it("returns 200 with the receipt") {
                    coEvery { receiptService.getReceiptFromId(any()) } returns ReceiptFixtures.aReceipt()

                    val result = webTestClient.get()
                        .uri("/receipts/${ReceiptFixtures.DEFAULT_RECEIPT_ID}")
                        .exchange()
                        .expectStatus().isOk
                        .expectBody()
                        .returnResult()

                    result.jsonPath("$.receipt.id") shouldBe ReceiptFixtures.DEFAULT_RECEIPT_ID
                    result.jsonPath("$.receipt.vendorName") shouldBe "Test Vendor"
                }
            }
        }

        describe("listReceipts") {

            context("when called with no params") {
                it("returns 200 with default pageSize") {
                    coEvery { receiptService.listReceipts(pageSize = 20, nextToken = null) } returns
                            (listOf(ReceiptFixtures.aReceipt()) to null)

                    val result = webTestClient.get()
                        .uri("/receipts")
                        .exchange()
                        .expectStatus().isOk
                        .expectBody()
                        .returnResult()

                    result.jsonPath("$.receipts[0].id") shouldBe ReceiptFixtures.DEFAULT_RECEIPT_ID
                    coVerify(exactly = 1) { receiptService.listReceipts(pageSize = 20, nextToken = null) }
                }
            }

            context("when maxResults exceeds the max page size") {
                it("clamps to 100") {
                    coEvery { receiptService.listReceipts(pageSize = 100, nextToken = null) } returns
                            (emptyList<Receipt>() to null)

                    webTestClient.get()
                        .uri("/receipts?maxResults=9999")
                        .exchange()
                        .expectStatus().isOk

                    coVerify(exactly = 1) { receiptService.listReceipts(pageSize = 100, nextToken = null) }
                }
            }

            context("when maxResults is below 1") {
                it("clamps to 1") {
                    coEvery { receiptService.listReceipts(pageSize = 1, nextToken = null) } returns
                            (emptyList<Receipt>() to null)

                    webTestClient.get()
                        .uri("/receipts?maxResults=0")
                        .exchange()
                        .expectStatus().isOk

                    coVerify(exactly = 1) { receiptService.listReceipts(pageSize = 1, nextToken = null) }
                }
            }
        }

        describe("deleteReceipt") {

            context("when receipt exists") {
                it("returns 200 with the deleted id") {
                    coEvery { receiptService.deleteReceipt(any()) } returns ObjectId(ReceiptFixtures.DEFAULT_RECEIPT_ID)

                    val result = webTestClient.delete()
                        .uri("/receipts/${ReceiptFixtures.DEFAULT_RECEIPT_ID}")
                        .exchange()
                        .expectStatus().isOk
                        .expectBody()
                        .returnResult()

                    result.jsonPath("$.id") shouldBe ReceiptFixtures.DEFAULT_RECEIPT_ID
                }
            }
        }

        describe("updateReceipt") {

            context("when input is valid") {
                it("returns 200 with the updated receipt") {
                    coEvery { receiptService.updateReceipt(any(), any()) } returns ReceiptFixtures.aReceipt()

                    val result = webTestClient.put()
                        .uri("/receipts/${ReceiptFixtures.DEFAULT_RECEIPT_ID}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ReceiptFixtures.anUpdateReceiptInput())
                        .exchange()
                        .expectStatus().isOk
                        .expectBody()
                        .returnResult()

                    result.jsonPath("$.receipt.id") shouldBe ReceiptFixtures.DEFAULT_RECEIPT_ID
                }
            }
        }
    }
}