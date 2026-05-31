package com.quri.management.api.handlers.profile

import com.quri.client.model.GetProfileInput
import com.quri.client.model.GetProfileOutput
import com.quri.management.services.ProfileService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the profile retrieval operation.
 */
@RestController
@RequestMapping("/profiles/{profileId}")
class GetProfile(private val profileService: ProfileService) {
    @GetMapping
    suspend fun getProfile(@PathVariable profileId: String): GetProfileOutput {
        val input = GetProfileInput.builder()
            .profileId(profileId)
            .build()

        val profile = profileService.getProfileFromId(input)

        return GetProfileOutput.builder()
            .profile(profile)
            .build()
    }
}
