package com.quri.management.fixtures.models

import com.quri.client.model.CreateProfileInput
import com.quri.client.model.DeleteProfileInput
import com.quri.client.model.Gender
import com.quri.client.model.GetProfileInput
import com.quri.client.model.Profile
import com.quri.client.model.UpdateProfileInput
import com.quri.client.model.UserLocation
import org.bson.types.ObjectId
import java.time.Instant

object ProfileFixtures {

    val DEFAULT_PROFILE_ID = ObjectId().toString()
    const val DEFAULT_OWNER_ID = "owner-1"
    const val DEFAULT_USER_ID = "user-1"

    fun aProfile(
        id: String = DEFAULT_PROFILE_ID,
        username: String = "testuser",
        firstName: String = "Test",
        lastName: String = "User",
        email: String = "test@quri.com",
        dateOfBirth: Instant = Instant.parse("1995-04-15T00:00:00Z"),
        createdAt: Instant = Instant.parse("2024-01-01T00:00:00Z"),
        createdBy: String = DEFAULT_USER_ID,
        updatedAt: Instant = Instant.parse("2024-01-01T00:00:00Z"),
        updatedBy: String = DEFAULT_USER_ID,
    ): Profile =
        Profile.builder()
            .id(id)
            .username(username)
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .dateOfBirth(dateOfBirth)
            .createdAt(createdAt)
            .createdBy(createdBy)
            .updatedAt(updatedAt)
            .updatedBy(updatedBy)
            .build()

    fun aCreateProfileInput(
        username: String = "testuser",
        firstName: String = "Test",
        lastName: String = "User",
        email: String = "test@quri.com",
        dateOfBirth: Instant = Instant.parse("1995-04-15T00:00:00Z"),
        middleName: String? = null,
        phoneNumber: String? = null,
    ): CreateProfileInput =
        CreateProfileInput.builder()
            .username(username)
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .dateOfBirth(dateOfBirth)
            .apply { middleName?.let { middleName(it) } }
            .apply { phoneNumber?.let { phoneNumber(phoneNumber) } }
            .build()

    fun aGetProfileInput(profileId: String = DEFAULT_PROFILE_ID): GetProfileInput =
        GetProfileInput.builder()
            .profileId(profileId)
            .build()

    fun aDeleteProfileInput(profileId: String = DEFAULT_PROFILE_ID): DeleteProfileInput =
        DeleteProfileInput.builder()
            .profileId(profileId)
            .build()

    @Suppress("CyclomaticComplexMethod")
    fun anUpdateProfileInput(
        profileId: String = DEFAULT_PROFILE_ID,
        username: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        middleName: String? = null,
        phoneNumber: String? = null,
        bio: String? = null,
        following: List<String>? = null,
        followers: List<String>? = null,
        gender: Gender? = null,
        location: UserLocation? = null,
    ): UpdateProfileInput =
        UpdateProfileInput.builder()
            .profileId(profileId)
            .apply { username?.let { username(it) } }
            .apply { firstName?.let { firstName(it) } }
            .apply { lastName?.let { lastName(it) } }
            .apply { email?.let { email(it) } }
            .apply { middleName?.let { middleName(it) } }
            .apply { phoneNumber?.let { phoneNumber(it) } }
            .apply { bio?.let { bio(it) } }
            .apply { following?.let { following(it) } }
            .apply { followers?.let { followers(it) } }
            .apply { gender?.let { gender(it) } }
            .apply { location?.let { location(it) } }
            .build()
}
