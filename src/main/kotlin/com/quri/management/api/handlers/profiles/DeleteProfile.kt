package com.quri.management.api.handlers.profiles

import com.quri.client.model.DeleteProfileInput
import com.quri.client.model.DeleteProfileOutput
import com.quri.management.api.outputs.profiles.DeleteProfileResponse
import com.quri.management.services.ProfileService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the profile deletion operation.
 */
@RestController
@RequestMapping("/profiles/{profileId}")
class DeleteProfile(private val profileService: ProfileService) {
    @DeleteMapping
    suspend fun deleteProfile(@PathVariable profileId: String): DeleteProfileResponse {
        val input = DeleteProfileInput.builder()
            .profileId(profileId)
            .build()

        val deletedProfileId = profileService.deleteProfile(input)

        val output = DeleteProfileOutput.builder()
            .profileId(deletedProfileId)
            .build()

        return DeleteProfileResponse.from(output)
    }
}
