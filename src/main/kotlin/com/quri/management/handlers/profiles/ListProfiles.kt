package com.quri.management.handlers.profiles

import com.quri.management.services.ProfileService
import com.quri.server.model.ListProfilesInput
import com.quri.server.model.ListProfilesOutput
import com.quri.server.service.ListProfilesOperation
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import software.amazon.smithy.java.server.RequestContext

const val DEFAULT_PROFILE_PAGE_SIZE = 20

/**
 * Handles the [ListProfiles] operation.
 *
 * TODO: Migrate to ListProfilesOperationAsync once Kotlin server codegen is available.
 * runBlocking is a temporary workaround — it blocks the Smithy worker thread.
 *
 * @see ProfileService.listProfiles
 */
@Component
class ListProfiles(private val profilesService: ProfileService) : ListProfilesOperation {
    override fun listProfiles(
        input: ListProfilesInput,
        context: RequestContext?,
    ): ListProfilesOutput {
        val (profiles, newToken) = runBlocking {
            profilesService.listProfiles(
                pageSize = input.maxResults ?: DEFAULT_PROFILE_PAGE_SIZE,
                nextToken = input.nextToken,
            )
        }

        return ListProfilesOutput.builder()
            .profiles(profiles)
            .nextToken(newToken)
            .build()
    }
}
