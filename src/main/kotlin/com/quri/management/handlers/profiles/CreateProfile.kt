package com.quri.management.handlers.profiles

import com.quri.management.services.ProfileService
import com.quri.server.model.CreateProfileInput
import com.quri.server.model.CreateProfileOutput
import com.quri.server.service.CreateProfileOperation
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import software.amazon.smithy.java.server.RequestContext

/**
 * Handles the [CreateProfile] operation.
 *
 * TODO: Migrate to CreateProfileOperationAsync once Kotlin server codegen is available.
 * runBlocking is a temporary workaround — it blocks the Smithy worker thread.
 *
 * @see ProfileService.createProfile
 */
@Component
class CreateProfile (
    private val profileService: ProfileService
): CreateProfileOperation {
    override fun createProfile(input: CreateProfileInput, context: RequestContext?): CreateProfileOutput {
        val createdProfile = runBlocking {
            profileService.createProfile(input)
        }

        return CreateProfileOutput.builder()
            .profile(createdProfile)
            .build()
    }
}