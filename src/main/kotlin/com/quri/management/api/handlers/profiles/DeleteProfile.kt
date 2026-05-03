package com.quri.management.api.handlers.profiles

import com.quri.client.model.DeleteProfileInput
import com.quri.client.model.DeleteProfileOutput
import com.quri.management.services.ProfileService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the [DeleteProfile] operation.
 *
 * @see ProfileService.deleteProfile
 */
@RestController
@RequestMapping("/profiles/{profileId}")
class DeleteProfile(private val profileService: ProfileService) {
    @DeleteMapping
    suspend fun deleteProfile(@PathVariable profileId: String): DeleteProfileOutput {
        val input = DeleteProfileInput.builder()
            .profileId(profileId)
            .build()

        val deletedProfileId = profileService.deleteProfile(input)

        return DeleteProfileOutput.builder()
            .profileId(deletedProfileId)
            .build()
    }
}
