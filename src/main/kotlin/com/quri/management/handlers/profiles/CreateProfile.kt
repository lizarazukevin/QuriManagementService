package com.quri.management.handlers.profiles

import com.quri.client.model.CreateProfileInput
import com.quri.client.model.CreateProfileOutput
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
class CreateProfile(private val profileService: ProfileService) {
    @PostMapping
    suspend fun createProfile(@RequestBody input: CreateProfileInput): CreateProfileOutput {
        val createdProfile = profileService.createProfile(input)

        return CreateProfileOutput.builder()
            .profile(createdProfile)
            .build()
    }

    // TODO: Add input validation for [CreateProfileInput]
}
