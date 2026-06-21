package com.quri.management.api.handlers

import com.ninjasquad.springmockk.MockkBean
import com.quri.client.model.Profile
import com.quri.management.api.handlers.profile.CreateProfile
import com.quri.management.api.handlers.profile.DeleteProfile
import com.quri.management.api.handlers.profile.GetProfile
import com.quri.management.api.handlers.profile.ListProfiles
import com.quri.management.api.handlers.profile.UpdateProfile
import com.quri.management.api.security.identity.UserIdentity
import com.quri.management.config.HandlerTest
import com.quri.management.fixtures.models.ProfileFixtures
import com.quri.management.fixtures.models.ProfileFixtures.DEFAULT_PROFILE_ID
import com.quri.management.fixtures.models.ProfileFixtures.DEFAULT_USER_ID
import com.quri.management.services.ProfileService
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import org.bson.types.ObjectId
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.http.MediaType

@Suppress("unused")
@WebFluxTest(
    controllers = [
        CreateProfile::class,
        DeleteProfile::class,
        GetProfile::class,
        ListProfiles::class,
        UpdateProfile::class,
    ],
)
class ProfileHandlerTest : HandlerTest() {

    @MockkBean lateinit var userIdentity: UserIdentity

    @MockkBean lateinit var profileService: ProfileService

    init {
        beforeEach {
            coEvery { userIdentity.userId() } returns DEFAULT_USER_ID
        }

        describe("createProfile") {

            context("when input is valid") {
                it("returns 201 with the created profile") {
                    coEvery { profileService.createProfile(any(), any()) } returns ProfileFixtures.aProfile()

                    val result = webTestClient.post()
                        .uri("/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ProfileFixtures.aCreateProfileInput())
                        .exchange()
                        .expectStatus().isCreated
                        .expectBody()
                        .returnResult()

                    result.jsonPath("$.profile.id") shouldBe DEFAULT_PROFILE_ID
                    result.jsonPath("$.profile.username") shouldBe "testuser"
                    result.jsonPath("$.profile.createdBy") shouldBe DEFAULT_USER_ID
                }
            }
        }

        describe("getProfile") {

            context("when profile exists") {
                it("returns 200 with the profile") {
                    coEvery { profileService.getProfileFromId(any()) } returns ProfileFixtures.aProfile()

                    val result = webTestClient.get()
                        .uri("/profiles/$DEFAULT_PROFILE_ID")
                        .exchange()
                        .expectStatus().isOk
                        .expectBody()
                        .returnResult()

                    result.jsonPath("$.profile.id") shouldBe DEFAULT_PROFILE_ID
                    result.jsonPath("$.profile.username") shouldBe "testuser"
                }
            }
        }

        describe("listProfiles") {

            context("when called with no params") {
                it("returns 200 with default pageSize") {
                    coEvery { profileService.listProfiles(pageSize = 20, nextToken = null) } returns
                        (listOf(ProfileFixtures.aProfile()) to null)

                    val result = webTestClient.get()
                        .uri("/profiles")
                        .exchange()
                        .expectStatus().isOk
                        .expectBody()
                        .returnResult()

                    result.jsonPath("$.profiles[0].id") shouldBe DEFAULT_PROFILE_ID
                    coVerify(exactly = 1) { profileService.listProfiles(pageSize = 20, nextToken = null) }
                }
            }

            context("when maxResults exceeds the max page size") {
                it("clamps to 100") {
                    coEvery { profileService.listProfiles(pageSize = 100, nextToken = null) } returns
                        (emptyList<Profile>() to null)

                    webTestClient.get()
                        .uri("/profiles?maxResults=9999")
                        .exchange()
                        .expectStatus().isOk

                    coVerify(exactly = 1) { profileService.listProfiles(pageSize = 100, nextToken = null) }
                }
            }

            context("when maxResults is below 1") {
                it("clamps to 1") {
                    coEvery { profileService.listProfiles(pageSize = 1, nextToken = null) } returns
                        (emptyList<Profile>() to null)

                    webTestClient.get()
                        .uri("/profiles?maxResults=0")
                        .exchange()
                        .expectStatus().isOk

                    coVerify(exactly = 1) { profileService.listProfiles(pageSize = 1, nextToken = null) }
                }
            }

            context("when nextToken is provided") {
                it("passes it through and returns the new token") {
                    val token = "tok_first"
                    coEvery { profileService.listProfiles(pageSize = 20, nextToken = token) } returns
                        (emptyList<Profile>() to "tok_second")

                    val result = webTestClient.get()
                        .uri("/profiles?nextToken=$token")
                        .exchange()
                        .expectStatus().isOk
                        .expectBody()
                        .returnResult()

                    result.jsonPath("$.nextToken") shouldBe "tok_second"
                }
            }
        }

        describe("deleteProfile") {

            context("when profile exists") {
                it("returns 200 with the deleted id") {
                    coEvery { profileService.deleteProfile(any()) } returns ObjectId(DEFAULT_PROFILE_ID)

                    val result = webTestClient.delete()
                        .uri("/profiles/$DEFAULT_PROFILE_ID")
                        .exchange()
                        .expectStatus().isOk
                        .expectBody()
                        .returnResult()

                    result.jsonPath("$.id") shouldBe DEFAULT_PROFILE_ID
                }
            }
        }

        describe("updateProfile") {

            context("when input is valid") {
                it("returns 200 with the updated profile") {
                    coEvery { profileService.updateProfile(any(), any()) } returns ProfileFixtures.aProfile()

                    val result = webTestClient.patch()
                        .uri("/profiles/$DEFAULT_PROFILE_ID")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ProfileFixtures.anUpdateProfileInput())
                        .exchange()
                        .expectStatus().isOk
                        .expectBody()
                        .returnResult()

                    result.jsonPath("$.profile.id") shouldBe DEFAULT_PROFILE_ID
                }
            }
        }
    }
}
