package com.quri.management.db.mongo.documents

import com.quri.client.model.Profile
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

    val phoneNumber: String? = null,

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
            .phoneNumber(phoneNumber)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .updatedBy(updatedBy)
            .updatedAt(updatedAt)
            .build()
}
