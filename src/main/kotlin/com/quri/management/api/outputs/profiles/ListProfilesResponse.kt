package com.quri.management.api.outputs.profiles

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.ListProfilesOutput
import com.quri.client.model.Profile

/**
 * Maps a paginated list of Smithy [Profile] models to a client-facing response.
 */
data class ListProfilesResponse(
    @JsonProperty("profiles") val profiles: List<ProfileResponse>,
    @JsonProperty("nextToken") val nextToken: String? = null,
) {
    companion object {
        fun from(model: ListProfilesOutput) =
            ListProfilesResponse(
                profiles = model.profiles.map { ProfileResponse.from(it) },
                nextToken = model.nextToken,
            )
    }
}
