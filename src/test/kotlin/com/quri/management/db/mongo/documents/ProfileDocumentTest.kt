package com.quri.management.db.mongo.documents

import com.quri.client.model.Gender
import com.quri.management.fixtures.documents.ProfileDocumentFixtures
import com.quri.management.fixtures.models.ProfileFixtures
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.bson.types.ObjectId
import java.time.Instant

@Suppress("unused")
class ProfileDocumentTest :
    DescribeSpec({

        describe("toApi") {

            context("when generatedId is provided") {
                it("uses generatedId over the document id") {
                    val document = ProfileDocumentFixtures.aProfileDocument()
                    val generatedId = ObjectId().toString()

                    val result = document.toApi(generatedId)

                    result.id shouldBe generatedId
                }
            }

            context("when generatedId is null") {
                it("uses the document id") {
                    val document = ProfileDocumentFixtures.aProfileDocument()

                    val result = document.toApi()

                    result.id shouldBe document.id.toString()
                }
            }

            context("maps all required fields correctly") {
                it("produces an api Profile with matching field values") {
                    val document = ProfileDocumentFixtures.aProfileDocument()

                    val result = document.toApi()

                    assertSoftly(result) {
                        it.username shouldBe document.username
                        it.firstName shouldBe document.firstName
                        it.lastName shouldBe document.lastName
                        it.email shouldBe document.email
                        it.createdBy shouldBe document.createdBy
                        it.createdAt shouldBe document.createdAt
                        it.updatedBy shouldBe document.updatedBy
                        it.updatedAt shouldBe document.updatedAt
                    }
                }
            }

            context("gender mapping") {
                context("when gender is present") {
                    it("maps the string value to the Gender enum") {
                        val document = ProfileDocumentFixtures.aProfileDocument(gender = "MALE")

                        val result = document.toApi()

                        result.gender shouldBe Gender.MALE
                    }
                }

                context("when gender is null") {
                    it("produces a null gender on the api model") {
                        val document = ProfileDocumentFixtures.aProfileDocument(gender = null)

                        val result = document.toApi()

                        result.gender shouldBe null
                    }
                }
            }

            context("following and followers mapping") {
                it("maps ObjectId lists to hex strings") {
                    val followingId = ObjectId()
                    val followerId = ObjectId()
                    val document = ProfileDocumentFixtures.aProfileDocument(
                        following = listOf(followingId),
                        followers = listOf(followerId),
                    )

                    val result = document.toApi()

                    result.following shouldBe listOf(followingId.toHexString())
                    result.followers shouldBe listOf(followerId.toHexString())
                }

                it("maps null lists to null on the api model") {
                    val document = ProfileDocumentFixtures.aProfileDocument(
                        following = null,
                        followers = null,
                    )

                    val result = document.toApi()

                    result.following shouldBe emptyList()
                    result.followers shouldBe emptyList()
                }
            }
        }

        describe("from(CreateProfileInput)") {

            it("maps all input fields and sets audit fields") {
                val input = ProfileFixtures.aCreateProfileInput()
                val ownerId = "owner-abc"

                val result = ProfileDocument.from(input, ownerId)

                assertSoftly(result) {
                    it.username shouldBe "testuser"
                    it.firstName shouldBe "Test"
                    it.lastName shouldBe "User"
                    it.email shouldBe "test@quri.com"
                    it.dateOfBirth shouldBe Instant.parse("1995-04-15T00:00:00Z")
                    it.createdBy shouldBe ownerId
                    it.updatedBy shouldBe ownerId
                    it.createdAt shouldNotBe null
                    it.updatedAt shouldNotBe null
                    it.createdAt shouldBe it.updatedAt
                }
            }
        }
    })
