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

    @JsonProperty("middleName") val middleName: String? = null,
    @JsonProperty("phoneNumber") val phoneNumber: String? = null,
) {
    init {
        require(
            username.isNotBlank() &&
                username.length in MIN_USERNAME_LENGTH..MAX_USERNAME_LENGTH,
        ) { "username is invalid" }
        require(
            firstName.isNotBlank() &&
                firstName.length <= MAX_NAME_LENGTH,
        ) { "firstName is invalid" }
        require(
            lastName.isNotBlank() &&
                lastName.length <= MAX_NAME_LENGTH,
        ) { "lastName is invalid" }
        require(
            email.isNotBlank() &&
                email.matches(Regex("""^[^@\s]+@[^@\s]+\.[^@\s]+$""")),
        ) { "email is invalid" }

        middleName?.let {
            require(
                it.isNotBlank() &&
                        middleName.length <= MAX_NAME_LENGTH
            ) { "middleName is invalid" }
        }
        phoneNumber?.let {
            require(it.matches(Regex("""^[+0-9()\-\s]{10,20}$"""))) {
                "phoneNumber is invalid"
            }
        }
    }

    fun toSmithyInput(): CreateProfileInput =
        CreateProfileInput.builder()
            .username(username)
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .middleName(middleName)
            .phoneNumber(phoneNumber)
            .build()

    companion object {
        private const val MIN_USERNAME_LENGTH = 3
        private const val MAX_USERNAME_LENGTH = 30
        private const val MAX_NAME_LENGTH = 50
    }
}
