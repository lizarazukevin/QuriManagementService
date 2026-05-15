package com.quri.management.api.validation.profile

import com.quri.client.model.UserLocation
import com.quri.management.api.validation.model.UserLocationValidator
import com.quri.management.api.validation.validateLength
import com.quri.management.api.validation.validateObjectIdList
import com.quri.management.api.validation.validatePattern
import com.quri.management.api.validation.validateTimestamp
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ProfileFieldsValidator(private val userLocationValidator: UserLocationValidator) {
    suspend fun validate(
        field: String,
        username: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        dateOfBirth: Instant? = null,
        middleName: String? = null,
        phoneNumber: String? = null,
        bio: String? = null,
        following: List<String>? = null,
        followers: List<String>? = null,
        location: UserLocation? = null,
    ) {
        username?.validateLength("$field.username", MIN_USERNAME_LENGTH, MAX_USERNAME_LENGTH)
        firstName?.validateLength("$field.firstName", MIN_NAME_LENGTH, MAX_NAME_LENGTH)
        lastName?.validateLength("$field.lastName", MIN_NAME_LENGTH, MAX_NAME_LENGTH)
        email?.validatePattern("$field.email", Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"), "invalid email format")
        dateOfBirth?.validateTimestamp("$field.dateOfBirth")
        middleName?.validateLength("$field.middleName", MIN_NAME_LENGTH, MAX_NAME_LENGTH)
        phoneNumber?.validatePattern("$field.phoneNumber", Regex("^[+0-9()\\-\\s]{10,20}$"), "invalid phone number")
        bio?.validateLength("$field.bio", MIN_BIO_LENGTH, MAX_BIO_LENGTH)
        following?.validateObjectIdList("$field.following")
        followers?.validateObjectIdList("$field.followers")
        location?.let { userLocationValidator.validate("$field.userLocation", it) }
    }

    companion object {
        private const val MIN_USERNAME_LENGTH = 3
        private const val MAX_USERNAME_LENGTH = 30
        private const val MIN_NAME_LENGTH = 1
        private const val MAX_NAME_LENGTH = 50
        private const val MIN_BIO_LENGTH = 1
        private const val MAX_BIO_LENGTH = 150
    }
}
