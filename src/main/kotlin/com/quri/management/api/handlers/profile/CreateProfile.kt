package com.quri.management.api.handlers.profile

import com.quri.client.model.CreateProfileInput
import com.quri.client.model.CreateProfileOutput
import com.quri.management.api.security.identity.UserIdentity
import com.quri.management.services.ProfileService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
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
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createProfile(@RequestBody input: CreateProfileInput): CreateProfileOutput {
        val profile = profileService.createProfile(input, userIdentity.userId())

        return CreateProfileOutput.builder()
            .profile(profile)
            .build()
    }
}
