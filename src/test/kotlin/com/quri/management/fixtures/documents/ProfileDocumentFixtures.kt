package com.quri.management.fixtures.documents

import com.quri.client.model.UserLocation
import com.quri.management.db.mongo.documents.ProfileDocument
import org.bson.types.ObjectId
import java.time.Instant

object ProfileDocumentFixtures {

    val DEFAULT_ID = ObjectId()
    const val DEFAULT_OWNER_ID = "owner-1"
    const val DEFAULT_USER_ID = "user-1"
    val DEFAULT_INSTANT: Instant = Instant.parse("2024-01-01T00:00:00Z")

    fun aProfileDocument(
        id: ObjectId = DEFAULT_ID,
        username: String = "testuser",
        firstName: String = "Test",
        lastName: String = "User",
        email: String = "test@quri.com",
        dateOfBirth: Instant? = Instant.parse("1995-04-15T00:00:00Z"),
        middleName: String? = null,
        phoneNumber: String? = null,
        bio: String? = null,
        following: List<ObjectId>? = emptyList(),
        followers: List<ObjectId>? = emptyList(),
        gender: String? = null,
        location: UserLocation? = null,
        createdBy: String = DEFAULT_OWNER_ID,
        createdAt: Instant = DEFAULT_INSTANT,
        updatedBy: String = DEFAULT_USER_ID,
        updatedAt: Instant = DEFAULT_INSTANT,
    ): ProfileDocument = ProfileDocument(
        id = id,
        username = username,
        firstName = firstName,
        lastName = lastName,
        email = email,
        dateOfBirth = dateOfBirth,
        middleName = middleName,
        phoneNumber = phoneNumber,
        bio = bio,
        following = following,
        followers = followers,
        gender = gender,
        location = location,
        createdBy = createdBy,
        createdAt = createdAt,
        updatedBy = updatedBy,
        updatedAt = updatedAt,
    )
}
