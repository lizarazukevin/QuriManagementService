package com.quri.management.handlers.profiles

import com.quri.management.services.ProfileService
import com.quri.server.model.GetProfileInput
import com.quri.server.model.GetProfileOutput
import com.quri.server.service.GetProfileOperation
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import software.amazon.smithy.java.server.RequestContext

/**
 * Handles the [GetProfile] operation.
 *
 * TODO: Migrate to GetProfileOperationAsync once Kotlin server codegen is available.
 * runBlocking is a temporary workaround — it blocks the Smithy worker thread.
 *
 * @see ProfileService.getProfileFromId
 */
@Component
class GetProfile(
    private val profileService: ProfileService
): GetProfileOperation {
    override fun getProfile(input: GetProfileInput, context: RequestContext?): GetProfileOutput {
        val foundProfile = runBlocking {
            profileService.getProfileFromId(input)
        }

        return GetProfileOutput.builder()
            .profile(foundProfile)
            .build()
    }
}