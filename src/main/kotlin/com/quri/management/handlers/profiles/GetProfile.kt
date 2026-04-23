package com.quri.management.handlers.profiles

import com.quri.client.model.GetProfileInput
import com.quri.client.model.GetProfileOutput
import com.quri.management.services.ProfileService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the [GetProfile] operation.
 *
 * @see ProfileService.getProfileFromId
 */
@RestController
@RequestMapping("/profiles/{profileId}")
class GetProfile(private val profileService: ProfileService) {
    @GetMapping
    suspend fun getProfile(@PathVariable profileId: String): GetProfileOutput {
        val input = getProfileInput(profileId)
        val foundProfile = profileService.getProfileFromId(input)

        return GetProfileOutput.builder()
            .profile(foundProfile)
            .build()
    }

    private fun getProfileInput(profileId: String): GetProfileInput =
        GetProfileInput.builder()
            .profileId(profileId)
            .build()
}
