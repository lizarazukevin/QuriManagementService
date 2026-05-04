package com.quri.management.api.outputs.profiles

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.CreateProfileOutput
import com.quri.client.model.GetProfileOutput
import com.quri.client.model.Profile
import com.quri.management.shared.models.ProfileLocation
import java.time.Instant

/**
 * Maps a Smithy [Profile] to a client-facing response.
 *
 */
data class ProfileResponse(
    @JsonProperty("id") val id: String,
    @JsonProperty("username") val username: String,
    @JsonProperty("firstName") val firstName: String,
    @JsonProperty("lastName") val lastName: String,
    @JsonProperty("email") val email: String,

    @JsonProperty("following") val following: List<String>? = emptyList(),
    @JsonProperty("followers") val followers: List<String>? = emptyList(),
    @JsonProperty("middleName") val middleName: String? = null,
    @JsonProperty("phoneNumber") val phoneNumber: String? = null,
    @JsonProperty("bio") val bio: String? = null,

    @JsonProperty("gender") val gender: String? = null,
    @JsonProperty("dateOfBirth") val dateOfBirth: Instant? = null,
    @JsonProperty("location") val location: ProfileLocation? = null,

    @JsonProperty("createdAt") val createdAt: Instant,
    @JsonProperty("createdBy") val createdBy: String,
    @JsonProperty("updatedAt") val updatedAt: Instant,
    @JsonProperty("updatedBy") val updatedBy: String,
) {
    companion object {
        fun from(model: CreateProfileOutput) = fromProfile(model.profile)
        fun from(model: GetProfileOutput) = fromProfile(model.profile)
        fun from(model: Profile) = fromProfile(model)

        private fun fromProfile(model: Profile) =
            ProfileResponse(
                id = model.id,
                username = model.username,
                firstName = model.firstName,
                lastName = model.lastName,
                email = model.email,
                following = model.following,
                followers = model.followers,
                middleName = model.middleName,
                phoneNumber = model.phoneNumber,
                bio = model.bio,
                gender = model.gender?.value,
                dateOfBirth = model.dateOfBirth,
                location = model.location?.let { ProfileLocation.from(it) },
                createdAt = model.createdAt,
                createdBy = model.createdBy,
                updatedAt = model.updatedAt,
                updatedBy = model.updatedBy,
            )
    }
}
