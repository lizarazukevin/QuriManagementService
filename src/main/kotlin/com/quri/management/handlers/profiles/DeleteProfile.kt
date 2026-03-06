package com.quri.management.handlers.profiles

import com.quri.management.services.ProfileService
import com.quri.server.model.DeleteProfileInput
import com.quri.server.model.DeleteProfileOutput
import com.quri.server.service.DeleteProfileOperation
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import software.amazon.smithy.java.server.RequestContext

/**
 * Handles the [DeleteProfile] operation.
 *
 * TODO: Migrate to DeleteProfileOperationAsync once Kotlin server codegen is available.
 * runBlocking is a temporary workaround — it blocks the Smithy worker thread.
 *
 * @see ProfileService.deleteProfile
 */
@Component
class DeleteProfile(
    private val profileService: ProfileService
): DeleteProfileOperation {
    override fun deleteProfile(input: DeleteProfileInput, context: RequestContext?): DeleteProfileOutput {
        val deletedProfileId = runBlocking {
            profileService.deleteProfile(input)
        }

        return DeleteProfileOutput.builder()
            .profileId(deletedProfileId)
            .build()
    }
}