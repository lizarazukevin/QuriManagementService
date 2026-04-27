package com.quri.management.handlers.profiles

import com.quri.client.model.CreateProfileInput
import com.quri.client.model.CreateProfileOutput
import com.quri.management.services.ProfileService
import com.quri.management.validators.inputs.profiles.CreateProfileInputRequest
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
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
class CreateProfile(private val profileService: ProfileService) {
    @PostMapping
    suspend fun createProfile(
            @RequestBody request: CreateProfileInputRequest,
            authentication: JwtAuthenticationToken,
        ): CreateProfileOutput {
        val userId = authentication.token.subject

        val input = CreateProfileInput.builder()
            .username(request.username)
            .firstName(request.firstName)
            .lastName(request.lastName)
            .email(request.email)
            .phoneNumber(request.phoneNumber)
            .build()

        val createdProfile = profileService.createProfile(input, userId)

        return CreateProfileOutput.builder()
            .profile(createdProfile)
            .build()
    }
}
