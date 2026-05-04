package com.quri.management.db.mongo.documents

import com.quri.client.model.Gender
import com.quri.client.model.Profile
import com.quri.management.shared.models.ProfileLocation
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.Instant

/**
 * Persistence document for a user profile in MongoDB.
 *
 * @see Profile
 */
data class ProfileDocument(
    @BsonId val id: ObjectId = ObjectId(),
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,

    val following: List<String>? = emptyList(),
    val followers: List<String>? = emptyList(),
    val middleName: String? = null,
    val phoneNumber: String? = null,
    val bio: String? = null,
    val gender: String? = null,
    val dateOfBirth: Instant? = null,
    val location: ProfileLocation? = null,

    val createdBy: String,
    val createdAt: Instant,
    val updatedBy: String,
    val updatedAt: Instant,
) {
    fun toSmithyModel(): Profile =
        Profile.builder()
            .id(id.toString())
            .username(username)
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .following(following)
            .followers(followers)
            .middleName(middleName)
            .phoneNumber(phoneNumber)
            .bio(bio)
            .gender(gender?.let { Gender.from(it) })
            .dateOfBirth(dateOfBirth)
            .location(location?.toSmithyModel())
            .createdBy(createdBy)
            .createdAt(createdAt)
            .updatedBy(updatedBy)
            .updatedAt(updatedAt)
            .build()
}
