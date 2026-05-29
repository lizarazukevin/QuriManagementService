package com.quri.management.services

import com.quri.client.model.BillStatus
import com.quri.client.model.InternalFailureException
import com.quri.client.model.ResourceNotFoundException
import com.quri.management.api.validation.bill.CreateBillValidator
import com.quri.management.api.validation.bill.UpdateBillValidator
import com.quri.management.db.mongo.collections.BillCollection
import com.quri.management.fixtures.BillFixtures
import com.quri.management.fixtures.BillFixtures.DEFAULT_BILL_ID
import com.quri.management.fixtures.BillFixtures.DEFAULT_OWNER_ID
import com.quri.management.fixtures.BillFixtures.DEFAULT_USER_ID
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
class BillServiceTest :
    DescribeSpec({

        val billCollection = mockk<BillCollection>()
        val createBillValidator = mockk<CreateBillValidator>()
        val updateBillValidator = mockk<UpdateBillValidator>()

        val billService = BillService(
            billCollection = billCollection,
            createBillValidator = createBillValidator,
            updateBillValidator = updateBillValidator,
        )

        afterEach { clearMocks(billCollection, createBillValidator, updateBillValidator) }

        describe("getBillFromId") {

            context("when a bill exists for the given ID") {
                it("returns the matching bill") {
                    val bill = BillFixtures.aBill()
                    val input = BillFixtures.aGetBillInput(billId = DEFAULT_BILL_ID)
                    coEvery { billCollection.findById(any()) } returns bill

                    val result = billService.getBillFromId(input)

                    result shouldBe bill
                    coVerify(exactly = 1) { billCollection.findById(ObjectId(input.billId)) }
                }
            }

            context("when no bill exists for the given ID") {
                it("throws ResourceNotFoundException") {
                    val input = BillFixtures.aGetBillInput(billId = DEFAULT_BILL_ID)
                    coEvery { billCollection.findById(any()) } returns null

                    shouldThrow<ResourceNotFoundException> {
                        billService.getBillFromId(input)
                    }
                }
            }
        }

        describe("createBill") {

            context("when input is valid and collection succeeds") {
                it("validates input, persists, and returns the created bill") {
                    val input = BillFixtures.aCreateBillInput()
                    val created = BillFixtures.aBill(status = BillStatus.DRAFT)
                    coJustRun { createBillValidator.validate(any(), any()) }
                    coEvery { billCollection.create(input, DEFAULT_OWNER_ID) } returns created

                    val result = billService.createBill(input, DEFAULT_OWNER_ID)

                    result shouldBe created
                    coVerify(exactly = 1) { createBillValidator.validate("createBill", input) }
                    coVerify(exactly = 1) { billCollection.create(input, DEFAULT_OWNER_ID) }
                }
            }

            context("when collection returns null") {
                it("throws InternalFailureException") {
                    val input = BillFixtures.aCreateBillInput()
                    coJustRun { createBillValidator.validate(any(), any()) }
                    coEvery { billCollection.create(any(), any()) } returns null

                    shouldThrow<InternalFailureException> {
                        billService.createBill(input, DEFAULT_OWNER_ID)
                    }
                }
            }
        }

        describe("listBills") {

            context("when bills exist") {
                it("returns the list and a pagination token") {
                    val bills = listOf(BillFixtures.aBill(), BillFixtures.aBill(id = ObjectId().toString()))
                    val nextToken = "next-page-token"
                    coEvery { billCollection.listAll(10, null) } returns Pair(bills, nextToken)

                    val (result, token) = billService.listBills(pageSize = 10, nextToken = null)

                    result shouldBe bills
                    token shouldBe nextToken
                }
            }

            context("when no bills exist") {
                it("returns an empty list and null token") {
                    coEvery { billCollection.listAll(any(), any()) } returns Pair(emptyList(), null)

                    val (result, token) = billService.listBills(pageSize = 10, nextToken = null)

                    result shouldBe emptyList()
                    token shouldBe null
                }
            }

            context("when a nextToken is provided") {
                it("passes it through to the collection") {
                    val token = "some-cursor"
                    coEvery { billCollection.listAll(10, token) } returns Pair(emptyList(), null)

                    billService.listBills(pageSize = 10, nextToken = token)

                    coVerify(exactly = 1) { billCollection.listAll(10, token) }
                }
            }
        }

        describe("deleteBill") {

            context("when the bill exists") {
                it("returns the deleted bill ID as a string") {
                    val objectId = ObjectId(DEFAULT_BILL_ID)
                    val input = BillFixtures.aDeleteBillInput(billId = DEFAULT_BILL_ID)
                    coEvery { billCollection.deleteById(any()) } returns objectId

                    val result = billService.deleteBill(input)

                    result shouldBe objectId
                }
            }

            context("when the bill does not exist") {
                it("throws ResourceNotFoundException") {
                    val input = BillFixtures.aDeleteBillInput(billId = DEFAULT_BILL_ID)
                    coEvery { billCollection.deleteById(any()) } returns null

                    shouldThrow<ResourceNotFoundException> {
                        billService.deleteBill(input)
                    }
                }
            }
        }

        describe("updateBill") {

            context("when input is valid and bill exists") {
                it("validates, updates, and returns the updated bill") {
                    val input = BillFixtures.anUpdateBillInput(name = "Updated Name")
                    val updated = BillFixtures.aBill(name = "Updated Name")
                    coJustRun { updateBillValidator.validate(any(), any()) }
                    coEvery { billCollection.update(input, DEFAULT_USER_ID) } returns updated

                    val result = billService.updateBill(input, DEFAULT_USER_ID)

                    result.name shouldBe "Updated Name"
                    coVerify(exactly = 1) { updateBillValidator.validate("updateBill", input) }
                }
            }

            context("when bill does not exist") {
                it("throws ResourceNotFoundException after validation") {
                    val input = BillFixtures.anUpdateBillInput()
                    coJustRun { updateBillValidator.validate(any(), any()) }
                    coEvery { billCollection.update(any(), any()) } returns null

                    shouldThrow<ResourceNotFoundException> {
                        billService.updateBill(input, DEFAULT_USER_ID)
                    }

                    coVerify(exactly = 1) { updateBillValidator.validate(any(), any()) }
                }
            }
        }
    })
