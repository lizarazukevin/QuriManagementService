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
        ) { "username must be $MIN_USERNAME_LENGTH-$MAX_USERNAME_LENGTH characters" }
        require(
            firstName.isNotBlank() &&
            firstName.length in MIN_NAME_LENGTH..MAX_NAME_LENGTH,
        ) { "firstName must be $MIN_NAME_LENGTH-$MAX_NAME_LENGTH characters" }
        require(
            lastName.isNotBlank() &&
            lastName.length in MIN_NAME_LENGTH..MAX_NAME_LENGTH,
        ) { "lastName must be $MIN_NAME_LENGTH-$MAX_NAME_LENGTH characters" }
        require(
                email.matches(Regex("""^[^@\s]+@[^@\s]+\.[^@\s]+$""")),
        ) { "email shape is invalid" }

        middleName?.let {
            require(
                it.isNotBlank() &&
                it.length in MIN_NAME_LENGTH..MAX_NAME_LENGTH,
            ) { "middleName must be $MIN_NAME_LENGTH-$MAX_NAME_LENGTH characters" }
        }
        phoneNumber?.let {
            require(it.matches(Regex("""^[+0-9()\-\s]{10,20}$"""))) {
                "phoneNumber shape is invalid"
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
        private const val MIN_NAME_LENGTH = 1
        private const val MAX_NAME_LENGTH = 50
    }
}
