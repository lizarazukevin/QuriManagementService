package com.quri.management.db.mongo.collections

import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.quri.management.db.mongo.MongoSchema.Collections.PROFILES
import com.quri.management.db.mongo.documents.ProfileDocument
import com.quri.server.model.CreateProfileInput
import com.quri.server.model.Profile
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Data access layer for the profiles collection in MongoDB.
 *
 * Handles all CRUD operations against the [ProfileDocument] collection and maps
 * persistence documents to Smithy-generated [Profile] models at the boundary.
 * No Smithy types leak into the persistence layer — mapping is handled internally
 *  * via [ProfileDocument.toSmithyModel].
 *
 *  @param dataStoreDatabase the MongoDB database instance injected by Spring
 */
@Component
class ProfileCollection(
    dataStoreDatabase: MongoDatabase
) {
    private val collection: MongoCollection<ProfileDocument> =
        dataStoreDatabase.getCollection(PROFILES, ProfileDocument::class.java)

    /**
     * Finds a single profile by its MongoDB [ObjectId].
     *
     * @param id the [ObjectId] of the profile to retrieve
     * @return the matching [Profile], or `null` if not found
     */
    suspend fun findById(id: ObjectId): Profile? {
        return collection.find(eq("_id", id)).firstOrNull()?.toSmithyModel()
    }

    /**
     * Persists a new profile document and returns it alongside its generated ID.
     *
     * @param input the [CreateProfileInput] containing personal user info
     * @return the persisted [Profile] with [Profile.profileId] populated, or `null` if
     * the insert did not return a generated ID
     */
    suspend fun create(input: CreateProfileInput): Profile? {
        val doc = ProfileDocument(
            username = input.username,
            firstName = input.firstName,
            lastName = input.lastName,
            email = input.email,
            phoneNumber = input.phoneNumber
        )
        val result = collection.insertOne(doc)
        val generatedId = result.insertedId?.asObjectId()?.value?.toString() ?: return null
        return doc.toSmithyModel().toBuilder()
            .profileId(generatedId)
            .createdAt(doc.createdAt)
            .updatedAt(doc.updatedAt)
            .build()
    }

    /**
     * Returns all profiles in the collection.
     *
     * @return list of all [Profile] documents mapped to Smithy models
     */
    suspend fun listAll(): List<Profile> {
        return collection.find().toList().map { it.toSmithyModel() }
    }

    /**
     * Deletes a profile by its [ObjectId].
     *
     * @param id the [ObjectId] of the profile to delete
     * @return the deleted [ObjectId] if the document was found and removed, `null` otherwise
     */
    suspend fun deleteById(id: ObjectId): ObjectId? {
        val result = collection.deleteOne(eq("_id", id))
        return id.takeIf { result.deletedCount == 1L }
    }

    private fun ProfileDocument.toSmithyModel(): Profile =
        Profile.builder()
            .profileId(id.toString())
            .username(username)
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .phoneNumber(phoneNumber)
            .build()
}