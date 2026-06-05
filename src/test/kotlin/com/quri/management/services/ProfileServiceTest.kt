package com.quri.management.services

import com.quri.client.model.InternalFailureException
import com.quri.client.model.ResourceNotFoundException
import com.quri.client.model.ValidationException
import com.quri.management.api.validation.profile.CreateProfileValidator
import com.quri.management.api.validation.profile.UpdateProfileValidator
import com.quri.management.db.mongo.collections.ProfileCollection
import com.quri.management.fixtures.models.ProfileFixtures
import com.quri.management.fixtures.models.ProfileFixtures.DEFAULT_OWNER_ID
import com.quri.management.fixtures.models.ProfileFixtures.DEFAULT_PROFILE_ID
import com.quri.management.fixtures.models.ProfileFixtures.DEFAULT_USER_ID
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
class ProfileServiceTest :
    DescribeSpec({

        val profileCollection = mockk<ProfileCollection>()
        val createProfileValidator = mockk<CreateProfileValidator>()
        val updateProfileValidator = mockk<UpdateProfileValidator>()

        val profileService = ProfileService(
            profileCollection = profileCollection,
            createProfileValidator = createProfileValidator,
            updateProfileValidator = updateProfileValidator,
        )

        afterEach { clearMocks(profileCollection, createProfileValidator, updateProfileValidator) }

        describe("getProfileFromId") {

            context("when a profile exists for the given ID") {
                it("passes the correct ObjectId and returns the matching profile") {
                    val profile = ProfileFixtures.aProfile()
                    val input = ProfileFixtures.aGetProfileInput(id = DEFAULT_PROFILE_ID)
                    coEvery { profileCollection.findById(any()) } returns profile

                    val result = profileService.getProfileFromId(input)

                    result shouldBe profile
                }
            }

            context("when no profile exists for the given ID") {
                it("throws ResourceNotFoundException") {
                    val input = ProfileFixtures.aGetProfileInput(id = DEFAULT_PROFILE_ID)
                    coEvery { profileCollection.findById(any()) } returns null

                    shouldThrow<ResourceNotFoundException> {
                        profileService.getProfileFromId(input)
                    }
                }
            }
        }

        describe("createProfile") {

            context("when input is valid and collection succeeds") {
                it("validates input, persists, and returns the created profile") {
                    val input = ProfileFixtures.aCreateProfileInput()
                    val created = ProfileFixtures.aProfile()
                    coJustRun { createProfileValidator.validate(any(), any()) }
                    coEvery { profileCollection.create(input, DEFAULT_OWNER_ID) } returns created
                    coEvery { profileCollection.exists(any()) } returns false

                    val result = profileService.createProfile(input, DEFAULT_OWNER_ID)

                    result shouldBe created
                    coVerify(exactly = 1) { createProfileValidator.validate("createProfile", input) }
                    coVerify(exactly = 1) { profileCollection.create(input, DEFAULT_OWNER_ID) }
                    coVerify(exactly = 1) { profileCollection.exists(any()) }
                }
            }

            context("when email is already registered") {
                it("throws ValidationException before attempting to persist") {
                    val input = ProfileFixtures.aCreateProfileInput()
                    coJustRun { createProfileValidator.validate(any(), any()) }
                    coEvery { profileCollection.exists(any()) } returns true

                    shouldThrow<ValidationException> {
                        profileService.createProfile(input, DEFAULT_OWNER_ID)
                    }

                    coVerify(exactly = 0) { profileCollection.create(any(), any()) }
                }
            }

            context("when collection returns null") {
                it("throws InternalFailureException") {
                    val input = ProfileFixtures.aCreateProfileInput()
                    coJustRun { createProfileValidator.validate(any(), any()) }
                    coEvery { profileCollection.create(any(), any()) } returns null
                    coEvery { profileCollection.exists(any()) } returns false

                    shouldThrow<InternalFailureException> {
                        profileService.createProfile(input, DEFAULT_OWNER_ID)
                    }
                }
            }
        }

        describe("listProfiles") {

            context("when profiles exist") {
                it("returns the list and a pagination token") {
                    val profiles = listOf(
                        ProfileFixtures.aProfile(),
                        ProfileFixtures.aProfile(id = ObjectId().toString(), username = "otheruser"),
                    )
                    val nextToken = "next-page-token"
                    coEvery { profileCollection.listAll(10, null) } returns Pair(profiles, nextToken)

                    val (result, token) = profileService.listProfiles(pageSize = 10, nextToken = null)

                    result shouldBe profiles
                    token shouldBe nextToken
                }
            }

            context("when no profiles exist") {
                it("returns an empty list and null token") {
                    coEvery { profileCollection.listAll(any(), any()) } returns Pair(emptyList(), null)

                    val (result, token) = profileService.listProfiles(pageSize = 10, nextToken = null)

                    result shouldBe emptyList()
                    token shouldBe null
                }
            }

            context("when a nextToken is provided") {
                it("passes it through to the collection") {
                    val token = "some-cursor"
                    coEvery { profileCollection.listAll(10, token) } returns Pair(emptyList(), null)

                    profileService.listProfiles(pageSize = 10, nextToken = token)

                    coVerify(exactly = 1) { profileCollection.listAll(10, token) }
                }
            }
        }

        describe("deleteProfile") {

            context("when the profile exists") {
                it("passes the correct ObjectId and returns it") {
                    val objectId = ObjectId(DEFAULT_PROFILE_ID)
                    val input = ProfileFixtures.aDeleteProfileInput(id = DEFAULT_PROFILE_ID)
                    coEvery { profileCollection.deleteById(any()) } returns objectId

                    val result = profileService.deleteProfile(input)

                    result shouldBe objectId
                }
            }

            context("when the profile does not exist") {
                it("throws ResourceNotFoundException") {
                    val input = ProfileFixtures.aDeleteProfileInput(id = DEFAULT_PROFILE_ID)
                    coEvery { profileCollection.deleteById(any()) } returns null

                    shouldThrow<ResourceNotFoundException> {
                        profileService.deleteProfile(input)
                    }
                }
            }
        }

        describe("updateProfile") {

            context("when input is valid and profile exists") {
                it("validates, updates, and returns the updated profile") {
                    val input = ProfileFixtures.anUpdateProfileInput(firstName = "Updated Name")
                    val updated = ProfileFixtures.aProfile(firstName = "Updated Name")
                    coJustRun { updateProfileValidator.validate(any(), any()) }
                    coEvery { profileCollection.update(input, DEFAULT_USER_ID) } returns updated

                    val result = profileService.updateProfile(input, DEFAULT_USER_ID)

                    result.firstName shouldBe "Updated Name"
                    coVerify(exactly = 1) { updateProfileValidator.validate("updateProfile", input) }
                }
            }

            context("when profile does not exist") {
                it("throws ResourceNotFoundException after validation") {
                    val input = ProfileFixtures.anUpdateProfileInput()
                    coJustRun { updateProfileValidator.validate(any(), any()) }
                    coEvery { profileCollection.update(any(), any()) } returns null

                    shouldThrow<ResourceNotFoundException> {
                        profileService.updateProfile(input, DEFAULT_USER_ID)
                    }

                    coVerify(exactly = 1) { updateProfileValidator.validate(any(), any()) }
                }
            }
        }
    })
