package com.quri.management.api.inputs.profiles

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.CreateProfileInput

/**
 * Maps this request to a Smithy [CreateProfileInput].
 */
data class CreateProfileInputRequest(
    @JsonProperty("username") val username: String,
    @JsonProperty("firstName") val firstName: String,
    @JsonProperty("lastName") val lastName: String,
    @JsonProperty("email") val email: String,

    @JsonProperty("phoneNumber") val phoneNumber: String? = null,
) {
    fun toSmithyInput(): CreateProfileInput =
        CreateProfileInput.builder()
            .username(username)
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .phoneNumber(phoneNumber)
            .build()
}
