package com.quri.management.handlers.profiles

import com.quri.client.model.CreateProfileInput
import com.quri.client.model.CreateProfileOutput
import com.quri.management.security.identity.UserIdentity
import com.quri.management.services.ProfileService
import com.quri.management.validators.inputs.profiles.CreateProfileInputRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the [CreateProfile] operation.
 *
 * @see ProfileService.createProfile
 */
@RestController
@RequestMapping("/profiles")
class CreateProfile(
    private val profileService: ProfileService,
    private val userIdentity: UserIdentity,
    identity: UserIdentity,
) {
    @PostMapping
    suspend fun createProfile(@RequestBody request: CreateProfileInputRequest): CreateProfileOutput {
        val input = CreateProfileInput.builder()
            .username(request.username)
            .firstName(request.firstName)
            .lastName(request.lastName)
            .email(request.email)
            .phoneNumber(request.phoneNumber)
            .build()

        val createdProfile = profileService.createProfile(input, userIdentity.userId())

        return CreateProfileOutput.builder()
            .profile(createdProfile)
            .build()
    }
}
