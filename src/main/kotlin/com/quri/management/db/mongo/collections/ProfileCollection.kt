package com.quri.management.db.mongo.collections

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import com.mongodb.client.model.Updates.combine
import com.mongodb.client.model.Updates.set
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.quri.client.model.CreateProfileInput
import com.quri.client.model.Profile
import com.quri.client.model.UpdateProfileInput
import com.quri.management.db.mongo.MongoSchema.Collections.PROFILES
import com.quri.management.db.mongo.documents.ProfileDocument
import com.quri.management.db.mongo.paginate
import kotlinx.coroutines.flow.firstOrNull
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Data access layer for the profiles collection in MongoDB.
 *
 * Handles all CRUD operations against the [ProfileDocument] collection and maps
 * persistence documents to Smithy-generated [Profile] models at the boundary.
 * No Smithy types leak into the persistence layer — mapping is handled internally
 *  * via [ProfileDocument.toApi].
 *
 *  @param dataStoreDatabase the MongoDB database instance injected by Spring
 */
@Component
class ProfileCollection(dataStoreDatabase: MongoDatabase) {
    private val collection: MongoCollection<ProfileDocument> =
        dataStoreDatabase.getCollection(PROFILES, ProfileDocument::class.java)

    /**
     * Finds a single profile by its MongoDB [ObjectId].
     *
     * @param id the [ObjectId] of the profile to retrieve
     * @return the matching [Profile], or `null` if not found
     */
    suspend fun findById(id: ObjectId): Profile? = collection.find(eq("_id", id)).firstOrNull()?.toApi()

    /**
     * Persists a new profile document and returns it alongside its generated ID.
     *
     * @param input the [CreateProfileInput] containing mutable user information
     * @param ownerId the owning entity
     * @return the persisted [Profile] with [Profile.id] populated, or `null` if
     * the insert did not return a generated ID
     */
    suspend fun create(
        input: CreateProfileInput,
        ownerId: String,
    ): Profile? {
        val doc = ProfileDocument.from(input, ownerId)
        val result = collection.insertOne(doc)
        val generatedId = result.insertedId?.asObjectId()?.value?.toString() ?: return null
        return doc.toApi(generatedId)
    }

    /**
     * Returns a paginated list of all profiles in the collection in ascending order by ID.
     *
     * @param pageSize is the maximum results per page
     * @param nextToken is the bookmarked ID the next paginated list starts from
     *
     * @return pair of all [Profile] documents mapped to Smithy models and nullable pagination token
     */
    suspend fun listAll(
        pageSize: Int,
        nextToken: String?,
    ): Pair<List<Profile>, String?> =
        paginate(collection, pageSize, nextToken) { it.id }
            .let { (docs, token) -> docs.map { it.toApi() } to token }

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

    /**
     * Determines if an entry in the collection exists with the given criteria.
     *
     * @param filter the query filter to match against (e.g., `eq("email", value)`)
     * @return true if at least one document matches the filter, false otherwise
     */
    suspend fun exists(filter: Bson): Boolean = collection.find(filter).limit(1).firstOrNull() != null

    /**
     * Updates a profile by its [ObjectId].
     *
     * Combines a list of conditional updates into a single update,
     * auditing the actor behind the modification.
     *
     * @param input the [UpdateProfileInput] contents for updating a profile
     * @param userId actor behind update
     * @return updated [Profile] document, `null` if update was not successful.
     */
    suspend fun update(
        input: UpdateProfileInput,
        userId: String,
    ): Profile? {
        val filter = eq("_id", ObjectId(input.id))
        val updates = buildUpdates(input, userId)
        val options = FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        return collection.findOneAndUpdate(filter, combine(updates), options)?.toApi()
    }

    private fun buildUpdates(
        input: UpdateProfileInput,
        userId: String,
    ): List<Bson> {
        val updates = mutableListOf<Bson>()

        input.username?.let { updates.add(set("username", it)) }
        input.firstName?.let { updates.add(set("firstName", it)) }
        input.lastName?.let { updates.add(set("lastName", it)) }
        input.email?.let { updates.add(set("email", it)) }
        input.middleName?.let { updates.add(set("middleName", it)) }
        input.phoneNumber?.let { updates.add(set("phoneNumber", it)) }
        input.bio?.let { updates.add(set("bio", it)) }
        if (input.hasFollowing()) {
            updates.add(set("following", input.following.map(::ObjectId)))
        }
        if (input.hasFollowers()) {
            updates.add(set("followers", input.followers.map(::ObjectId)))
        }
        input.gender?.let { updates.add(set("gender", it.value)) }
        input.location?.let { updates.add(set("location", it)) }

        updates.add(set("updatedBy", userId))
        updates.add(set("updatedAt", Instant.now()))
        return updates
    }
}
