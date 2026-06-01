package com.quri.management.api.handlers.profile

import com.quri.client.model.DeleteProfileInput
import com.quri.client.model.DeleteProfileOutput
import com.quri.management.services.ProfileService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the profile deletion operation.
 */
@RestController
@RequestMapping("/profiles/{id}")
class DeleteProfile(private val profileService: ProfileService) {
    @DeleteMapping
    suspend fun deleteProfile(@PathVariable id: String): DeleteProfileOutput {
        val input = DeleteProfileInput.builder()
            .id(id)
            .build()

        val deletedProfileId = profileService.deleteProfile(input)

        return DeleteProfileOutput.builder()
            .id(deletedProfileId.toString())
            .build()
    }
}
