package com.quri.management.services

import com.quri.client.model.InternalFailureException
import com.quri.client.model.ResourceNotFoundException
import com.quri.management.api.validation.receipt.CreateReceiptValidator
import com.quri.management.api.validation.receipt.UpdateReceiptValidator
import com.quri.management.db.mongo.collections.ReceiptCollection
import com.quri.management.fixtures.models.ReceiptFixtures
import com.quri.management.fixtures.models.ReceiptFixtures.DEFAULT_OWNER_ID
import com.quri.management.fixtures.models.ReceiptFixtures.DEFAULT_RECEIPT_ID
import com.quri.management.fixtures.models.ReceiptFixtures.DEFAULT_USER_ID
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import org.bson.types.ObjectId

@Suppress("unused")
class ReceiptServiceTest :
    DescribeSpec({

        val receiptCollection = mockk<ReceiptCollection>()
        val createReceiptValidator = mockk<CreateReceiptValidator>()
        val updateReceiptValidator = mockk<UpdateReceiptValidator>()

        val receiptService = ReceiptService(
            receiptCollection = receiptCollection,
            createReceiptValidator = createReceiptValidator,
            updateReceiptValidator = updateReceiptValidator,
        )

        afterEach { clearMocks(receiptCollection, createReceiptValidator, updateReceiptValidator) }

        describe("getReceiptFromId") {

            context("when a receipt exists for the given ID") {
                it("returns the matching receipt") {
                    val receipt = ReceiptFixtures.aReceipt()
                    val input = ReceiptFixtures.aGetReceiptInput(id = DEFAULT_RECEIPT_ID)
                    coEvery { receiptCollection.findById(any()) } returns receipt

                    val result = receiptService.getReceiptFromId(input)

                    result shouldBe receipt
                }
            }

            context("when no receipt exists for the given ID") {
                it("throws ResourceNotFoundException") {
                    val input = ReceiptFixtures.aGetReceiptInput(id = DEFAULT_RECEIPT_ID)
                    coEvery { receiptCollection.findById(any()) } returns null

                    shouldThrow<ResourceNotFoundException> {
                        receiptService.getReceiptFromId(input)
                    }
                }
            }
        }

        describe("createReceipt") {

            context("when input is valid and collection succeeds") {
                it("validates input, persists, and returns the created receipt") {
                    val input = ReceiptFixtures.aCreateReceiptInput()
                    val created = ReceiptFixtures.aReceipt()
                    coJustRun { createReceiptValidator.validate(any(), any()) }
                    coEvery { receiptCollection.create(input, DEFAULT_OWNER_ID) } returns created

                    val result = receiptService.createReceipt(input, DEFAULT_OWNER_ID)

                    result shouldBe created
                    coVerify(exactly = 1) { createReceiptValidator.validate("createReceipt", input) }
                    coVerify(exactly = 1) { receiptCollection.create(input, DEFAULT_OWNER_ID) }
                }
            }

            context("when collection returns null") {
                it("throws InternalFailureException") {
                    val input = ReceiptFixtures.aCreateReceiptInput()
                    coJustRun { createReceiptValidator.validate(any(), any()) }
                    coEvery { receiptCollection.create(any(), any()) } returns null

                    shouldThrow<InternalFailureException> {
                        receiptService.createReceipt(input, DEFAULT_OWNER_ID)
                    }
                }
            }
        }

        describe("listReceipts") {

            context("when receipts exist") {
                it("returns the list and a pagination token") {
                    val receipts = listOf(
                        ReceiptFixtures.aReceipt(),
                        ReceiptFixtures.aReceipt(id = ObjectId().toString(), vendorName = "Other Vendor"),
                    )
                    val nextToken = "next-page-token"
                    coEvery { receiptCollection.listAll(10, null) } returns Pair(receipts, nextToken)

                    val (result, token) = receiptService.listReceipts(pageSize = 10, nextToken = null)

                    result shouldBe receipts
                    token shouldBe nextToken
                }
            }

            context("when no receipts exist") {
                it("returns an empty list and null token") {
                    coEvery { receiptCollection.listAll(any(), any()) } returns Pair(emptyList(), null)

                    val (result, token) = receiptService.listReceipts(pageSize = 10, nextToken = null)

                    result shouldBe emptyList()
                    token shouldBe null
                }
            }

            context("when a nextToken is provided") {
                it("passes it through to the collection") {
                    val token = "some-cursor"
                    coEvery { receiptCollection.listAll(10, token) } returns Pair(emptyList(), null)

                    receiptService.listReceipts(pageSize = 10, nextToken = token)

                    coVerify(exactly = 1) { receiptCollection.listAll(10, token) }
                }
            }
        }

        describe("deleteReceipt") {

            context("when the receipt exists") {
                it("returns the deleted ObjectId") {
                    val objectId = ObjectId(DEFAULT_RECEIPT_ID)
                    val input = ReceiptFixtures.aDeleteReceiptInput(id = DEFAULT_RECEIPT_ID)
                    coEvery { receiptCollection.deleteById(any()) } returns objectId

                    val result = receiptService.deleteReceipt(input)

                    result shouldBe objectId
                }
            }

            context("when the receipt does not exist") {
                it("throws ResourceNotFoundException") {
                    val input = ReceiptFixtures.aDeleteReceiptInput(id = DEFAULT_RECEIPT_ID)
                    coEvery { receiptCollection.deleteById(any()) } returns null

                    shouldThrow<ResourceNotFoundException> {
                        receiptService.deleteReceipt(input)
                    }
                }
            }
        }

        describe("updateReceipt") {

            context("when input is valid and receipt exists") {
                it("validates, updates, and returns the updated receipt") {
                    val input = ReceiptFixtures.anUpdateReceiptInput(vendorName = "Updated Vendor")
                    val updated = ReceiptFixtures.aReceipt(vendorName = "Updated Vendor")
                    coJustRun { updateReceiptValidator.validate(any(), any()) }
                    coEvery { receiptCollection.update(input, DEFAULT_USER_ID) } returns updated

                    val result = receiptService.updateReceipt(input, DEFAULT_USER_ID)

                    result.vendorName shouldBe "Updated Vendor"
                    coVerify(exactly = 1) { updateReceiptValidator.validate("updateReceipt", input) }
                }
            }

            context("when receipt does not exist") {
                it("throws ResourceNotFoundException after validation") {
                    val input = ReceiptFixtures.anUpdateReceiptInput()
                    coJustRun { updateReceiptValidator.validate(any(), any()) }
                    coEvery { receiptCollection.update(any(), any()) } returns null

                    shouldThrow<ResourceNotFoundException> {
                        receiptService.updateReceipt(input, DEFAULT_USER_ID)
                    }

                    coVerify(exactly = 1) { updateReceiptValidator.validate(any(), any()) }
                }
            }
        }
    })
