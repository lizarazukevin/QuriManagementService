package com.quri.management.api.handlers.profiles

import com.quri.client.model.CreateProfileOutput
import com.quri.management.api.inputs.profiles.CreateProfileInputRequest
import com.quri.management.api.outputs.profiles.ProfileResponse
import com.quri.management.api.security.identity.UserIdentity
import com.quri.management.services.ProfileService
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
class CreateProfile(private val profileService: ProfileService, private val userIdentity: UserIdentity) {
    @PostMapping
    suspend fun createProfile(@RequestBody request: CreateProfileInputRequest): ProfileResponse {
        val input = request.toSmithyInput()

        val createdProfile = profileService.createProfile(input, userIdentity.userId())

        val output = CreateProfileOutput.builder()
            .profile(createdProfile)
            .build()

        return ProfileResponse.from(output)
    }
}
