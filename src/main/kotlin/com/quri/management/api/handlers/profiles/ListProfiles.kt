package com.quri.management.api.handlers.profiles

import com.quri.client.model.ListProfilesInput
import com.quri.client.model.ListProfilesOutput
import com.quri.management.api.outputs.profiles.ListProfilesResponse
import com.quri.management.services.ProfileService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Handles the list profiles operation.
 */
@RestController
@RequestMapping("/profiles")
class ListProfiles(private val profileService: ProfileService) {
    @GetMapping
    suspend fun listProfiles(
        @RequestParam maxResults: Int?,
        @RequestParam nextToken: String?,
    ): ListProfilesResponse {
        val pageSize = (maxResults ?: DEFAULT_PROFILE_PAGE_SIZE).coerceIn(1, MAX_PROFILE_PAGE_SIZE)
        val input = ListProfilesInput.builder()
            .maxResults(pageSize)
            .nextToken(nextToken)
            .build()

        val (profiles, newToken) = profileService.listProfiles(
            pageSize = input.maxResults,
            nextToken = input.nextToken,
        )

        val output = ListProfilesOutput.builder()
            .profiles(profiles)
            .nextToken(newToken)
            .build()

        return ListProfilesResponse.from(output)
    }

    companion object {
        private const val DEFAULT_PROFILE_PAGE_SIZE = 20
        private const val MAX_PROFILE_PAGE_SIZE = 100
    }
}
