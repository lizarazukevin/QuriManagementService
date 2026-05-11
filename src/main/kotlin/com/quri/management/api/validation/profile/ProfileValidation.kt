package com.quri.management.api.validation.profile

import com.quri.management.api.validation.validateLength
import com.quri.management.api.validation.validateObjectIdList
import com.quri.management.api.validation.validatePattern
import com.quri.management.api.validation.validateTimestamp
import java.time.Instant

object ProfileValidation {
    const val MIN_USERNAME_LENGTH = 3
    const val MAX_USERNAME_LENGTH = 30
    const val MIN_NAME_LENGTH = 1
    const val MAX_NAME_LENGTH = 50
    const val MIN_BIO_LENGTH = 1
    const val MAX_BIO_LENGTH = 150

    fun validateUsername(
        field: String,
        username: String,
    ) = username.validateLength("$field.username", MIN_USERNAME_LENGTH, MAX_USERNAME_LENGTH)

    fun validateName(
        field: String,
        name: String,
    ) = name.validateLength("$field.name", MIN_NAME_LENGTH, MAX_NAME_LENGTH)

    fun validateEmail(
        field: String,
        email: String,
    ) = email.validatePattern("$field.email", Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"), "Invalid email format")

    fun validatePhoneNumber(
        field: String,
        phoneNumber: String,
    ) = phoneNumber.validatePattern("$field.phoneNumber", Regex("^[+0-9()\\-\\s]{10,20}$"), "Invalid phone number")

    fun validateDateOfBirth(
        field: String,
        dateOfBirth: Instant,
    ) = dateOfBirth.validateTimestamp("$field.dateOfBirth")

    fun validateBio(
        field: String,
        bio: String,
    ) = bio.validateLength("$field.bio", MIN_BIO_LENGTH, MAX_BIO_LENGTH)

    fun validateUserIdList(
        field: String,
        userIds: List<String>,
    ) = userIds.validateObjectIdList(field)
}
