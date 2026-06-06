package com.quri.management.db.mongo.collections

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.quri.client.model.Gender
import com.quri.management.config.IntegrationTest
import com.quri.management.db.mongo.MongoSchema.Collections
import com.quri.management.db.mongo.documents.ProfileDocument
import com.quri.management.fixtures.models.ProfileFixtures
import com.quri.management.fixtures.models.ProfileFixtures.DEFAULT_PROFILE_ID
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired

@Suppress("unused")
class ProfileCollectionTest : IntegrationTest() {

    @Autowired
    lateinit var profileCollection: ProfileCollection

    @Autowired
    lateinit var dataStoreDatabase: MongoDatabase

    init {
        afterEach {
            dataStoreDatabase
                .getCollection(Collections.PROFILES, ProfileDocument::class.java)
                .drop()
        }

        describe("findById") {

            context("when a profile exists for a given ID") {
                it("returns the matching profile") {
                    val input = ProfileFixtures.aCreateProfileInput()
                    val created = profileCollection.create(input, "owner-1")!!

                    val result = profileCollection.findById(ObjectId(created.id))
                    result shouldNotBe null
                    result!!.id shouldBe created.id
                }
            }

            context("when no profile exists for the given ID") {
                it("returns null") {
                    val result = profileCollection.findById(ObjectId())
                    result shouldBe null
                }
            }
        }

        describe("create") {

            context("input is a valid") {
                it("persists and returns the profile with a generated ID") {
                    val input = ProfileFixtures.aCreateProfileInput()

                    val result = profileCollection.create(input, "owner-1")

                    assertSoftly(result!!) {
                        it.id shouldNotBe null
                        it.username shouldBe "testuser"
                        it.firstName shouldBe "Test"
                        it.lastName shouldBe "User"
                        it.email shouldBe "test@quri.com"
                        it.dateOfBirth shouldNotBe null
                        it.createdBy shouldBe "owner-1"
                        it.updatedBy shouldBe "owner-1"
                    }
                }

                it("assigns distinct IDs to separate documents") {
                    val first = profileCollection.create(ProfileFixtures.aCreateProfileInput(), "owner-1")
                    val second = profileCollection.create(ProfileFixtures.aCreateProfileInput(), "owner-2")

                    first!!.id shouldNotBe second!!.id
                }
            }
        }

        describe("listAll") {

            context("when no profiles exists") {
                it("returns an empty list and null token") {
                    val (results, token) = profileCollection.listAll(10, null)

                    results shouldBe emptyList()
                    token shouldBe null
                }
            }

            context("when profiles exist within page size") {
                it("returns all profiles and null token") {
                    repeat(3) { profileCollection.create(ProfileFixtures.aCreateProfileInput(), "owner-1") }

                    val (results, token) = profileCollection.listAll(10, null)

                    results shouldHaveSize 3
                    token shouldBe null
                }
            }

            context("when profiles exceed page size") {
                it("returns pageSize results and a pagination token") {
                    repeat(5) { profileCollection.create(ProfileFixtures.aCreateProfileInput(), "owner-1") }

                    val (results, token) = profileCollection.listAll(3, null)

                    results shouldHaveSize 3
                    token shouldNotBe null
                }
            }

            context("when nextToken is provided") {
                it("returns the next page starting after the token") {
                    repeat(5) { profileCollection.create(ProfileFixtures.aCreateProfileInput(), "owner-1") }

                    val (firstPage, token) = profileCollection.listAll(3, null)
                    val (secondPage, nextToken) = profileCollection.listAll(3, token)

                    firstPage shouldHaveSize 3
                    secondPage shouldHaveSize 2
                    nextToken shouldBe null
                }
            }
        }

        describe("deleteById") {

            context("when the profile exists") {
                it("removes the document and returns the ObjectId") {
                    val created = profileCollection.create(ProfileFixtures.aCreateProfileInput(), "owner-1")!!
                    val objectId = ObjectId(created.id)

                    val result = profileCollection.deleteById(objectId)

                    result shouldBe objectId
                    profileCollection.findById(objectId) shouldBe null
                }
            }

            context("when the profile does not exist") {
                it("returns null") {
                    val result = profileCollection.deleteById(ObjectId())
                    result shouldBe null
                }
            }
        }

        describe("exists") {
            context("when the profile exists") {
                it("returns the profile") {
                    val input = ProfileFixtures.aCreateProfileInput(email = "test@quri.com")
                    val created = profileCollection.create(input, "owner-1")!!

                    val result = profileCollection.existsByEmail(created.email)!!

                    result shouldNotBe null
                }
            }

            context("when the profile does not exist") {
                it("returns null") {
                    val input = ProfileFixtures.aCreateProfileInput(email = "test@quri.com")
                    val created = profileCollection.create(input, "owner-1")

                    val result = profileCollection.existsByEmail(email = "fake@email.com")

                    result shouldBe null
                }
            }
        }

        describe("update") {

            context("when the profile exists") {
                it("updates specified fields and returns the updated profile") {
                    val created = profileCollection.create(ProfileFixtures.aCreateProfileInput(), "owner-1")!!
                    val input = ProfileFixtures.anUpdateProfileInput(
                        id = created.id,
                        username = "UpdatedUsername",
                        firstName = "UpdatedFirstName",
                        lastName = "UpdatedLastName",
                        email = "updated@email.com",
                        middleName = "UpdatedMiddleName",
                        phoneNumber = "12345678910",
                        bio = "Updated bio",
                        following = listOf(DEFAULT_PROFILE_ID),
                        followers = listOf(DEFAULT_PROFILE_ID),
                        gender = Gender.PREFER_NOT_TO_SAY,
                        location = ProfileFixtures.aUserLocation(),
                    )

                    val result = profileCollection.update(input, "user-1")

                    assertSoftly(result!!) {
                        it.id shouldBe created.id
                        it.username shouldBe "UpdatedUsername"
                        it.firstName shouldBe "UpdatedFirstName"
                        it.lastName shouldBe "UpdatedLastName"
                        it.email shouldBe "updated@email.com"
                        it.middleName shouldBe "UpdatedMiddleName"
                        it.phoneNumber shouldBe "12345678910"
                        it.bio shouldBe "Updated bio"
                        it.following shouldContain DEFAULT_PROFILE_ID
                        it.followers shouldContain DEFAULT_PROFILE_ID
                        it.gender shouldBe Gender.PREFER_NOT_TO_SAY
                        it.location shouldBe ProfileFixtures.aUserLocation()
                    }
                }

                it("leaves unspecified fields unchanged") {
                    val created = profileCollection.create(
                        ProfileFixtures.aCreateProfileInput(username = "OriginalUsername"),
                        "owner-1",
                    )!!
                    val input = ProfileFixtures.anUpdateProfileInput(id = created.id)

                    val result = profileCollection.update(input, "user-1")

                    assertSoftly(result!!) {
                        it.id shouldBe created.id
                        it.username shouldBe "OriginalUsername"
                        it.firstName shouldBe "Test"
                        it.lastName shouldBe "User"
                        it.email shouldBe "test@quri.com"
                        it.dateOfBirth shouldNotBe null
                        it.middleName shouldBe null
                        it.phoneNumber shouldBe null
                        it.bio shouldBe null
                        it.following shouldBe emptyList<String>()
                        it.followers shouldBe emptyList<String>()
                        it.gender shouldBe null
                        it.location shouldBe null
                        it.updatedBy shouldBe "user-1"
                        it.updatedAt shouldNotBe null
                    }
                }
            }

            context("when the profile does not exist") {
                it("returns null") {
                    val input = ProfileFixtures.anUpdateProfileInput(id = ObjectId().toString())
                    val result = profileCollection.update(input, "user-1")
                    result shouldBe null
                }
            }
        }
    }
}
