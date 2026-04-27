package com.quri.management.validators.inputs.profiles

import com.fasterxml.jackson.annotation.JsonProperty
import com.quri.client.model.CreateProfileInput

/**
 * Maps this request to a Smithy [CreateProfileInput].
 */
data class CreateProfileInputRequest(
    @JsonProperty("username") val username: String? = null,
    @JsonProperty("firstName") val firstName: String? = null,
    @JsonProperty("lastName") val lastName: String? = null,
    @JsonProperty("email") val email: String? = null,
    @JsonProperty("phoneNumber") val phoneNumber: String? = null,
) {
    fun toSmithyModel(): CreateProfileInput =
        CreateProfileInput.builder()
            .username(username)
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .phoneNumber(phoneNumber)
            .build()
}
