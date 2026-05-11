package com.quri.management.api.handlers.profiles

import com.quri.client.model.UpdateProfileInput
import com.quri.client.model.UpdateProfileOutput
import com.quri.management.api.security.identity.UserIdentity
import com.quri.management.services.ProfileService
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the profile update operation.
 */
@RestController
@RequestMapping("/profiles")
class UpdateProfile(private val profileService: ProfileService, private val userIdentity: UserIdentity) {
    @PatchMapping
    suspend fun updateProfile(@RequestBody input: UpdateProfileInput): UpdateProfileOutput {
        val profile = profileService.updateProfile(input, userIdentity.userId())

        return UpdateProfileOutput.builder()
            .profile(profile)
            .build()
    }
}
