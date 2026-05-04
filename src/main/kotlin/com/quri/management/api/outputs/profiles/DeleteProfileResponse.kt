package com.quri.management.api.outputs.profiles

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.DeleteProfileOutput
import com.quri.client.model.Profile

/**
 * Maps the [Profile] delete result to a client-facing response.
 */
data class DeleteProfileResponse(@JsonProperty("profileId") val profileId: String?) {
    companion object {
        fun from(model: DeleteProfileOutput) =
            DeleteProfileResponse(
                profileId = model.profileId,
            )
    }
}
