package com.quri.management.api.handlers

import com.ninjasquad.springmockk.MockkBean
import com.quri.client.model.Bill
import com.quri.client.model.ResourceNotFoundException
import com.quri.client.model.ValidationException
import com.quri.management.api.handlers.bill.CreateBill
import com.quri.management.api.handlers.bill.DeleteBill
import com.quri.management.api.handlers.bill.GetBill
import com.quri.management.api.handlers.bill.ListBills
import com.quri.management.api.handlers.bill.UpdateBill
import com.quri.management.api.security.identity.UserIdentity
import com.quri.management.config.HandlerTest
import com.quri.management.fixtures.models.BillFixtures
import com.quri.management.fixtures.models.BillFixtures.DEFAULT_BILL_ID
import com.quri.management.fixtures.models.BillFixtures.DEFAULT_USER_ID
import com.quri.management.services.BillService
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import org.bson.types.ObjectId
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.http.MediaType

@Suppress("unused")
@WebFluxTest(
    controllers = [
        CreateBill::class,
        DeleteBill::class,
        GetBill::class,
        ListBills::class,
        UpdateBill::class,
    ],
)
class BillHandlerTest : HandlerTest() {

    @MockkBean lateinit var userIdentity: UserIdentity

    @MockkBean lateinit var billService: BillService

    init {
        beforeEach {
            coEvery { userIdentity.userId() } returns DEFAULT_USER_ID
        }

        describe("createBill") {

            context("when input is valid") {
                it("returns 201 with created bill") {
                    coEvery { billService.createBill(any(), any()) } returns BillFixtures.aBill()

                    val result = webTestClient.post()
                        .uri("/bills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(BillFixtures.aCreateBillInput())
                        .exchange()
                        .expectStatus().isCreated
                        .expectBody()
                        .returnResult()

                    result.jsonPath("$.bill.id") shouldBe DEFAULT_BILL_ID
                    result.jsonPath("$.bill.status") shouldBe "DRAFT"
                    result.jsonPath("$.bill.createdBy") shouldBe DEFAULT_USER_ID
                }
            }

            context("when input is invalid") {
                it("returns 400") {
                    coEvery { billService.createBill(any(), any()) } throws
                        ValidationException.builder().message("name is required").build()

                    webTestClient.post()
                        .uri("/bills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(BillFixtures.aCreateBillInput())
                        .exchange()
                        .expectStatus().isBadRequest
                }
            }
        }

        describe("getBill") {

            context("when bill exists") {
                it("returns 200 with the bill") {
                    coEvery { billService.getBillFromId(any()) } returns BillFixtures.aBill()

                    val result = webTestClient.get()
                        .uri("/bills/$DEFAULT_BILL_ID")
                        .exchange()
                        .expectStatus().isOk
                        .expectBody()
                        .returnResult()

                    result.jsonPath("$.bill.id") shouldBe DEFAULT_BILL_ID
                    result.jsonPath("$.bill.status") shouldBe "DRAFT"
                }
            }

            context("when bill does not exist") {
                it("returns 404") {
                    coEvery { billService.getBillFromId(any()) } throws
                        ResourceNotFoundException.builder().message("bill not found").build()

                    webTestClient.get()
                        .uri("/bills/missing-id")
                        .exchange()
                        .expectStatus().isNotFound
                }
            }
        }

        describe("listBills") {

            context("when called with no params") {
                it("returns 200 with default pageSize") {
                    coEvery { billService.listBills(pageSize = 20, nextToken = null) } returns
                        (listOf(BillFixtures.aBill()) to null)

                    val result = webTestClient.get()
                        .uri("/bills")
                        .exchange()
                        .expectStatus().isOk
                        .expectBody()
                        .returnResult()

                    result.jsonPath("$.bills[0].id") shouldBe DEFAULT_BILL_ID
                    coVerify(exactly = 1) { billService.listBills(pageSize = 20, nextToken = null) }
                }
            }

            context("when maxResults exceeds the max page size") {
                it("clamps to 100") {
                    coEvery { billService.listBills(pageSize = 100, nextToken = null) } returns
                        (emptyList<Bill>() to null)

                    webTestClient.get()
                        .uri("/bills?maxResults=9999")
                        .exchange()
                        .expectStatus().isOk

                    coVerify(exactly = 1) { billService.listBills(pageSize = 100, nextToken = null) }
                }
            }

            context("when maxResults is below 1") {
                it("clamps to 1") {
                    coEvery { billService.listBills(pageSize = 1, nextToken = null) } returns
                        (emptyList<Bill>() to null)

                    webTestClient.get()
                        .uri("/bills?maxResults=0")
                        .exchange()
                        .expectStatus().isOk

                    coVerify(exactly = 1) { billService.listBills(pageSize = 1, nextToken = null) }
                }
            }

            context("when nextToken is provided") {
                it("passes it through and returns the new token") {
                    val token = "tok_first"
                    coEvery { billService.listBills(pageSize = 20, nextToken = token) } returns
                        (emptyList<Bill>() to "tok_second")

                    val result = webTestClient.get()
                        .uri("/bills?nextToken=$token")
                        .exchange()
                        .expectStatus().isOk
                        .expectBody()
                        .returnResult()

                    result.jsonPath("$.nextToken") shouldBe "tok_second"
                }
            }
        }

        describe("deleteBill") {

            context("when bill exists") {
                it("returns 200 with the deleted id") {
                    coEvery { billService.deleteBill(any()) } returns ObjectId(DEFAULT_BILL_ID)

                    val result = webTestClient.delete()
                        .uri("/bills/$DEFAULT_BILL_ID")
                        .exchange()
                        .expectStatus().isOk
                        .expectBody()
                        .returnResult()

                    result.jsonPath("$.id") shouldBe DEFAULT_BILL_ID
                }
            }

            context("when bill does not exist") {
                it("returns 404") {
                    coEvery { billService.deleteBill(any()) } throws
                        ResourceNotFoundException.builder().message("bill not found").build()

                    webTestClient.delete()
                        .uri("/bills/missing-id")
                        .exchange()
                        .expectStatus().isNotFound
                }
            }
        }

        describe("updateBill") {

            context("when input is valid") {
                it("returns 200 with the updated bill") {
                    coEvery { billService.updateBill(any(), any()) } returns BillFixtures.aBill()

                    val result = webTestClient.patch()
                        .uri("/bills/$DEFAULT_BILL_ID")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(BillFixtures.anUpdateBillInput())
                        .exchange()
                        .expectStatus().isOk
                        .expectBody()
                        .returnResult()

                    result.jsonPath("$.bill.id") shouldBe DEFAULT_BILL_ID
                }
            }

            context("when input is invalid") {
                it("returns 400") {
                    coEvery { billService.updateBill(any(), any()) } throws
                        ValidationException.builder().message("invalid amount").build()

                    webTestClient.patch()
                        .uri("/bills/$DEFAULT_BILL_ID")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(BillFixtures.anUpdateBillInput())
                        .exchange()
                        .expectStatus().isBadRequest
                }
            }
        }
    }
}
