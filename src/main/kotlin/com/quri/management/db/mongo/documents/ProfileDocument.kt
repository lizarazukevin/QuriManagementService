package com.quri.management.db.mongo.documents

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.Instant

/**
 * Persistence document for a user profile in MongoDB.
 *
 * @param username unique identifier chosen by the user
 * @param firstName user's first name
 * @param lastName user's last name
 * @param email user's email address
 * @param phoneNumber user's phone number
 */
data class ProfileDocument(
    @BsonId val id: ObjectId = ObjectId(),
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)